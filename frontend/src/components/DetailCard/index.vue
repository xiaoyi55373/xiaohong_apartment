<template>
  <div class="xh-detail-card" :class="cardClasses">
    <!-- 卡片头部 -->
    <div v-if="showHeader" class="xh-detail-card__header">
      <div class="xh-detail-card__header-main">
        <div v-if="icon" class="xh-detail-card__icon">
          <el-icon>
            <component :is="icon" />
          </el-icon>
        </div>
        <div class="xh-detail-card__title-section">
          <h3 class="xh-detail-card__title">{{ title }}</h3>
          <p v-if="subtitle" class="xh-detail-card__subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <div class="xh-detail-card__header-extra">
        <slot name="header-extra" />
        <div v-if="collapsible" class="xh-detail-card__collapse-btn" @click="toggleCollapse">
          <el-icon>
            <component :is="isCollapsed ? ArrowDown : ArrowUp" />
          </el-icon>
        </div>
      </div>
    </div>

    <!-- 分割线 -->
    <div v-if="showHeader && showDivider" class="xh-detail-card__divider"></div>

    <!-- 卡片内容 -->
    <div 
      class="xh-detail-card__body" 
      :class="{ 'is-collapsed': isCollapsed }"
      :style="bodyStyle"
    >
      <slot />
    </div>

    <!-- 卡片底部 -->
    <div v-if="$slots.footer" class="xh-detail-card__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'

export interface DetailCardProps {
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
  variant?: 'default' | 'bordered' | 'ghost' | 'elevated'
  /** 卡片尺寸 */
  size?: 'small' | 'default' | 'large'
  /** 是否有阴影 */
  shadow?: boolean
  /** 是否悬浮效果 */
  hoverable?: boolean
  /** 内边距 */
  padding?: string | number
  /** 主题色 */
  theme?: 'default' | 'primary' | 'success' | 'warning' | 'danger'
}

const props = withDefaults(defineProps<DetailCardProps>(), {
  showHeader: true,
  showDivider: true,
  collapsible: false,
  defaultCollapsed: false,
  variant: 'default',
  size: 'default',
  shadow: true,
  hoverable: false,
  theme: 'default'
})

const isCollapsed = ref(props.defaultCollapsed)

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const cardClasses = computed(() => ({
  [`xh-detail-card--${props.variant}`]: true,
  [`xh-detail-card--${props.size}`]: true,
  [`xh-detail-card--${props.theme}`]: true,
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
.xh-detail-card {
  background: #fff;
  border-radius: 16px;
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

    .xh-detail-card__header {
      background: transparent;
      padding-left: 0;
      padding-right: 0;
    }

    .xh-detail-card__body {
      padding-left: 0;
      padding-right: 0;
    }
  }

  &--elevated {
    border: none;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

    &.is-shadow {
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
    }
  }

  // 主题色样式
  &--primary {
    .xh-detail-card__icon {
      background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
    }
  }

  &--success {
    .xh-detail-card__icon {
      background: linear-gradient(135deg, #52C41A 0%, #73D13D 100%);
    }
  }

  &--warning {
    .xh-detail-card__icon {
      background: linear-gradient(135deg, #FAAD14 0%, #FFC53D 100%);
    }
  }

  &--danger {
    .xh-detail-card__icon {
      background: linear-gradient(135deg, #FF4D4F 0%, #FF7875 100%);
    }
  }

  // 尺寸样式
  &--small {
    .xh-detail-card__header {
      padding: 16px 20px;
    }

    .xh-detail-card__body {
      padding: 20px;
    }

    .xh-detail-card__title {
      font-size: 15px;
    }

    .xh-detail-card__icon {
      width: 36px;
      height: 36px;

      .el-icon {
        font-size: 18px;
      }
    }
  }

  &--default {
    .xh-detail-card__header {
      padding: 24px;
    }

    .xh-detail-card__body {
      padding: 24px;
    }
  }

  &--large {
    .xh-detail-card__header {
      padding: 28px 32px;
    }

    .xh-detail-card__body {
      padding: 32px;
    }

    .xh-detail-card__title {
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

  &__header-main {
    display: flex;
    align-items: center;
    gap: 16px;
    flex: 1;
  }

  &__header-extra {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__icon {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    box-shadow: 0 4px 12px rgba(255, 107, 107, 0.25);

    .el-icon {
      font-size: 22px;
      color: #fff;
    }
  }

  &__title-section {
    flex: 1;
    min-width: 0;
  }

  &__title {
    font-size: 17px;
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

// 详情卡片组样式
.xh-detail-card-group {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .xh-detail-card {
    animation: cardSlideIn 0.5s cubic-bezier(0.4, 0, 0.2, 1) both;

    @for $i from 1 through 10 {
      &:nth-child(#{$i}) {
        animation-delay: #{$i * 0.08}s;
      }
    }

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
@keyframes cardSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.xh-detail-card {
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
  .xh-detail-card {
    &--default,
    &--large {
      .xh-detail-card__header {
        padding: 20px;
      }

      .xh-detail-card__body {
        padding: 20px;
      }
    }

    &__divider {
      margin: 0 20px;
    }

    &__title {
      font-size: 16px;
    }

    &__subtitle {
      font-size: 12px;
    }

    &__icon {
      width: 40px;
      height: 40px;

      .el-icon {
        font-size: 20px;
      }
    }
  }
}
</style>
