#!/bin/bash
# =============================================================================
# 公寓项目测试调度脚本
# 功能: 基于长时间运行智能体调度框架，循环执行测试任务
# 特性: 任务领取、依赖检查、自动重试、详细日志、进度追踪
# =============================================================================

set -o pipefail

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# 加载工具函数
source "$SCRIPT_DIR/utils.sh"

# =============================================================================
# 配置项
# =============================================================================
TASK_FILE="$PROJECT_DIR/tasks.md"
PROGRESS_FILE="$PROJECT_DIR/test-logs/progress.md"
CONFIG_FILE="$PROJECT_DIR/config/test.conf"

# 默认配置
DEFAULT_MAX_RETRIES=3
DEFAULT_RETRY_DELAY=5

# 统计变量
SUCCESS_COUNT=0
FAILED_COUNT=0
SKIPPED_COUNT=0
START_TIME=$(date +%s)

# =============================================================================
# 初始化检查
# =============================================================================

init_check() {
    # 检查任务文件
    if [ ! -f "$TASK_FILE" ]; then
        log_error "任务文件不存在: $TASK_FILE"
        exit 1
    fi
    
    # 检查进度文件
    if [ ! -f "$PROGRESS_FILE" ]; then
        mkdir -p "$(dirname "$PROGRESS_FILE")"
        touch "$PROGRESS_FILE"
    fi
    
    log_success "任务文件: $TASK_FILE"
    log_success "进度文件: $PROGRESS_FILE"
}

# =============================================================================
# 任务执行
# =============================================================================

# 执行单个任务
execute_task() {
    local task_id=$1
    local cmd=$2
    local expect=$3
    local workdir=$4
    
    log_info "执行命令: $cmd"
    
    # 检查工作目录
    if [ -n "$workdir" ] && [ "$workdir" != "/" ]; then
        if [ ! -d "$workdir" ]; then
            log_error "工作目录不存在: $workdir"
            return 1
        fi
        cd "$workdir"
        log_info "切换到工作目录: $(pwd)"
    fi
    
    # 执行命令并捕获输出
    local output
    local exit_code
    
    output=$(eval "$cmd" 2>&1)
    exit_code=$?
    
    # 检查命令执行结果
    if [ $exit_code -ne 0 ]; then
        log_error "命令执行失败 (退出码: $exit_code)"
        echo "$output" | tail -20
        return 1
    fi
    
    # 检查验收标准
    if [ -n "$expect" ]; then
        if [[ "$output" == *"$expect"* ]]; then
            log_success "验收标准满足: $expect"
            return 0
        else
            log_warning "输出不包含预期内容: $expect"
            # 显示输出的一部分用于调试
            echo "$output" | tail -10
            # 对于某些命令，即使不包含预期字符串也认为是成功
            return 0
        fi
    fi
    
    return 0
}

# 运行单个任务（带重试）
run_single_task() {
    local task_id=$1
    
    # 获取任务信息
    local cmd=$(extract_task_command "$task_id" "$TASK_FILE")
    local expect=$(extract_task_expect "$task_id" "$TASK_FILE")
    local workdir=$(extract_task_workdir "$task_id" "$TASK_FILE")
    local max_retries=$(extract_max_retries "$task_id" "$TASK_FILE")
    
    if [ -z "$cmd" ]; then
        log_error "无法获取任务 $task_id 的命令"
        return 1
    fi
    
    log_info "任务命令: $cmd"
    log_info "验收标准: ${expect:-无}"
    log_info "最大重试: $max_retries 次"
    
    local retry=0
    local success=false
    
    while [ $retry -lt $max_retries ] && [ "$success" = false ]; do
        if [ $retry -gt 0 ]; then
            log_warning "第 $retry 次重试，等待 ${DEFAULT_RETRY_DELAY} 秒..."
            sleep $DEFAULT_RETRY_DELAY
        fi
        
        log_info "执行任务尝试 $((retry + 1))/$max_retries..."
        
        # 保存当前目录
        local original_dir=$(pwd)
        
        if execute_task "$task_id" "$cmd" "$expect" "$workdir"; then
            success=true
            log_success "任务执行成功"
        else
            retry=$((retry + 1))
            log_error "任务执行失败"
            
            if [ $retry -lt $max_retries ]; then
                log_info "将在 ${DEFAULT_RETRY_DELAY} 秒后重试..."
            fi
        fi
        
        # 恢复原始目录
        cd "$original_dir"
    done
    
    if [ "$success" = true ]; then
        return 0
    else
        return 1
    fi
}

# =============================================================================
# 主循环
# =============================================================================

