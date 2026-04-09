<template>
  <Teleport to="body">
    <Transition name="dialog-mask" @after-enter="onAfterEnter" @after-leave="onAfterLeave">
      <div
        v-if="visible"
        class="dialog-mask"
        :class="{ 'dialog-mask--blur': maskBlur }"
        :style="maskStyle"
        @click="handleMaskClick"
      >
        <Transition name="dialog-content">
          <div
            v-if="showContent"
            ref="dialogRef"
            class="dialog-wrapper"
            :class="[`dialog--${size}`, `dialog--${type}`]"
            :style="dialogStyle"
            @click.stop
          >
            <!-- 关闭按钮 -->
            <button v-if="showClose" class="dialog-close" @click="handleClose">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12" stroke-linecap="round" />
              </svg>
            </button>

            <!-- 头部 -->
            <div v-if="title || $slots.header" class="dialog-header">
              <slot name="header">
                <div class="dialog-header__content">
                  <!-- 图标 -->
                  <div v-if="showTypeIcon" class="dialog-header__icon" :class="`dialog-header__icon--${type}`">
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
                    <svg v-else-if="type === 'confirm'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                      <circle cx="12" cy="12" r="10" />
                      <line x1="12" y1="16" x2="12" y2="12" />
                      <line x1="12" y1="8" x2="12.01" y2="8" />
                    </svg>
                  </div>
                  <h3 class="dialog-title">{{ title }}</h3>
                </div>
              </slot>
            </div>

            <!-- 内容区 -->
            <div class="dialog-body" :class="{ 'dialog-body--center': centerContent }">
              <slot>
                <p v-if="content" class="dialog-content__text">{{ content }}</p>
              </slot>
            </div>

            <!-- 底部 -->
            <div v-if="showFooter || $slots.footer" class="dialog-footer" :class="{ 'dialog-footer--center': centerFooter }">
              <slot name="footer">
                <el-button v-if="showCancel" :size="buttonSize" @click="handleCancel">
                  {{ cancelText }}
                </el-button>
                <el-button 
                  v-if="showConfirm" 
                  :type="confirmButtonType" 
                  :size="buttonSize" 
                  :loading="confirmLoading"
                  @click="handleConfirm"
                >
                  {{ confirmText }}
                </el-button>
              </slot>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
/**
 * 小红公寓claw - Dialog 对话框组件
 * @description 模态对话框，支持确认/取消、自定义内容
 * @author 小红
 */
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'

export type DialogType = 'default' | 'success' | 'error' | 'warning' | 'confirm'
export type DialogSize = 'small' | 'default' | 'large' | 'fullscreen'

interface Props {
  /** 是否显示 */
  visible: boolean
  /** 对话框类型 */
  type?: DialogType
  /** 标题 */
  title?: string
  /** 内容文本 */
  content?: string
  /** 尺寸 */
  size?: DialogSize
  /** 是否显示关闭按钮 */
  showClose?: boolean
  /** 是否显示遮罩层 */
  showMask?: boolean
  /** 点击遮罩是否关闭 */
  closeOnClickMask?: boolean
  /** 按ESC是否关闭 */
  closeOnPressEscape?: boolean
  /** 是否显示底部 */
  showFooter?: boolean
  /** 是否显示取消按钮 */
  showCancel?: boolean
  /** 是否显示确认按钮 */
  showConfirm?: boolean
  /** 取消按钮文字 */
  cancelText?: string
  /** 确认按钮文字 */
  confirmText?: string
  /** 确认按钮类型 */
  confirmButtonType?: 'primary' | 'success' | 'warning' | 'danger'
  /** 确认按钮加载状态 */
  confirmLoading?: boolean
  /** 按钮尺寸 */
  buttonSize?: 'small' | 'default' | 'large'
  /** 遮罩层模糊效果 */
  maskBlur?: boolean
  /** 内容居中 */
  centerContent?: boolean
  /** 底部居中 */
  centerFooter?: boolean
  /** 是否显示类型图标 */
  showTypeIcon?: boolean
  /** 自定义宽度 */
  width?: string | number
  /** 自定义顶部偏移 */
  top?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  size: 'default',
  showClose: true,
  showMask: true,
  closeOnClickMask: true,
  closeOnPressEscape: true,
  showFooter: true,
  showCancel: true,
  showConfirm: true,
  cancelText: '取消',
  confirmText: '确定',
  confirmButtonType: 'primary',
  confirmLoading: false,
  buttonSize: 'default',
  maskBlur: false,
  centerContent: false,
  centerFooter: false,
  showTypeIcon: true,
  top: '15vh'
})

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'close'): void
  (e: 'confirm'): void
  (e: 'cancel'): void
  (e: 'open'): void
  (e: 'opened'): void
  (e: 'closed'): void
}>()

