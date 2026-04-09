#!/bin/bash
# =============================================================================
# 工具函数库
# 提供颜色输出、日志记录、任务解析等功能
# =============================================================================

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
GRAY='\033[0;90m'
NC='\033[0m' # No Color

# 日志函数
log_info()    { echo -e "${BLUE}[INFO]${NC} $(date '+%H:%M:%S') $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $(date '+%H:%M:%S') $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $(date '+%H:%M:%S') $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $(date '+%H:%M:%S') $1"; }
log_step()    { echo -e "${CYAN}[STEP $1/$2]${NC} $(date '+%H:%M:%S') $3"; }
log_debug()   { echo -e "${GRAY}[DEBUG]${NC} $(date '+%H:%M:%S') $1"; }
log_highlight(){ echo -e "${MAGENTA}$1${NC}"; }

# =============================================================================
# 任务解析函数
# =============================================================================

# 获取任务总数
get_total_tasks() {
    local task_file=$1
    local count=$(grep -cE "^- \[([ xX])\] \*\*[^*]+\*\*" "$task_file" 2>/dev/null)
    echo "${count:-0}"
}

# 获取已完成任务数
get_completed_tasks() {
    local task_file=$1
    local count=$(grep -cE "^- \[[xX]\] \*\*[^*]+\*\*" "$task_file" 2>/dev/null)
    echo "${count:-0}"
}

# 获取待完成任务数
get_pending_tasks() {
    local task_file=$1
    local count=$(grep -cE "^- \[ \] \*\*[^*]+\*\*" "$task_file" 2>/dev/null)
    echo "${count:-0}"
}

# 提取任务ID
extract_task_id() {
    local line=$1
    echo "$line" | grep -oE "\*\*[A-Z0-9]+\*\*" | tr -d '*' 2>/dev/null
}

# 提取任务优先级
extract_priority() {
    local task_id=$1
    local task_file=$2
    
    # 找到任务所在行，然后查找优先级
    local task_line=$(grep -n "\*\*$task_id\*\*" "$task_file" | head -1 | cut -d: -f2)
    
    # 在任务定义后几行查找优先级
    local line_num=$(grep -n "\*\*$task_id\*\*" "$task_file" | head -1 | cut -d: -f1)
    if [ -n "$line_num" ]; then
        local end_line=$((line_num + 10))
        sed -n "${line_num},${end_line}p" "$task_file" | grep -oE "P[0-9]" | head -1
    fi
}

# 提取任务命令
extract_task_command() {
    local task_id=$1
    local task_file=$2
    
    local line_num=$(grep -n "\*\*$task_id\*\*" "$task_file" 2>/dev/null | head -1 | cut -d: -f1)
    if [ -n "$line_num" ]; then
        local end_line=$((line_num + 15))
        local cmd=$(sed -n "${line_num},${end_line}p" "$task_file" 2>/dev/null | grep "命令:" | head -1 | sed 's/.*命令: `\(.*\)`.*/\1/')
        echo "$cmd"
    fi
}

# 提取验收标准
extract_task_expect() {
    local task_id=$1
    local task_file=$2
    
    local line_num=$(grep -n "\*\*$task_id\*\*" "$task_file" 2>/dev/null | head -1 | cut -d: -f1)
    if [ -n "$line_num" ]; then
        local end_line=$((line_num + 15))
        local expect=$(sed -n "${line_num},${end_line}p" "$task_file" 2>/dev/null | grep "验收标准:" | head -1 | sed 's/.*验收标准: //')
        echo "$expect"
    fi
}

# 提取工作目录
extract_task_workdir() {
    local task_id=$1
    local task_file=$2
    
    local line_num=$(grep -n "\*\*$task_id\*\*" "$task_file" 2>/dev/null | head -1 | cut -d: -f1)
    if [ -n "$line_num" ]; then
        local end_line=$((line_num + 15))
        local workdir=$(sed -n "${line_num},${end_line}p" "$task_file" 2>/dev/null | grep "工作目录:" | head -1 | sed 's/.*工作目录: //')
        echo "$workdir"
    fi
}

# 提取依赖任务
extract_task_deps() {
    local task_id=$1
    local task_file=$2
    
    local line_num=$(grep -n "\*\*$task_id\*\*" "$task_file" 2>/dev/null | head -1 | cut -d: -f1)
    if [ -n "$line_num" ]; then
        local end_line=$((line_num + 15))
        local deps=$(sed -n "${line_num},${end_line}p" "$task_file" 2>/dev/null | grep "依赖任务:" | head -1 | sed 's/.*依赖任务: //' | tr ',' ' ')
        echo "$deps"
    fi
}

