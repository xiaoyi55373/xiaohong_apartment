<template>
  <div class="xh-form-card" :class="cardClasses">
    <!-- 卡片头部 -->
    <div v-if="showHeader" class="xh-form-card__header">
      <div class="xh-form-card__header-left">
        <div v-if="icon" class="xh-form-card__icon">
          <el-icon>
            <component :is="icon" />
          </el-icon>
        </div>
        <div class="xh-form-card__title-section">
          <h3 class="xh-form-card__title">{{ title }}</h3>
          <p v-if="subtitle" class="xh-form-card__subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <div class="xh-form-card__header-right">
        <slot name="header-extra" />
        <div v-if="collapsible" class="xh-form-card__collapse-btn" @click="toggleCollapse">
          <el-icon>
            <component :is="isCollapsed ? ArrowDown : ArrowUp" />
          </el-icon>
        </div>
      </div>
    </div>

    <!-- 分割线 -->
    <div v-if="showHeader && showDivider" class="xh-form-card__divider"></div>

    <!-- 卡片内容 -->
    <div 
      class="xh-form-card__body" 
      :class="{ 'is-collapsed': isCollapsed }"
      :style="bodyStyle"
    >
      <slot />
    </div>

    <!-- 卡片底部 -->
    <div v-if="$slots.footer" class="xh-form-card__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'

export interface FormCardProps {
  /** 卡片标题 */
  title?: string
  /** 副标题 */
  subtitle?: string
  /** 图标 */
  icon?: string
  /** 是否显示头部 */
  showHeader?: boolean
  /** 是否显示分割线 */
  showDivider?: boolean
  /** 是否可折叠 */
  collapsible?: boolean
  /** 默认折叠状态 */
  defaultCollapsed?: boolean
  /** 卡片变体 */
  variant?: 'default' | 'bordered' | 'ghost'
  /** 卡片尺寸 */
  size?: 'small' | 'default' | 'large'
  /** 是否有阴影 */
  shadow?: boolean
  /** 是否悬浮效果 */
  hoverable?: boolean
  /** 内边距 */
  padding?: string | number
}

const props = withDefaults(defineProps<FormCardProps>(), {
  showHeader: true,
  showDivider: true,
  collapsible: false,
  defaultCollapsed: false,
  variant: 'default',
  size: 'default',
  shadow: true,
  hoverable: false
})

const isCollapsed = ref(props.defaultCollapsed)

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const cardClasses = computed(() => ({
  [`xh-form-card--${props.variant}`]: true,
  [`xh-form-card--${props.size}`]: true,
  'is-shadow': props.shadow,
  'is-hoverable': props.hoverable,
  'is-collapsed': isCollapsed.value
}))

const bodyStyle = computed(() => {
  const styles: Record<string, string> = {}
  if (props.padding !== undefined) {
    const paddingValue = typeof props.padding === 'number' ? `${props.padding}px` : props.padding
    styles.padding = paddingValue
  }
  return styles
})
</script>

<style scoped lang="scss">
.xh-form-card {
  background: #fff;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;

  // 变体样式
  &--default {
    border: 1px solid #E8E8EF;
  }

  &--bordered {
    border: 2px solid #E8E8EF;
    
    &.is-shadow {
      box-shadow: none;
    }
  }

  &--ghost {
    background: transparent;
    border: none;
    
    &.is-shadow {
      box-shadow: none;
    }

    .xh-form-card__header {
      background: transparent;
      padding-left: 0;
      padding-right: 0;
    }

    .xh-form-card__body {
      padding-left: 0;
      padding-right: 0;
    }
  }

  // 尺寸样式
  &--small {
    .xh-form-card__header {
      padding: 12px 16px;
    }

    .xh-form-card__body {
      padding: 16px;
    }

    .xh-form-card__title {
      font-size: 14px;
    }

    .xh-form-card__icon {
      width: 32px;
      height: 32px;

      .el-icon {
        font-size: 16px;
      }
    }
  }

  &--default {
    .xh-form-card__header {
      padding: 20px 24px;
    }

    .xh-form-card__body {
      padding: 24px;
    }
  }

  &--large {
    .xh-form-card__header {
      padding: 28px 32px;
    }

    .xh-form-card__body {
      padding: 32px;
    }

    .xh-form-card__title {
      font-size: 18px;
    }
  }

  // 阴影效果
  &.is-shadow {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  }

  // 悬浮效果
  &.is-hoverable {
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    }
  }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: linear-gradient(135deg, #FAFAFC 0%, #F5F5F8 100%);
  }

  &__header-left {
    display: flex;
    align-items: center;
    gap: 14px;
    flex: 1;
  }

  &__header-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    .el-icon {
      font-size: 20px;
      color: #fff;
    }
  }

  &__title-section {
    flex: 1;
    min-width: 0;
  }

  &__title {
    font-size: 16px;
    font-weight: 600;
    color: #1A1A2E;
    margin: 0;
    line-height: 1.4;
  }

  &__subtitle {
    font-size: 13px;
    color: #8A8AA3;
    margin: 4px 0 0;
    line-height: 1.4;
  }

  &__collapse-btn {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    background: #fff;
    border: 1px solid #E8E8EF;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;

    .el-icon {
      font-size: 14px;
      color: #8A8AA3;
      transition: transform 0.3s ease;
    }

    &:hover {
      background: #FFF0F0;
      border-color: #FF6B6B;

      .el-icon {
        color: #FF6B6B;
      }
    }
  }

  &__divider {
    height: 1px;
    background: linear-gradient(90deg, transparent 0%, #E8E8EF 20%, #E8E8EF 80%, transparent 100%);
    margin: 0 24px;
  }

  &__body {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &.is-collapsed {
      max-height: 0;
      padding-top: 0;
      padding-bottom: 0;
      opacity: 0;
      overflow: hidden;
    }
  }

  &__footer {
    padding: 16px 24px 24px;
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    border-top: 1px solid #F0F0F5;
    margin-top: -8px;
  }
}

// 表单卡片组样式
.xh-form-card-group {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .xh-form-card {
    &:not(:last-child) {
      position: relative;

      &::after {
        content: '';
        position: absolute;
        bottom: -10px;
        left: 50%;
        transform: translateX(-50%);
        width: 2px;
        height: 20px;
        background: linear-gradient(180deg, #E8E8EF 0%, transparent 100%);
      }
    }
  }
}

// 动画效果
.xh-form-card {
  animation: cardAppear 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes cardAppear {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式适配
@media (max-width: 768px) {
  .xh-form-card {
    &--default,
    &--large {
      .xh-form-card__header {
        padding: 16px 20px;
      }

      .xh-form-card__body {
        padding: 20px;
      }
    }

    &__divider {
      margin: 0 20px;
    }

    &__title {
      font-size: 15px;
    }

    &__subtitle {
      font-size: 12px;
    }

    &__icon {
      width: 36px;
      height: 36px;

      .el-icon {
        font-size: 18px;
      }
    }
  }
}
</style>