const dialogRef = ref<HTMLElement>()
const showContent = ref(false)

// 遮罩样式
const maskStyle = computed(() => {
  if (!props.showMask) return { background: 'transparent', pointerEvents: 'none' as const }
  return {}
})

// 对话框样式
const dialogStyle = computed(() => {
  const style: Record<string, string> = {}
  
  if (props.width) {
    style.width = typeof props.width === 'number' ? `${props.width}px` : props.width
  }
  
  if (props.top && props.size !== 'fullscreen') {
    style.marginTop = props.top
  }
  
  return style
})

// 监听 visible 变化
watch(() => props.visible, (val) => {
  if (val) {
    emit('open')
    nextTick(() => {
      showContent.value = true
    })
    if (props.closeOnPressEscape) {
      document.addEventListener('keydown', handleKeydown)
    }
  } else {
    showContent.value = false
    document.removeEventListener('keydown', handleKeydown)
  }
})

// 遮罩点击
const handleMaskClick = () => {
  if (props.closeOnClickMask) {
    handleClose()
  }
}

// 关闭
const handleClose = () => {
  emit('update:visible', false)
  emit('close')
}

// 确认
const handleConfirm = () => {
  emit('confirm')
}

// 取消
const handleCancel = () => {
  emit('cancel')
  handleClose()
}

// 键盘事件
const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape') {
    handleClose()
  }
}

// 动画完成回调
const onAfterEnter = () => {
  emit('opened')
}

const onAfterLeave = () => {
  emit('closed')
}

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})

defineExpose({
  close: handleClose
})
</script>

<style scoped lang="scss">
// 品牌色变量
$brand-primary: #FF6B6B;
$brand-success: #1DD1A1;
$brand-warning: #FF9F43;
$brand-error: #FF4757;
$brand-info: #54A0FF;

// 遮罩层
.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9998;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow: auto;
  padding: 20px;

  &--blur {
    backdrop-filter: blur(4px);
  }
}

// 对话框
.dialog-wrapper {
  position: relative;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), 0 8px 24px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
  overflow: hidden;

  // 尺寸
  &.dialog--small {
    max-width: 400px;
  }

  &.dialog--default {
    max-width: 520px;
  }

  &.dialog--large {
    max-width: 720px;
  }

  &.dialog--fullscreen {
    max-width: calc(100vw - 40px);
    height: calc(100vh - 40px);
    margin-top: 0 !important;
  }
}

// 关闭按钮
.dialog-close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  padding: 0;
  margin: 0;
  border: none;
  background: transparent;
  color: #8a8aa3;
  cursor: pointer;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  z-index: 10;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    background: #f5f5fa;
    color: #4a4a68;
  }
}

// 头部
.dialog-header {
  padding: 20px 24px 0;
  flex-shrink: 0;

  &__content {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__icon {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 16px;
      height: 16px;
    }

    &--success {
      background: rgba($brand-success, 0.1);
      color: $brand-success;
    }

    &--error {
      background: rgba($brand-error, 0.1);
      color: $brand-error;
    }

    &--warning {
      background: rgba($brand-warning, 0.1);
      color: $brand-warning;
    }

    &--confirm {
      background: rgba($brand-info, 0.1);
      color: $brand-info;
    }
  }
}

.dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  line-height: 1.4;
}

// 内容区
.dialog-body {
  padding: 20px 24px;
  flex: 1;
  overflow-y: auto;
  min-height: 80px;

  &--center {
    text-align: center;
  }
}

.dialog-content__text {
  margin: 0;
  font-size: 14px;
  color: #4a4a68;
  line-height: 1.6;
}

// 底部
.dialog-footer {
  padding: 16px 24px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  flex-shrink: 0;
  border-top: 1px solid #f0f0f5;

  &--center {
    justify-content: center;
  }
}

// 遮罩动画
.dialog-mask-enter-active,
.dialog-mask-leave-active {
  transition: opacity 0.3s ease;
}

.dialog-mask-enter-from,
.dialog-mask-leave-to {
  opacity: 0;
}

// 内容动画
.dialog-content-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.dialog-content-leave-active {
  transition: all 0.2s ease;
}

.dialog-content-enter-from {
  opacity: 0;
  transform: scale(0.9) translateY(-20px);
}

.dialog-content-leave-to {
  opacity: 0;
  transform: scale(0.95) translateY(-10px);
}
</style>
