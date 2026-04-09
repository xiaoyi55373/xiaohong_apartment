<template>
  <div
    :class="formItemClasses"
    :style="formItemStyle"
  >
    <label
      v-if="label"
      :class="labelClasses"
      :style="labelStyle"
    >
      <span v-if="required" class="xh-form-item__required">*</span>
      {{ label }}
      <el-tooltip v-if="tooltip" :content="tooltip" placement="top">
        <SvgIcon name="info" :size="14" class="xh-form-item__tooltip" />
      </el-tooltip>
    </label>
    <div class="xh-form-item__content">
      <slot />
      <transition name="xh-fade">
        <div v-if="error" class="xh-form-item__error">
          <SvgIcon name="error" :size="14" />
          <span>{{ error }}</span>
        </div>
      </transition>
      <div v-if="$slots.extra || extra" class="xh-form-item__extra">
        <slot name="extra">{{ extra }}</slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, inject } from 'vue'
import SvgIcon from '@/components/SvgIcon/src/SvgIcon.vue'

export interface FormItemProps {
  /** 标签文本 */
  label?: string
  /** 是否必填 */
  required?: boolean
  /** 错误信息 */
  error?: string
  /** 提示信息 */
  tooltip?: string
  /** 额外说明 */
  extra?: string
  /** 标签宽度 */
  labelWidth?: string | number
  /** 占据列数 */
  span?: number
}

const props = defineProps<FormItemProps>()

// 注入表单上下文
const formContext = inject('xhForm', {
  layout: 'vertical',
  labelPosition: 'top',
  labelWidth: undefined as string | number | undefined
})

const formItemClasses = computed(() => {
  return [
    'xh-form-item',
    {
      'xh-form-item--error': props.error,
      'xh-form-item--required': props.required,
      [`xh-form-item--span-${props.span}`]: props.span
    }
  ]
})

const formItemStyle = computed(() => {
  const styles: Record<string, string> = {}
  if (props.span) {
    styles.gridColumn = `span ${props.span}`
  }
  return styles
})

const labelClasses = computed(() => {
  return [
    'xh-form-item__label',
    `xh-form-item__label--${formContext.labelPosition}`
  ]
})

const labelStyle = computed(() => {
  const width = props.labelWidth ?? formContext.labelWidth
  if (width && formContext.labelPosition !== 'top') {
    return { width: typeof width === 'number' ? `${width}px` : width }
  }
  return {}
})
</script>

<style scoped lang="scss">
.xh-form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;

  &__label {
    font-size: 14px;
    font-weight: 500;
    color: #1A1A2E;
    line-height: 1.5;
    display: flex;
    align-items: center;
    gap: 4px;

    &--left {
      justify-content: flex-start;
    }

    &--right {
      justify-content: flex-end;
    }
  }

  &__required {
    color: #FF6B6B;
    margin-right: 2px;
  }

  &__tooltip {
    color: #8A8AA3;
    cursor: help;
    transition: color 0.2s;

    &:hover {
      color: #FF6B6B;
    }
  }

  &__content {
    position: relative;
    flex: 1;
  }

  &__error {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 6px;
    font-size: 12px;
    color: #FF6B6B;
    animation: xh-shake 0.3s ease;
  }

  &__extra {
    margin-top: 6px;
    font-size: 12px;
    color: #8A8AA3;
    line-height: 1.5;
  }

  // 水平布局
  &--span-2 { grid-column: span 2; }
  &--span-3 { grid-column: span 3; }
  &--span-4 { grid-column: span 4; }
}

// 表单布局适配
.xh-form--horizontal {
  .xh-form-item {
    flex-direction: row;
    align-items: flex-start;
    gap: 16px;

    &__label {
      padding-top: 10px;
      flex-shrink: 0;
    }

    &__content {
      flex: 1;
      min-width: 0;
    }
  }
}

.xh-form--inline {
  .xh-form-item {
    flex-direction: row;
    align-items: center;
    gap: 8px;

    &__label {
      padding-top: 0;
      white-space: nowrap;
    }
  }
}

// 动画
.xh-fade-enter-active,
.xh-fade-leave-active {
  transition: opacity 0.2s ease;
}

.xh-fade-enter-from,
.xh-fade-leave-to {
  opacity: 0;
}

@keyframes xh-shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  75% { transform: translateX(4px); }
}
</style>
