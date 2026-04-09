<template>
  <Teleport to="body">
    <Transition name="message-fade" @after-leave="onAfterLeave">
      <div
        v-if="visible"
        class="message-wrapper"
        :class="[`message--${type}`, `message--${size}`]"
        :style="customStyle"
        @mouseenter="clearTimer"
        @mouseleave="startTimer"
      >
        <div class="message-content">
          <!-- 图标 -->
          <div class="message-icon">
            <svg v-if="type === 'success'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <circle cx="12" cy="12" r="10" />
              <path d="M8 12l2.5 2.5L16 9" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            <svg v-else-if="type === 'error'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <circle cx="12" cy="12" r="10" />
              <path d="M8 8l8 8M16 8l-8 8" stroke-linecap="round" />
            </svg>
            <svg v-else-if="type === 'warning'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
              <line x1="12" y1="9" x2="12" y2="13" />
              <line x1="12" y1="17" x2="12.01" y2="17" />
            </svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="16" x2="12" y2="12" />
              <line x1="12" y1="8" x2="12.01" y2="8" />
            </svg>
          </div>

          <!-- 消息内容 -->
          <div class="message-text">{{ message }}</div>

          <!-- 关闭按钮 -->
          <button v-if="showClose" class="message-close" @click="close">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12" stroke-linecap="round" />
            </svg>
          </button>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
/**
 * 小红公寓claw - Message 消息提示组件
 * @description 页面顶部居中消息提示，自动消失
 * @author 小红
 */
import { ref, computed, onMounted, onUnmounted } from 'vue'

export type MessageType = 'success' | 'error' | 'warning' | 'info'
export type MessageSize = 'small' | 'default' | 'large'

interface Props {
  /** 消息类型 */
  type?: MessageType
  /** 消息内容 */
  message: string
  /** 显示时长(ms)，0表示不自动关闭 */
  duration?: number
  /** 是否显示关闭按钮 */
  showClose?: boolean
  /** 尺寸 */
  size?: MessageSize
  /** 自定义样式 */
  customStyle?: Record<string, string>
  /** 关闭回调 */
  onClose?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info',
  duration: 3000,
  showClose: false,
  size: 'default'
})

const emit = defineEmits<{
  (e: 'destroy'): void
}>()

const visible = ref(false)
let timer: ReturnType<typeof setTimeout> | null = null

// 自定义样式
const customStyle = computed(() => {
  return props.customStyle || {}
})

// 关闭消息
const close = () => {
  visible.value = false
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
}

// 开始计时
const startTimer = () => {
  if (props.duration > 0 && !timer) {
    timer = setTimeout(() => {
      close()
    }, props.duration)
  }
}

// 清除计时器
const clearTimer = () => {
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
}

// 动画结束后销毁
const onAfterLeave = () => {
  props.onClose?.()
  emit('destroy')
}

onMounted(() => {
  visible.value = true
  startTimer()
})

onUnmounted(() => {
  clearTimer()
})

defineExpose({
  close,
  visible
})
</script>

<style scoped lang="scss">
// 品牌色变量
$brand-success: #1DD1A1;
$brand-warning: #FF9F43;
$brand-error: #FF4757;
$brand-info: #54A0FF;

.message-wrapper {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 14px 20px;
  min-width: 280px;
  max-width: 500px;

  // 类型样式 - 顶部边框
  &.message--success {
    border-top: 3px solid $brand-success;
    .message-icon { color: $brand-success; }
  }

  &.message--error {
    border-top: 3px solid $brand-error;
    .message-icon { color: $brand-error; }
  }

  &.message--warning {
    border-top: 3px solid $brand-warning;
    .message-icon { color: $brand-warning; }
  }

  &.message--info {
    border-top: 3px solid $brand-info;
    .message-icon { color: $brand-info; }
  }
}

.message-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.message-icon {
  width: 22px;
  height: 22px;
  flex-shrink: 0;

  svg {
    width: 100%;
    height: 100%;
  }
}

.message-text {
  flex: 1;
  font-size: 14px;
  color: #1a1a2e;
  line-height: 1.5;
  word-break: break-word;
}

.message-close {
  width: 22px;
  height: 22px;
  padding: 0;
  margin: 0;
  border: none;
  background: transparent;
  color: #8a8aa3;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;

  svg {
    width: 14px;
    height: 14px;
  }

  &:hover {
    background: #f5f5fa;
    color: #4a4a68;
  }
}

// 尺寸变体
.message--small {
  padding: 10px 16px;
  min-width: 240px;

  .message-icon {
    width: 18px;
    height: 18px;
  }

  .message-text {
    font-size: 13px;
  }
}

.message--large {
  padding: 18px 24px;
  min-width: 320px;

  .message-icon {
    width: 26px;
    height: 26px;
  }

  .message-text {
    font-size: 15px;
  }
}

// 过渡动画
.message-fade-enter-active,
.message-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-fade-enter-from {
  opacity: 0;
  transform: translateX(-50%) translateY(-20px);
}

.message-fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-20px);
}
</style>
