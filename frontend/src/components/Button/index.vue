<template>
  <button
    :class="buttonClasses"
    :disabled="disabled || loading"
    :type="nativeType"
    @click="handleClick"
  >
    <span v-if="loading" class="xh-button__loading">
      <svg class="xh-button__spinner" viewBox="0 0 50 50">
        <circle cx="25" cy="25" r="20" fill="none" stroke="currentColor" stroke-width="4" />
      </svg>
    </span>
    <span v-if="icon && !loading" class="xh-button__icon">
      <SvgIcon :name="icon" :size="iconSize" />
    </span>
    <span class="xh-button__content">
      <slot />
    </span>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SvgIcon from '@/components/SvgIcon/src/SvgIcon.vue'

export interface ButtonProps {
  /** 按钮类型 */
  type?: 'primary' | 'secondary' | 'success' | 'warning' | 'danger' | 'text'
  /** 按钮尺寸 */
  size?: 'small' | 'medium' | 'large'
  /** 是否禁用 */
  disabled?: boolean
  /** 是否加载中 */
  loading?: boolean
  /** 是否为块级按钮 */
  block?: boolean
  /** 是否为圆角按钮 */
  round?: boolean
  /** 是否为圆形按钮 */
  circle?: boolean
  /** 图标名称 */
  icon?: string
  /** 图标尺寸 */
  iconSize?: number
  /** 原生按钮类型 */
  nativeType?: 'button' | 'submit' | 'reset'
  /** 是否幽灵按钮 */
  ghost?: boolean
}