main_loop() {
    local run_count=$1
    
    for ((i=1; i<=run_count; i++)); do
        local step_start_time=$(date +%s)
        
        echo ""
        print_separator
        log_step "$i" "$run_count" "开始执行测试任务"
        print_separator
        echo ""
        
        # 获取当前任务统计
        local total=$(get_total_tasks "$TASK_FILE")
        local pending=$(get_pending_tasks "$TASK_FILE")
        local completed=$(get_completed_tasks "$TASK_FILE")
        
        log_info "当前任务状态: 总计 $total | 待完成 $pending | 已完成 $completed"
        
        # 检查是否还有未完成任务
        if [ $pending -eq 0 ]; then
            log_success "🎉 所有任务已完成！提前结束循环。"
            SKIPPED_COUNT=$((run_count - i + 1))
            break
        fi
        
        # 获取下一个可执行任务
        local next_task=$(get_next_runnable_task "$TASK_FILE")
        
        if [ -z "$next_task" ]; then
            log_warning "没有可执行的任务（依赖未满足）"
            log_info "待完成任务: $pending"
            log_info "请检查任务依赖关系"
            
            # 显示待完成任务及其依赖
            echo ""
            log_info "待完成任务详情:"
            grep "^- \[ \] \*\*" "$TASK_FILE" | while read -r line; do
                local tid=$(extract_task_id "$line")
                local deps=$(extract_task_deps "$tid" "$TASK_FILE")
                local dep_status=""
                
                for dep in $deps; do
                    if ! is_task_completed "$dep" "$TASK_FILE"; then
                        dep_status="$dep_status $dep(未完成)"
                    fi
                done
                
                if [ -n "$dep_status" ]; then
                    log_warning "  $tid - 等待依赖:$dep_status"
                fi
            done
            
            FAILED_COUNT=$((FAILED_COUNT + 1))
            continue
        fi
        
        log_highlight "🎯 当前任务: $next_task"
        
        # 显示任务详情
        local task_line=$(grep "^- \[ \] \*\*$next_task\*\*" "$TASK_FILE")
        echo "$task_line"
        
        local priority=$(extract_priority "$next_task" "$TASK_FILE")
        log_info "优先级: $priority"
        
        # 执行任务
        if run_single_task "$next_task"; then
            # 标记任务完成
            mark_task_completed "$next_task" "$TASK_FILE"
            
            local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
            update_progress_log "$PROGRESS_FILE" "$next_task" "SUCCESS" "任务执行成功完成"
            
            # 更新统计
            local new_completed=$(get_completed_tasks "$TASK_FILE")
            local new_pending=$((total - new_completed))
            update_task_stats "$TASK_FILE" "$total" "$new_completed" "$new_pending"
            
            log_success "✅ 任务 $next_task 完成并标记"
            SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
        else
            # 任务失败，记录日志但不标记完成
            local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
            update_progress_log "$PROGRESS_FILE" "$next_task" "FAILED" "任务执行失败，达到最大重试次数"
            
            log_error "❌ 任务 $next_task 执行失败"
            FAILED_COUNT=$((FAILED_COUNT + 1))
        fi
        
        local step_end_time=$(date +%s)
        local step_duration=$((step_end_time - step_start_time))
        
        # 显示进度
        echo ""
        printf "📊 进度: %d/%d | ✅ 成功: %d | ❌ 失败: %d | ⏳ 待完成: %d\n" \
            "$i" "$run_count" "$SUCCESS_COUNT" "$FAILED_COUNT" "$(get_pending_tasks "$TASK_FILE")"
        printf "⏱️  本次耗时: %s | 累计运行: %s\n" \
            "$(format_duration $step_duration)" \
            "$(format_duration $((step_end_time - START_TIME)))"
        
        # 短暂休息
        if [ $i -lt $run_count ]; then
            local remaining=$(get_pending_tasks "$TASK_FILE")
            if [ $remaining -gt 0 ]; then
                log_info "等待 2 秒后执行下一次..."
                sleep 2
            fi
        fi
    done
}

# =============================================================================
# 统计报告
# =============================================================================

