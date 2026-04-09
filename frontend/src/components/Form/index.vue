<template>
  <form
    :class="formClasses"
    @submit.prevent="handleSubmit"
  >
    <div v-if="title || description" class="xh-form__header">
      <h3 v-if="title" class="xh-form__title">{{ title }}</h3>
      <p v-if="description" class="xh-form__description">{{ description }}</p>
    </div>

    <div class="xh-form__body" :style="gridStyle">
      <slot />
    </div>

    <div v-if="showFooter" class="xh-form__footer">
      <slot name="footer">
        <XhButton
          v-if="showCancel"
          type="secondary"
          :size="buttonSize"
          :disabled="loading"
          @click="handleCancel"
        >
          {{ cancelText }}
        </XhButton>
        <XhButton
          v-if="showSubmit"
          type="primary"
          :size="buttonSize"
          :loading="loading"
          native-type="submit"
        >
          {{ submitText }}
        </XhButton>
      </slot>
    </div>
  </form>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import XhButton from '../Button/index.vue'

export interface FormProps {
  /** 表单标题 */
  title?: string
  /** 表单描述 */
  description?: string
  /** 布局方式 */
  layout?: 'vertical' | 'horizontal' | 'inline'
  /** 标签位置 */
  labelPosition?: 'top' | 'left' | 'right'
  /** 标签宽度 */
  labelWidth?: string | number
  /** 是否显示底部按钮 */
  showFooter?: boolean
  /** 是否显示提交按钮 */
  showSubmit?: boolean
  /** 是否显示取消按钮 */
  showCancel?: boolean
  /** 提交按钮文字 */
  submitText?: string
  /** 取消按钮文字 */
  cancelText?: string
  /** 按钮尺寸 */
  buttonSize?: 'small' | 'medium' | 'large'
  /** 是否加载中 */
  loading?: boolean
  /** 每行显示列数 */
  columns?: number
  /** 列间距 */
  columnGap?: string
  /** 行间距 */
  rowGap?: string
}

const props = withDefaults(defineProps<FormProps>(), {
  layout: 'vertical',
  labelPosition: 'top',
  showFooter: true,
  showSubmit: true,
  showCancel: true,
  submitText: '提交',
  cancelText: '取消',
  buttonSize: 'medium',
  loading: false,
  columns: 1,
  columnGap: '24px',
  rowGap: '20px'
})

const emit = defineEmits<{
  submit: [values: any]
  cancel: []
}>()

const formClasses = computed(() => {
  return [
    'xh-form',
    `xh-form--${props.layout}`,
    `xh-form--label-${props.labelPosition}`
  ]
})

const gridStyle = computed(() => {
  if (props.columns > 1) {
    return {
      display: 'grid',
      gridTemplateColumns: `repeat(${props.columns}, 1fr)`,
      gap: `${props.rowGap} ${props.columnGap}`
    }
  }
  return {}
})

const handleSubmit = () => {
  emit('submit', {})
}

const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped lang="scss">
.xh-form {
  width: 100%;

  &__header {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #E8E8EF;
  }

  &__title {
    font-size: 20px;
    font-weight: 600;
    color: #1A1A2E;
    margin: 0 0 8px;
    line-height: 1.4;
  }

  &__description {
    font-size: 14px;
    color: #8A8AA3;
    margin: 0;
    line-height: 1.5;
  }

  &__body {
    display: flex;
    flex-direction: column;
    gap: v-bind('props.rowGap');
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid #E8E8EF;
  }

  // 水平布局
  &--horizontal {
    .xh-form__body {
      flex-direction: row;
      flex-wrap: wrap;
      align-items: flex-start;
    }
  }

  // 行内布局
  &--inline {
    .xh-form__body {
      flex-direction: row;
      flex-wrap: wrap;
      align-items: center;
      gap: 16px;
    }

    .xh-form__footer {
      margin-top: 0;
      padding-top: 0;
      border-top: none;
      margin-left: auto;
    }
  }
}
</style>
