#!/bin/bash
# =============================================================================
# 任务管理工具
# 用于管理测试任务列表
#
# 使用方法:
#   ./task-manager.sh status     # 查看任务状态
#   ./task-manager.sh list       # 列出所有任务
#   ./task-manager.sh pending    # 列出待完成任务
#   ./task-manager.sh next       # 显示下一个可执行任务
#   ./task-manager.sh complete <task_id>  # 标记任务完成
#   ./task-manager.sh reset <task_id>     # 重置任务状态
#   ./task-manager.sh reset-all  # 重置所有任务
# =============================================================================

set -e

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# 加载工具函数
source "$SCRIPT_DIR/utils.sh"

# 配置文件
TASK_FILE="$PROJECT_DIR/tasks.md"
PROGRESS_FILE="$PROJECT_DIR/test-logs/progress.md"

# 检查任务文件
if [ ! -f "$TASK_FILE" ]; then
    log_error "任务文件不存在: $TASK_FILE"
    exit 1
fi

# =============================================================================
# 命令处理
# =============================================================================

cmd_status() {
    print_separator
    log_highlight "                     任务状态概览"
    print_separator
    echo ""
    
    local total=$(get_total_tasks "$TASK_FILE")
    local completed=$(get_completed_tasks "$TASK_FILE")
    local pending=$(get_pending_tasks "$TASK_FILE")
    
    log_info "📊 任务统计:"
    log_info "   总任务数: $total"
    log_info "   已完成:   $completed"
    log_info "   待完成:   $pending"
    
    if [ "$total" -gt 0 ] 2>/dev/null; then
        local percentage=$((completed * 100 / total))
        log_info "   完成度:   $percentage%"
        
        # 进度条
        local filled=$((percentage / 5))
        local empty=$((20 - filled))
        local bar=""
        for ((i=0; i<filled; i++)); do bar="${bar}█"; done
        for ((i=0; i<empty; i++)); do bar="${bar}░"; done
        log_info "   进度:     [$bar]"
    fi
    
    echo ""
    
    # 显示下一个可执行任务
    local next_task=$(get_next_runnable_task "$TASK_FILE")
    if [ -n "$next_task" ]; then
        log_info "🎯 下一个可执行任务: $next_task"
        local cmd=$(extract_task_command "$next_task" "$TASK_FILE")
        log_info "   命令: $cmd"
    else
        if [ "$pending" -eq 0 ] 2>/dev/null; then
            log_success "🎉 所有任务已完成！"
        else
            log_warning "⚠️  有 $pending 个任务待完成，但依赖关系不满足"
        fi
    fi
    
    echo ""
    print_separator
}