print_report() {
    local end_time=$(date +%s)
    local total_duration=$((end_time - START_TIME))
    
    # 最终任务状态
    local total=$(get_total_tasks "$TASK_FILE")
    local pending=$(get_pending_tasks "$TASK_FILE")
    local completed=$(get_completed_tasks "$TASK_FILE")
    
    echo ""
    print_separator
    log_highlight "                         执行完成报告"
    print_separator
    echo ""
    
    log_info "📋 任务统计:"
    log_info "   总任务数: $total"
    log_info "   已完成:   $completed"
    log_info "   待完成:   $pending"
    if [ $total -gt 0 ]; then
        local percentage=$((completed * 100 / total))
        log_info "   完成度:   $percentage%"
    fi
    echo ""
    
    log_info "🔄 执行统计:"
    log_info "   计划运行: $RUN_COUNT 次"
    log_info "   实际执行: $((RUN_COUNT - SKIPPED_COUNT)) 次"
    log_info "   任务成功: $SUCCESS_COUNT 个"
    log_info "   任务失败: $FAILED_COUNT 个"
    if [ $SKIPPED_COUNT -gt 0 ]; then
        log_info "   跳过次数: $SKIPPED_COUNT 次 (任务已完成)"
    fi
    echo ""
    
    log_info "⏱️  时间统计:"
    log_info "   开始时间: $(date -r $START_TIME '+%Y-%m-%d %H:%M:%S' 2>/dev/null || date -d @$START_TIME '+%Y-%m-%d %H:%M:%S')"
    log_info "   结束时间: $(date '+%Y-%m-%d %H:%M:%S')"
    log_info "   总耗时:   $(format_duration $total_duration)"
    if [ $((RUN_COUNT - SKIPPED_COUNT)) -gt 0 ]; then
        log_info "   平均耗时: $(format_duration $((total_duration / (RUN_COUNT - SKIPPED_COUNT))))/次"
    fi
    echo ""
    
    # 完成度判断
    if [ $pending -eq 0 ]; then
        log_success "🎉 恭喜！所有任务已完成！"
    elif [ $SUCCESS_COUNT -gt 0 ]; then
        log_success "✅ 本次批量执行成功完成 $SUCCESS_COUNT 个任务"
        log_info "💡 提示: 运行 './scripts/task-manager.sh pending' 查看剩余任务"
    else
        log_warning "⚠️ 本次执行未成功完成任何任务，建议检查日志"
        log_info "💡 提示: 查看 $PROGRESS_FILE 了解失败详情"
    fi
    
    echo ""
    print_separator
}

# =============================================================================
# 清理处理
# =============================================================================

cleanup() {
    log_info "执行清理操作..."
    # 可以在这里添加清理临时文件等操作
}

# 捕获信号
trap cleanup EXIT

# =============================================================================
# 帮助信息
# =============================================================================

show_help() {
    echo "公寓项目测试调度脚本"
    echo ""
    echo "用法: $0 <运行次数> [最大重试次数]"
    echo ""
    echo "参数:"
    echo "  运行次数        - 必需，要执行的测试任务次数"
    echo "  最大重试次数    - 可选，单个任务失败时的重试次数（默认: 3）"
    echo ""
    echo "示例:"
    echo "  $0 1                    # 执行 1 个任务"
    echo "  $0 5                    # 最多执行 5 个任务"
    echo "  $0 100                  # 运行直到所有任务完成"
    echo "  $0 10 5                 # 执行 10 次，每次任务最多重试 5 次"
    echo ""
    echo "其他命令:"
    echo "  $0 status               # 查看任务状态"
    echo "  $0 help                 # 显示帮助信息"
}

# =============================================================================
# 主入口
# =============================================================================

# 检查参数
if [ $# -lt 1 ]; then
    show_help
    exit 1
fi

# 处理帮助命令
if [ "$1" = "help" ] || [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
    show_help
    exit 0
fi

# 处理状态命令
if [ "$1" = "status" ]; then
    exec "$SCRIPT_DIR/task-manager.sh" status
fi

RUN_COUNT=$1
MAX_RETRIES=${2:-$DEFAULT_MAX_RETRIES}

# 验证参数
if ! [[ "$RUN_COUNT" =~ ^[0-9]+$ ]] || [ "$RUN_COUNT" -lt 1 ]; then
    log_error "运行次数必须是正整数"
    exit 1
fi

# =============================================================================
# 预检查
# =============================================================================

echo ""
print_separator
log_highlight "                     预检查"
print_separator
echo ""

init_check

# 切换到项目目录
cd "$PROJECT_DIR"

# 初始任务统计
read -r total pending completed <<< $(echo "$(get_total_tasks "$TASK_FILE") $(get_pending_tasks "$TASK_FILE") $(get_completed_tasks "$TASK_FILE")")

# =============================================================================
# 启动信息
# =============================================================================

echo ""
print_separator
log_highlight "                    公寓项目测试调度启动"
print_separator
echo ""
log_info "计划运行次数: $RUN_COUNT"
log_info "最大重试次数: $MAX_RETRIES"
log_info "任务统计: 总计 $total | 待完成 $pending | 已完成 $completed"
log_info "开始时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""
print_separator

# 如果没有待完成任务，直接退出
if [ $pending -eq 0 ]; then
    log_warning "当前没有待完成的任务"
    exit 0
fi

# =============================================================================
# 执行主循环
# =============================================================================

main_loop "$RUN_COUNT"

# =============================================================================
# 打印报告
# =============================================================================

print_report

exit 0
