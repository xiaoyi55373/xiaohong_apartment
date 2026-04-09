<template>
  <Teleport to="body">
    <TransitionGroup name="toast" tag="div" class="toast-container" :class="`toast--${position}`">
      <div
        v-for="item in toastList"
        :key="item.id"
        class="toast-item"
        :class="[`toast--${item.type}`, `toast--${item.size}`]"
        @mouseenter="pauseTimer(item.id)"
        @mouseleave="resumeTimer(item.id)"
      >
        <!-- 图标 -->
        <div class="toast-icon">
          <svg v-if="item.type === 'success'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <path d="M8 12l2.5 2.5L16 9" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          <svg v-else-if="item.type === 'error'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <path d="M8 8l8 8M16 8l-8 8" stroke-linecap="round" />
          </svg>
          <svg v-else-if="item.type === 'warning'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
            <line x1="12" y1="9" x2="12" y2="13" />
            <line x1="12" y1="17" x2="12.01" y2="17" />
          </svg>
          <svg v-else-if="item.type === 'info'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="16" x2="12" y2="12" />
            <line x1="12" y1="8" x2="12.01" y2="8" />
          </svg>
          <svg v-else-if="item.type === 'loading'" class="toast-icon--spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2v4m0 12v4M4.93 4.93l2.83 2.83m8.48 8.48l2.83 2.83M2 12h4m12 0h4M4.93 19.07l2.83-2.83m8.48-8.48l2.83-2.83" stroke-linecap="round" />
          </svg>
        </div>

        <!-- 内容 -->
        <div class="toast-content">
          <div v-if="item.title" class="toast-title">{{ item.title }}</div>
          <div class="toast-message">{{ item.message }}</div>
        </div>

        <!-- 关闭按钮 -->
        <button v-if="item.closable" class="toast-close" @click="remove(item.id)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12" stroke-linecap="round" />
          </svg>
        </button>

        <!-- 进度条 -->
        <div v-if="item.showProgress && item.duration > 0" class="toast-progress">
          <div 
            class="toast-progress__bar" 
            :style="{ 
              animationDuration: `${item.duration}ms`,
              animationPlayState: item.paused ? 'paused' : 'running'
            }" 
          />
        </div>
      </div>
    </TransitionGroup>
  </Teleport>
</template>

<script setup lang="ts">
/**
 * 小红公寓claw - Toast 轻提示组件
 * @description 轻量级消息提示，支持多种类型和位置
 * @author 小红
 */
import { ref, computed } from 'vue'

export type ToastType = 'success' | 'error' | 'warning' | 'info' | 'loading'
export type ToastPosition = 'top-left' | 'top-center' | 'top-right' | 'bottom-left' | 'bottom-center' | 'bottom-right'
export type ToastSize = 'small' | 'default' | 'large'

interface ToastItem {
  id: string
  type: ToastType
  title?: string
  message: string
  duration: number
  closable: boolean
  showProgress: boolean
  position: ToastPosition
  size: ToastSize
  paused: boolean
  timer?: ReturnType<typeof setTimeout>
}

interface ToastOptions {
  type?: ToastType
  title?: string
  message: string
  duration?: number
  closable?: boolean
  showProgress?: boolean
  position?: ToastPosition
  size?: ToastSize
}

const toastList = ref<ToastItem[]>([])
let idCounter = 0

// 生成唯一ID
const generateId = () => `toast-${++idCounter}-${Date.now()}`

// 添加 Toast
const add = (options: ToastOptions) => {
  const id = generateId()
  const duration = options.duration ?? 3000
  
  const item: ToastItem = {
    id,
    type: options.type || 'info',
    title: options.title,
    message: options.message,
    duration,
    closable: options.closable ?? true,
    showProgress: options.showProgress ?? (duration > 0),
    position: options.position || 'top-right',
    size: options.size || 'default',
    paused: false
  }

  toastList.value.push(item)

  // 自动关闭
  if (duration > 0 && options.type !== 'loading') {
    item.timer = setTimeout(() => {
      remove(id)
    }, duration)
  }

  return id
}

// 移除 Toast
const remove = (id: string) => {
  const index = toastList.value.findIndex(item => item.id === id)
  if (index > -1) {
    const item = toastList.value[index]
    if (item.timer) {
      clearTimeout(item.timer)
    }
    toastList.value.splice(index, 1)
  }
}

// 暂停计时器
const pauseTimer = (id: string) => {
  const item = toastList.value.find(item => item.id === id)
  if (item && item.timer) {
    item.paused = true
    clearTimeout(item.timer)
  }
}

