<template>
  <div class="xh-step-form">
    <!-- 步骤条 -->
    <div class="xh-step-form__header">
      <div class="xh-step-form__steps">
        <div
          v-for="(step, index) in steps"
          :key="index"
          class="xh-step-form__step"
          :class="{
            'is-active': currentStep === index,
            'is-completed': currentStep > index,
            'is-last': index === steps.length - 1
          }"
        >
          <div class="xh-step-form__step-line">
            <div class="xh-step-form__step-progress" :style="getProgressStyle(index)"></div>
          </div>
          <div class="xh-step-form__step-content">
            <div class="xh-step-form__step-icon">
              <el-icon v-if="currentStep > index" class="is-completed">
                <Check />
              </el-icon>
              <span v-else>{{ index + 1 }}</span>
            </div>
            <div class="xh-step-form__step-info">
              <div class="xh-step-form__step-title">{{ step.title }}</div>
              <div v-if="step.description" class="xh-step-form__step-desc">{{ step.description }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 表单内容区域 -->
    <div class="xh-step-form__content">
      <div class="xh-step-form__panel">
        <div v-if="currentStepData?.icon" class="xh-step-form__panel-icon">
          <el-icon>
            <component :is="currentStepData.icon" />
          </el-icon>
        </div>
        <h3 v-if="currentStepData?.title" class="xh-step-form__panel-title">
          {{ currentStepData.title }}
        </h3>
        <p v-if="currentStepData?.description" class="xh-step-form__panel-desc">
          {{ currentStepData.description }}
        </p>
        
        <div class="xh-step-form__panel-body">
          <slot :name="`step-${currentStep}`" :step="currentStep" :data="currentStepData">
            <component :is="currentStepData?.component" v-if="currentStepData?.component" />
          </slot>
        </div>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="xh-step-form__footer">
      <el-button
        v-if="showPrev && currentStep > 0"
        :size="buttonSize"
        @click="handlePrev"
      >
        <el-icon class="btn-icon"><ArrowLeft /></el-icon>
        {{ prevText }}
      </el-button>
      
      <el-button
        v-if="showNext && currentStep < steps.length - 1"
        type="primary"
        :size="buttonSize"
        :loading="loading"
        @click="handleNext"
      >
        {{ nextText }}
        <el-icon class="btn-icon"><ArrowRight /></el-icon>
      </el-button>
      
      <el-button
        v-if="showSubmit && currentStep === steps.length - 1"
        type="primary"
        :size="buttonSize"
        :loading="loading"
        @click="handleSubmit"
      >
        <el-icon class="btn-icon"><Check /></el-icon>
        {{ submitText }}
      </el-button>
      
      <el-button
        v-if="showCancel"
        :size="buttonSize"
        @click="handleCancel"
      >
        {{ cancelText }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { Check, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

export interface StepItem {
  /** 步骤标题 */
  title: string
  /** 步骤描述 */
  description?: string
  /** 步骤图标 */
  icon?: string
  /** 自定义组件 */
  component?: any
  /** 是否可点击跳转 */
  clickable?: boolean
}

export interface StepFormProps {
  /** 步骤数据 */
  steps: StepItem[]
  /** 当前步骤索引 */
  modelValue?: number
  /** 是否显示上一步按钮 */
  showPrev?: boolean
  /** 是否显示下一步按钮 */
  showNext?: boolean
  /** 是否显示提交按钮 */
  showSubmit?: boolean
  /** 是否显示取消按钮 */
  showCancel?: boolean
  /** 上一步按钮文字 */
  prevText?: string
  /** 下一步按钮文字 */
  nextText?: string
  /** 提交按钮文字 */
  submitText?: string
  /** 取消按钮文字 */
  cancelText?: string
  /** 按钮尺寸 */
  buttonSize?: 'small' | 'default' | 'large'
  /** 是否加载中 */
  loading?: boolean
  /** 是否验证当前步骤 */
  validateStep?: boolean
}

const props = withDefaults(defineProps<StepFormProps>(), {
  modelValue: 0,
  showPrev: true,
  showNext: true,
  showSubmit: true,
  showCancel: true,
  prevText: '上一步',
  nextText: '下一步',
  submitText: '提交',
  cancelText: '取消',
  buttonSize: 'default',
  loading: false,
  validateStep: true
})

const emit = defineEmits<{
  'update:modelValue': [value: number]
  change: [step: number, prevStep: number]
  prev: [step: number]
  next: [step: number]
  submit: []
  cancel: []
  'step-click': [step: number]
}>()

const currentStep = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  currentStep.value = val
})

watch(currentStep, (val) => {
  emit('update:modelValue', val)
})

const currentStepData = computed(() => props.steps[currentStep.value])

const getProgressStyle = (index: number) => {
  if (currentStep.value > index) {
    return { width: '100%' }
  } else if (currentStep.value === index) {
    return { width: '50%' }
  }
  return { width: '0%' }
}

const handlePrev = () => {
  if (currentStep.value > 0) {
    const prevStep = currentStep.value
    currentStep.value--
    emit('prev', currentStep.value)
    emit('change', currentStep.value, prevStep)
  }
}

const handleNext = () => {
  if (currentStep.value < props.steps.length - 1) {
    const prevStep = currentStep.value
    currentStep.value++
    emit('next', currentStep.value)
    emit('change', currentStep.value, prevStep)
  }
}

const handleSubmit = () => {
  emit('submit')
}

const handleCancel = () => {
  emit('cancel')
}

const goToStep = (step: number) => {
  if (step >= 0 && step < props.steps.length && props.steps[step].clickable !== false) {
    const prevStep = currentStep.value
    currentStep.value = step
    emit('step-click', step)
    emit('change', step, prevStep)
  }
}

defineExpose({
  currentStep,
  goToStep,
  handlePrev,
  handleNext,
  handleSubmit
})
</script>

<style scoped lang="scss">
.xh-step-form {
  width: 100%;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  overflow: hidden;

  &__header {
    padding: 32px 40px 24px;
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  }

  &__steps {
    display: flex;
    justify-content: space-between;
    position: relative;
  }

  &__step {
    flex: 1;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;

    &.is-last {
      flex: 0 0 auto;
    }

    &-line {
      position: absolute;
      top: 16px;
      left: 50%;
      right: -50%;
      height: 2px;
      background: rgba(255, 255, 255, 0.3);
      overflow: hidden;
    }

    &-progress {
      height: 100%;
      background: #fff;
      transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    }

    &.is-last &-line {
      display: none;
    }

    &-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      position: relative;
      z-index: 1;
    }

    &-icon {
      width: 34px;
      height: 34px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.3);
      border: 2px solid rgba(255, 255, 255, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      font-weight: 600;
      color: #fff;
      transition: all 0.3s ease;

      .el-icon {
        font-size: 16px;
      }
    }

    &.is-active &-icon {
      background: #fff;
      border-color: #fff;
      color: #FF6B6B;
      transform: scale(1.1);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    &.is-completed &-icon {
      background: #fff;
      border-color: #fff;
      color: #52C41A;
    }

    &-info {
      text-align: center;
      margin-top: 10px;
    }

    &-title {
      font-size: 14px;
      font-weight: 500;
      color: rgba(255, 255, 255, 0.9);
      transition: color 0.3s ease;
    }

    &.is-active &-title,
    &.is-completed &-title {
      color: #fff;
      font-weight: 600;
    }

    &-desc {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.7);
      margin-top: 4px;
    }
  }

  &__content {
    padding: 40px;
  }

  &__panel {
    max-width: 800px;
    margin: 0 auto;

    &-icon {
      width: 64px;
      height: 64px;
      border-radius: 16px;
      background: linear-gradient(135deg, #FFF0F0 0%, #FFE4E4 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 20px;

      .el-icon {
        font-size: 32px;
        color: #FF6B6B;
      }
    }

    &-title {
      font-size: 22px;
      font-weight: 600;
      color: #1A1A2E;
      margin: 0 0 8px;
    }

    &-desc {
      font-size: 14px;
      color: #8A8AA3;
      margin-bottom: 32px;
    }

    &-body {
      :deep(.el-form-item) {
        margin-bottom: 24px;

        &__label {
          font-weight: 500;
          color: #1A1A2E;
        }
      }
    }
  }

  &__footer {
    display: flex;
    justify-content: center;
    gap: 16px;
    padding: 24px 40px 40px;
    border-top: 1px solid #F0F0F5;

    .el-button {
      min-width: 120px;
      height: 44px;
      font-size: 15px;
      font-weight: 500;
      border-radius: 10px;

      .btn-icon {
        margin-right: 6px;
        font-size: 16px;
      }

      &--primary {
        background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
        border: none;

        &:hover {
          background: linear-gradient(135deg, #FF5252 0%, #FF7B7B 100%);
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(255, 107, 107, 0.35);
        }
      }
    }
  }
}

// 动画效果
.xh-step-form {
  &__step-icon {
    animation: stepIconAppear 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  }

  &__panel {
    animation: panelSlideUp 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  }
}

@keyframes stepIconAppear {
  from {
    transform: scale(0);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes panelSlideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

// 响应式适配
@media (max-width: 768px) {
  .xh-step-form {
    &__header {
      padding: 24px 20px 20px;
    }

    &__step {
      &-title {
        font-size: 12px;
      }

      &-desc {
        display: none;
      }

      &-icon {
        width: 28px;
        height: 28px;
        font-size: 12px;
      }
    }

    &__content {
      padding: 24px 20px;
    }

    &__panel {
      &-title {
        font-size: 18px;
      }
    }

    &__footer {
      padding: 20px;
      flex-wrap: wrap;

      .el-button {
        flex: 1;
        min-width: auto;
      }
    }
  }
}
</style>
