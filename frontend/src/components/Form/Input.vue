<template>
  <div :class="inputClasses">
    <div v-if="prefix || $slots.prefix" class="xh-input__prefix">
      <slot name="prefix">
        <SvgIcon v-if="prefix" :name="prefix" :size="iconSize" />
      </slot>
    </div>
    <input
      ref="inputRef"
      v-model="modelValue"
      :type="inputType"
      :placeholder="placeholder"
      :disabled="disabled"
      :readonly="readonly"
      :maxlength="maxlength"
      :min="min"
      :max="max"
      :step="step"
      class="xh-input__field"
      @focus="handleFocus"
      @blur="handleBlur"
      @input="handleInput"
      @change="handleChange"
      @keydown="handleKeydown"
    />
    <div v-if="suffix || $slots.suffix || clearable || showPassword" class="xh-input__suffix">
      <slot name="suffix">
        <SvgIcon v-if="suffix && !showClear && !showPwdToggle" :name="suffix" :size="iconSize" />
      </slot>
      <span
        v-if="showClear"
        class="xh-input__clear"
        @click="handleClear"
      >
        <SvgIcon name="close-circle" :size="16" />
      </span>
      <span
        v-if="showPwdToggle"
        class="xh-input__password"
        @click="togglePassword"
      >
        <SvgIcon :name="passwordVisible ? 'eye-off' : 'eye'" :size="16" />
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import SvgIcon from '@/components/SvgIcon/src/SvgIcon.vue'

export interface InputProps {
  /** 输入值 */
  modelValue?: string | number
  /** 输入类型 */
  type?: 'text' | 'password' | 'number' | 'tel' | 'email' | 'url' | 'search'
  /** 占位符 */
  placeholder?: string
  /** 是否禁用 */
  disabled?: boolean
  /** 是否只读 */
  readonly?: boolean
  /** 是否可清空 */
  clearable?: boolean
  /** 是否显示密码切换 */
  showPassword?: boolean
  /** 最大长度 */
  maxlength?: number
  /** 最小值（数字类型） */
  min?: number
  /** 最大值（数字类型） */
  max?: number
  /** 步长（数字类型） */
  step?: number
  /** 前缀图标 */
  prefix?: string
  /** 后缀图标 */
  suffix?: string
  /** 图标尺寸 */
  iconSize?: number
  /** 输入框尺寸 */
  size?: 'small' | 'medium' | 'large'
  /** 是否聚焦时高亮 */
  focusHighlight?: boolean
}

const props = withDefaults(defineProps<InputProps>(), {
  modelValue: '',
  type: 'text',
  disabled: false,
  readonly: false,
  clearable: false,
  showPassword: false,
  iconSize: 16,
  size: 'medium',
  focusHighlight: true
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  input: [value: string | number]
  change: [value: string | number]
  clear: []
  keydown: [event: KeyboardEvent]
}>()

const inputRef = ref<HTMLInputElement>()
const isFocused = ref(false)
const passwordVisible = ref(false)

const modelValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const inputType = computed(() => {
  if (props.type === 'password' && props.showPassword) {
    return passwordVisible.value ? 'text' : 'password'
  }
  return props.type
})

const inputClasses = computed(() => {
  return [
    'xh-input',
    `xh-input--${props.size}`,
    {
      'xh-input--disabled': props.disabled,
      'xh-input--focused': isFocused.value && props.focusHighlight,
      'xh-input--with-prefix': props.prefix || props.$slots?.prefix,
      'xh-input--with-suffix': props.suffix || props.$slots?.suffix || props.clearable || props.showPassword
    }
  ]
})

const showClear = computed(() => {
  return props.clearable && modelValue.value && !props.disabled && !props.readonly && isFocused.value
})

const showPwdToggle = computed(() => {
  return props.type === 'password' && props.showPassword && !props.disabled && !props.readonly
})

const handleFocus = (event: FocusEvent) => {
  isFocused.value = true
  emit('focus', event)
}

const handleBlur = (event: FocusEvent) => {
  isFocused.value = false
  emit('blur', event)
}

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('input', target.value)
}

const handleChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('change', target.value)
}

const handleKeydown = (event: KeyboardEvent) => {
  emit('keydown', event)
}

const handleClear = () => {
  emit('update:modelValue', '')
  emit('clear')
  inputRef.value?.focus()
}

const togglePassword = () => {
  passwordVisible.value = !passwordVisible.value
}

const focus = () => {
  inputRef.value?.focus()
}

const blur = () => {
  inputRef.value?.blur()
}

defineExpose({
  focus,
  blur,
  inputRef
})
</script>

<style scoped lang="scss">
.xh-input {
  display: flex;
  align-items: center;
  width: 100%;
  background: #FFFFFF;
  border: 1px solid #E8E8EF;
  border-radius: 8px;
  transition: all 0.2s ease;
  overflow: hidden;

  &:hover:not(&--disabled) {
    border-color: #D0D0E0;
  }

  &--focused {
    border-color: #FF6B6B;
    box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
  }

  // 尺寸
  &--small {
    height: 32px;
    font-size: 13px;

    .xh-input__field {
      padding: 0 12px;
    }

    &.xh-input--with-prefix .xh-input__field {
      padding-left: 8px;
    }

    &.xh-input--with-suffix .xh-input__field {
      padding-right: 8px;
    }
  }

  &--medium {
    height: 40px;
    font-size: 14px;

    .xh-input__field {
      padding: 0 16px;
    }

    &.xh-input--with-prefix .xh-input__field {
      padding-left: 12px;
    }

    &.xh-input--with-suffix .xh-input__field {
      padding-right: 12px;
    }
  }

  &--large {
    height: 48px;
    font-size: 15px;

    .xh-input__field {
      padding: 0 20px;
    }

    &.xh-input--with-prefix .xh-input__field {
      padding-left: 16px;
    }

    &.xh-input--with-suffix .xh-input__field {
      padding-right: 16px;
    }
  }

  // 禁用状态
  &--disabled {
    background: #F5F5FA;
    cursor: not-allowed;

    .xh-input__field {
      color: #B8B8CC;
      cursor: not-allowed;
    }
  }

  &__prefix,
  &__suffix {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #8A8AA3;
    flex-shrink: 0;
  }

  &__prefix {
    padding-left: 12px;
  }

  &__suffix {
    padding-right: 12px;
    gap: 8px;
  }

  &__field {
    flex: 1;
    min-width: 0;
    height: 100%;
    border: none;
    background: transparent;
    color: #1A1A2E;
    outline: none;
    font-family: inherit;

    &::placeholder {
      color: #B8B8CC;
    }

    &::-webkit-outer-spin-button,
    &::-webkit-inner-spin-button {
      -webkit-appearance: none;
      margin: 0;
    }

    &[type="number"] {
      -moz-appearance: textfield;
    }
  }

  &__clear,
  &__password {
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #B8B8CC;
    transition: color 0.2s;

    &:hover {
      color: #FF6B6B;
    }
  }
}
</style>