// 恢复计时器
const resumeTimer = (id: string) => {
  const item = toastList.value.find(item => item.id === id)
  if (item && item.duration > 0 && !item.paused === false) {
    item.paused = false
    item.timer = setTimeout(() => {
      remove(id)
    }, item.duration)
  }
}

// 清空所有
const clear = () => {
  toastList.value.forEach(item => {
    if (item.timer) clearTimeout(item.timer)
  })
  toastList.value = []
}

// 快捷方法
const success = (message: string, options?: Omit<ToastOptions, 'type' | 'message'>) => 
  add({ type: 'success', message, ...options })

const error = (message: string, options?: Omit<ToastOptions, 'type' | 'message'>) => 
  add({ type: 'error', message, ...options })

const warning = (message: string, options?: Omit<ToastOptions, 'type' | 'message'>) => 
  add({ type: 'warning', message, ...options })

const info = (message: string, options?: Omit<ToastOptions, 'type' | 'message'>) => 
  add({ type: 'info', message, ...options })

const loading = (message: string, options?: Omit<ToastOptions, 'type' | 'message'>) => 
  add({ type: 'loading', message, duration: 0, ...options })

// 暴露方法
defineExpose({
  add,
  remove,
  clear,
  success,
  error,
  warning,
  info,
  loading
})
</script>

<style scoped lang="scss">
// 品牌色变量
$brand-primary: #FF6B6B;
$brand-success: #1DD1A1;
$brand-warning: #FF9F43;
$brand-error: #FF4757;
$brand-info: #54A0FF;

// 容器样式
.toast-container {
  position: fixed;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  pointer-events: none;
  padding: 20px;

  &.toast--top-left {
    top: 0;
    left: 0;
  }

  &.toast--top-center {
    top: 0;
    left: 50%;
    transform: translateX(-50%);
    align-items: center;
  }

  &.toast--top-right {
    top: 0;
    right: 0;
    align-items: flex-end;
  }

  &.toast--bottom-left {
    bottom: 0;
    left: 0;
    flex-direction: column-reverse;
  }

  &.toast--bottom-center {
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    align-items: center;
    flex-direction: column-reverse;
  }

  &.toast--bottom-right {
    bottom: 0;
    right: 0;
    align-items: flex-end;
    flex-direction: column-reverse;
  }
}

// Toast 项
.toast-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 18px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06);
  pointer-events: auto;
  min-width: 280px;
  max-width: 400px;
  position: relative;
  overflow: hidden;
  border-left: 4px solid transparent;

  // 类型样式
  &.toast--success {
    border-left-color: $brand-success;
    .toast-icon { color: $brand-success; }
  }

  &.toast--error {
    border-left-color: $brand-error;
    .toast-icon { color: $brand-error; }
  }

  &.toast--warning {
    border-left-color: $brand-warning;
    .toast-icon { color: $brand-warning; }
  }

  &.toast--info {
    border-left-color: $brand-info;
    .toast-icon { color: $brand-info; }
  }

  &.toast--loading {
    border-left-color: $brand-primary;
    .toast-icon { color: $brand-primary; }
  }
}

// 图标
.toast-icon {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
  margin-top: 2px;

  svg {
    width: 100%;
    height: 100%;
  }

  &--spin {
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// 内容
.toast-content {
  flex: 1;
  min-width: 0;
}

.toast-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 4px;
  line-height: 1.4;
}

.toast-message {
  font-size: 14px;
  color: #4a4a68;
  line-height: 1.5;
  word-break: break-word;
}

// 关闭按钮
.toast-close {
  width: 20px;
  height: 20px;
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

// 进度条
.toast-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: rgba(0, 0, 0, 0.05);

  &__bar {
    height: 100%;
    background: linear-gradient(90deg, $brand-primary, lighten($brand-primary, 15%));
    animation: progress linear forwards;
  }
}

@keyframes progress {
  from { width: 100%; }
  to { width: 0%; }
}

// 尺寸变体
.toast--small {
  padding: 10px 14px;
  min-width: 200px;

  .toast-icon {
    width: 18px;
    height: 18px;
  }

  .toast-title {
    font-size: 14px;
  }

  .toast-message {
    font-size: 13px;
  }
}

.toast--large {
  padding: 18px 24px;
  min-width: 320px;

  .toast-icon {
    width: 26px;
    height: 26px;
  }

  .toast-title {
    font-size: 16px;
  }

  .toast-message {
    font-size: 15px;
  }
}

// 过渡动画
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

// 顶部居中/底部居中动画
.toast--top-center,
.toast--bottom-center {
  .toast-enter-from {
    opacity: 0;
    transform: translateY(-20px);
  }

  .toast-leave-to {
    opacity: 0;
    transform: translateY(20px);
  }
}
</style>