cmd_list() {
    print_separator
    log_highlight "                     所有任务列表"
    print_separator
    echo ""
    
    local in_task=0
    local task_info=""
    
    while IFS= read -r line; do
        # 检测任务行
        if [[ "$line" =~ ^-\ \[[\ xX]\]\ \*\*([^*]+)\*\* ]]; then
            # 输出之前的任务信息
            if [ $in_task -eq 1 ] && [ -n "$task_info" ]; then
                echo "$task_info"
                echo ""
            fi
            
            in_task=1
            task_info="$line"
            
            # 高亮显示
            if [[ "$line" =~ ^-\ \[[xX]\] ]]; then
                echo -e "${GREEN}$line${NC}"
            else
                echo -e "${YELLOW}$line${NC}"
            fi
        elif [ $in_task -eq 1 ] && [[ "$line" =~ ^[[:space:]]+- ]]; then
            # 任务详情行
            echo "  $line"
        elif [[ "$line" =~ ^## ]]; then
            # 阶段标题
            if [ $in_task -eq 1 ] && [ -n "$task_info" ]; then
                echo ""
            fi
            in_task=0
            task_info=""
            echo ""
            log_highlight "$line"
        fi
    done < "$TASK_FILE"
    
    echo ""
    print_separator
}

cmd_pending() {
    print_separator
    log_highlight "                     待完成任务列表"
    print_separator
    echo ""
    
    grep "^- \[ \] \*\*" "$TASK_FILE" 2>/dev/null | while read -r line; do
        local task_id=$(extract_task_id "$line")
        local priority=$(extract_priority "$task_id" "$TASK_FILE")
        local deps=$(extract_task_deps "$task_id" "$TASK_FILE")
        
        # 检查依赖是否满足
        local dep_status=""
        if [ -n "$deps" ]; then
            local all_met=true
            for dep in $deps; do
                if ! is_task_completed "$dep" "$TASK_FILE" 2>/dev/null; then
                    all_met=false
                    break
                fi
            done
            
            if [ "$all_met" = true ]; then
                dep_status="${GREEN}[依赖满足]${NC}"
            else
                dep_status="${RED}[等待依赖]${NC}"
            fi
        else
            dep_status="${GREEN}[可执行]${NC}"
        fi
        
        echo -e "${YELLOW}$line${NC}"
        echo -e "  优先级: $priority $dep_status"
        if [ -n "$deps" ]; then
            echo -e "  依赖: $deps"
        fi
        echo ""
    done
    
    print_separator
}

cmd_next() {
    local next_task=$(get_next_runnable_task "$TASK_FILE")
    
    if [ -z "$next_task" ]; then
        log_warning "没有可执行的任务"
        local pending=$(get_pending_tasks "$TASK_FILE")
        if [ $pending -gt 0 ]; then
            log_info "有 $pending 个任务待完成，但依赖关系不满足"
            log_info "请检查 tasks.md 中的依赖配置"
        else
            log_success "所有任务已完成！"
        fi
        exit 0
    fi
    
    print_separator
    log_highlight "                     下一个可执行任务"
    print_separator
    echo ""
    
    log_info "任务ID: $next_task"
    
    local priority=$(extract_priority "$next_task" "$TASK_FILE")
    log_info "优先级: $priority"
    
    local cmd=$(extract_task_command "$next_task" "$TASK_FILE")
    log_info "命令: $cmd"
    
    local expect=$(extract_task_expect "$next_task" "$TASK_FILE")
    log_info "验收标准: $expect"
    
    local workdir=$(extract_task_workdir "$next_task" "$TASK_FILE")
    if [ -n "$workdir" ] && [ "$workdir" != "/" ]; then
        log_info "工作目录: $workdir"
    fi
    
    local deps=$(extract_task_deps "$next_task" "$TASK_FILE")
    if [ -n "$deps" ]; then
        log_info "依赖任务: $deps"
    fi
    
    local retries=$(extract_max_retries "$next_task" "$TASK_FILE")
    log_info "最大重试: $retries 次"
    
    echo ""
    print_separator
}

cmd_complete() {
    local task_id=$1
    
    if [ -z "$task_id" ]; then
        log_error "请提供任务ID"
        echo "用法: $0 complete <task_id>"
        exit 1
    fi
    
    if is_task_completed "$task_id" "$TASK_FILE"; then
        log_warning "任务 $task_id 已经是完成状态"
        exit 0
    fi
    
    mark_task_completed "$task_id" "$TASK_FILE"
    
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    update_progress_log "$PROGRESS_FILE" "$task_id" "COMPLETED" "任务标记为完成"
    
    log_success "任务 $task_id 已标记为完成"
    
    # 更新统计
    local total=$(get_total_tasks "$TASK_FILE")
    local completed=$(get_completed_tasks "$TASK_FILE")
    local pending=$((total - completed))
    update_task_stats "$TASK_FILE" "$total" "$completed" "$pending"
}

cmd_reset() {
    local task_id=$1
    
    if [ -z "$task_id" ]; then
        log_error "请提供任务ID"
        echo "用法: $0 reset <task_id>"
        echo "或: $0 reset-all (重置所有任务)"
        exit 1
    fi
    
    mark_task_pending "$task_id" "$TASK_FILE"
    
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    update_progress_log "$PROGRESS_FILE" "$task_id" "RESET" "任务状态重置"
    
    log_success "任务 $task_id 已重置为待完成状态"
    
    # 更新统计
    local total=$(get_total_tasks "$TASK_FILE")
    local completed=$(get_completed_tasks "$TASK_FILE")
    local pending=$((total - completed))
    update_task_stats "$TASK_FILE" "$total" "$completed" "$pending"
}

cmd_reset_all() {
    log_warning "确定要重置所有任务状态吗？这会将所有已完成任务标记为未完成。(y/N)"
    read -r confirm
    
    if [[ "$confirm" =~ ^[Yy]$ ]]; then
        # 获取所有已完成任务
        grep "^- \[[xX]\] \*\*" "$TASK_FILE" | while read -r line; do
            local task_id=$(extract_task_id "$line")
            mark_task_pending "$task_id" "$TASK_FILE"
            log_info "已重置: $task_id"
        done
        
        local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
        update_progress_log "$PROGRESS_FILE" "ALL" "RESET_ALL" "所有任务状态重置"
        
        log_success "所有任务已重置"
        
        # 更新统计
        local total=$(get_total_tasks "$TASK_FILE")
        update_task_stats "$TASK_FILE" "$total" 0 "$total"
    else
        log_info "操作已取消"
    fi
}

# =============================================================================
# 帮助信息
# =============================================================================

show_help() {
    echo "任务管理工具"
    echo ""
    echo "用法: $0 <命令> [参数]"
    echo ""
    echo "命令:"
    echo "  status              显示任务状态概览"
    echo "  list                列出所有任务"
    echo "  pending             列出待完成任务"
    echo "  next                显示下一个可执行任务"
    echo "  complete <task_id>  标记指定任务为完成"
    echo "  reset <task_id>     重置指定任务状态"
    echo "  reset-all           重置所有任务状态"
    echo "  help                显示帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 status           # 查看整体进度"
    echo "  $0 next             # 查看下一个任务"
    echo "  $0 complete T001    # 标记 T001 完成"
    echo "  $0 reset T001       # 重置 T001 状态"
}

# =============================================================================
# 主入口
# =============================================================================

COMMAND=${1:-status}

# 切换命令
shift || true

case "$COMMAND" in
    status)
        cmd_status
        ;;
    list)
        cmd_list
        ;;
    pending)
        cmd_pending
        ;;
    next)
        cmd_next
        ;;
    complete)
        cmd_complete "$1"
        ;;
    reset)
        cmd_reset "$1"
        ;;
    reset-all)
        cmd_reset_all
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        log_error "未知命令: $COMMAND"
        show_help
        exit 1
        ;;
esac