const props = withDefaults(defineProps<ButtonProps>(), {
  type: 'primary',
  size: 'medium',
  disabled: false,
  loading: false,
  block: false,
  round: false,
  circle: false,
  iconSize: 16,
  nativeType: 'button',
  ghost: false
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const buttonClasses = computed(() => {
  const classes = [
    'xh-button',
    `xh-button--${props.type}`,
    `xh-button--${props.size}`,
    {
      'xh-button--disabled': props.disabled || props.loading,
      'xh-button--loading': props.loading,
      'xh-button--block': props.block,
      'xh-button--round': props.round,
      'xh-button--circle': props.circle,
      'xh-button--ghost': props.ghost,
      'xh-button--with-icon': props.icon || props.loading
    }
  ]
  return classes
})

const handleClick = (event: MouseEvent) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped lang="scss">
.xh-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-family: var(--el-font-family);
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
  outline: none;
  position: relative;
  overflow: hidden;

  // 尺寸
  &--small {
    height: 32px;
    padding: 0 16px;
    font-size: 14px;
    border-radius: 6px;
  }

  &--medium {
    height: 40px;
    padding: 0 24px;
    font-size: 15px;
    border-radius: 8px;
  }

  &--large {
    height: 48px;
    padding: 0 32px;
    font-size: 16px;
    border-radius: 8px;
  }

  // 主按钮
  &--primary {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
    color: #FFFFFF;
    box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);

    &:hover:not(:disabled) {
      background: linear-gradient(135deg, #E55A5A 0%, #FF6B6B 100%);
      box-shadow: 0 6px 16px rgba(255, 107, 107, 0.4);
      transform: translateY(-1px);
    }

    &:active:not(:disabled) {
      background: linear-gradient(135deg, #CC4A4A 0%, #E55A5A 100%);
      transform: translateY(0);
      box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
    }

    &.xh-button--ghost {
      background: transparent;
      color: #FF6B6B;
      border: 1px solid #FF6B6B;
      box-shadow: none;

      &:hover:not(:disabled) {
        background: rgba(255, 107, 107, 0.1);
      }
    }
  }

  // 次要按钮
  &--secondary {
    background: #FFFFFF;
    color: #FF6B6B;
    border: 1px solid #FFE0E0;
    box-shadow: 0 2px 8px rgba(26, 26, 46, 0.06);

    &:hover:not(:disabled) {
      background: #FFF0F0;
      border-color: #FF6B6B;
      box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
    }

    &:active:not(:disabled) {
      background: #FFE0E0;
      transform: translateY(0);
    }

    &.xh-button--ghost {
      background: transparent;
      color: #4A4A68;
      border: 1px solid #E8E8EF;

      &:hover:not(:disabled) {
        background: #F5F5FA;
        border-color: #D0D0E0;
      }
    }
  }

  // 成功按钮
  &--success {
    background: linear-gradient(135deg, #1DD1A1 0%, #4DE4B8 100%);
    color: #FFFFFF;
    box-shadow: 0 4px 12px rgba(29, 209, 161, 0.3);

    &:hover:not(:disabled) {
      background: linear-gradient(135deg, #18B88D 0%, #1DD1A1 100%);
      box-shadow: 0 6px 16px rgba(29, 209, 161, 0.4);
      transform: translateY(-1px);
    }

    &:active:not(:disabled) {
      background: linear-gradient(135deg, #14A07A 0%, #18B88D 100%);
      transform: translateY(0);
    }

    &.xh-button--ghost {
      background: transparent;
      color: #1DD1A1;
      border: 1px solid #1DD1A1;
      box-shadow: none;

      &:hover:not(:disabled) {
        background: rgba(29, 209, 161, 0.1);
      }
    }
  }

  // 警告按钮
  &--warning {
    background: linear-gradient(135deg, #FF9F43 0%, #FFB36A 100%);
    color: #FFFFFF;
    box-shadow: 0 4px 12px rgba(255, 159, 67, 0.3);

    &:hover:not(:disabled) {
      background: linear-gradient(135deg, #E68A30 0%, #FF9F43 100%);
      box-shadow: 0 6px 16px rgba(255, 159, 67, 0.4);
      transform: translateY(-1px);
    }

    &:active:not(:disabled) {
      background: linear-gradient(135deg, #CC7720 0%, #E68A30 100%);
      transform: translateY(0);
    }

    &.xh-button--ghost {
      background: transparent;
      color: #FF9F43;
      border: 1px solid #FF9F43;
      box-shadow: none;

      &:hover:not(:disabled) {
        background: rgba(255, 159, 67, 0.1);
      }
    }
  }

  // 危险按钮
  &--danger {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF4757 100%);
    color: #FFFFFF;
    box-shadow: 0 4px 12px rgba(255, 71, 87, 0.3);

    &:hover:not(:disabled) {
      background: linear-gradient(135deg, #E55A5A 0%, #FF6B6B 100%);
      box-shadow: 0 6px 16px rgba(255, 71, 87, 0.4);
      transform: translateY(-1px);
    }

    &:active:not(:disabled) {
      background: linear-gradient(135deg, #CC4A4A 0%, #E55A5A 100%);
      transform: translateY(0);
    }

    &.xh-button--ghost {
      background: transparent;
      color: #FF4757;
      border: 1px solid #FF4757;
      box-shadow: none;

      &:hover:not(:disabled) {
        background: rgba(255, 71, 87, 0.1);
      }
    }
  }

  // 文字按钮
  &--text {
    background: transparent;
    color: #FF6B6B;
    padding: 0 8px;
    height: auto;

    &:hover:not(:disabled) {
      color: #E55A5A;
      background: rgba(255, 107, 107, 0.08);
    }

    &:active:not(:disabled) {
      color: #CC4A4A;
    }
  }

  // 禁用状态
  &--disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  // 加载状态
  &--loading {
    cursor: wait;
  }

  // 块级按钮
  &--block {
    width: 100%;
  }

  // 圆角按钮
  &--round {
    border-radius: 9999px;
  }

  // 圆形按钮
  &--circle {
    border-radius: 50%;
    padding: 0;
    width: 40px;
    height: 40px;

    &.xh-button--small {
      width: 32px;
      height: 32px;
    }

    &.xh-button--large {
      width: 48px;
      height: 48px;
    }
  }

  // 带图标
  &--with-icon {
    .xh-button__content {
      margin-left: 2px;
    }
  }

  // 加载动画
  &__loading {
    display: inline-flex;
    align-items: center;
  }

  &__spinner {
    width: 16px;
    height: 16px;
    animation: xh-spin 1s linear infinite;

    circle {
      stroke-dasharray: 80, 200;
      stroke-dashoffset: 0;
      animation: xh-dash 1.5s ease-in-out infinite;
    }
  }

  &__icon {
    display: inline-flex;
    align-items: center;
  }
}

@keyframes xh-spin {
  100% {
    transform: rotate(360deg);
  }
}

@keyframes xh-dash {
  0% {
    stroke-dasharray: 1, 150;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -35;
  }
  100% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -124;
  }
}
</style>