# 提取最大重试次数
extract_max_retries() {
    local task_id=$1
    local task_file=$2
    
    local line_num=$(grep -n "\*\*$task_id\*\*" "$task_file" 2>/dev/null | head -1 | cut -d: -f1)
    if [ -n "$line_num" ]; then
        local end_line=$((line_num + 15))
        local retries=$(sed -n "${line_num},${end_line}p" "$task_file" 2>/dev/null | grep "最大重试次数:" | head -1 | grep -oE "[0-9]+")
        if [ -n "$retries" ]; then
            echo "$retries"
        else
            echo "3"  # 默认重试3次
        fi
    else
        echo "3"
    fi
}

# 获取所有未完成任务ID（按优先级排序）
get_pending_task_ids() {
    local task_file=$1
    
    # 获取所有未完成任务行
    grep "^- \[ \] \*\*" "$task_file" 2>/dev/null | while read -r line; do
        local task_id=$(extract_task_id "$line")
        local priority=$(extract_priority "$task_id" "$task_file")
        
        # 为排序添加数字优先级
        local priority_num=99
        case "$priority" in
            P0) priority_num=0 ;;
            P1) priority_num=1 ;;
            P2) priority_num=2 ;;
            P3) priority_num=3 ;;
        esac
        
        echo "$priority_num|$task_id"
    done | sort -t'|' -k1 -n | cut -d'|' -f2
}

# 检查任务是否已完成
is_task_completed() {
    local task_id=$1
    local task_file=$2
    
    grep "^- \[[xX]\] \*\*$task_id\*\*" "$task_file" > /dev/null 2>&1
}

# 标记任务完成
mark_task_completed() {
    local task_id=$1
    local task_file=$2
    
    # 使用临时文件进行替换
    local temp_file=$(mktemp)
    sed "s/^- \[ \] \*\*$task_id\*\*/- [x] **$task_id**/" "$task_file" > "$temp_file"
    mv "$temp_file" "$task_file"
}

# 标记任务为未完成（重置）
mark_task_pending() {
    local task_id=$1
    local task_file=$2
    
    local temp_file=$(mktemp)
    sed "s/^- \[[xX]\] \*\*$task_id\*\*/- [ ] **$task_id**/" "$task_file" > "$temp_file"
    mv "$temp_file" "$task_file"
}

# 检查任务依赖是否满足
check_dependencies() {
    local task_id=$1
    local task_file=$2
    
    local deps=$(extract_task_deps "$task_id" "$task_file")
    
    for dep in $deps; do
        if ! is_task_completed "$dep" "$task_file"; then
            return 1
        fi
    done
    
    return 0
}

# =============================================================================
# 进度日志函数
# =============================================================================

# 更新进度日志
update_progress_log() {
    local progress_file=$1
    local task_id=$2
    local status=$3
    local message=$4
    
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo "[$timestamp] [$status] [$task_id] $message" >> "$progress_file"
}

# 更新任务统计信息
update_task_stats() {
    local task_file=$1
    local total=$2
    local completed=$3
    local pending=$4
    
    # 使用临时文件更新统计信息
    local temp_file=$(mktemp)
    awk -v total="$total" -v completed="$completed" -v pending="$pending" '
        /^- 总任务数:/ { print "- 总任务数: " total; next }
        /^- 已完成:/ { print "- 已完成: " completed; next }
        /^- 待完成:/ { print "- 待完成: " pending; next }
        /^- 最后更新:/ { print "- 最后更新: " strftime("%Y-%m-%d %H:%M:%S"); next }
        { print }
    ' "$task_file" > "$temp_file"
    mv "$temp_file" "$task_file"
}

# =============================================================================
# 辅助函数
# =============================================================================

# 格式化耗时
format_duration() {
    local seconds=$1
    local minutes=$((seconds / 60))
    local remaining_seconds=$((seconds % 60))
    
    if [ $minutes -gt 0 ]; then
        echo "${minutes}分${remaining_seconds}秒"
    else
        echo "${remaining_seconds}秒"
    fi
}

# 打印分隔线
print_separator() {
    echo "================================================================================"
}

# 打印短分隔线
print_short_separator() {
    echo "----------------------------------------"
}

# 获取下一个可执行任务（满足依赖关系）
get_next_runnable_task() {
    local task_file=$1
    
    get_pending_task_ids "$task_file" | while read -r task_id; do
        if check_dependencies "$task_id" "$task_file"; then
            echo "$task_id"
            return
        fi
    done
}
