<template>
  <div
    :class="[
      'xh-card',
      `xh-card--${variant}`,
      `xh-card--${size}`,
      {
        'xh-card--hoverable': hoverable,
        'xh-card--bordered': bordered,
        'xh-card--loading': loading,
        'xh-card--selected': selected,
      },
    ]"
    :style="cardStyle"
    @click="handleClick"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <!-- 加载状态 -->
    <div v-if="loading" class="xh-card__loading">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 卡片头部 -->
    <div v-if="$slots.header || title || extra" class="xh-card__header">
      <div class="xh-card__header-left">
        <div v-if="icon" class="xh-card__icon">
          <el-icon :size="iconSize">
            <component :is="icon" />
          </el-icon>
        </div>
        <div class="xh-card__title-wrapper">
          <h3 v-if="title" class="xh-card__title">{{ title }}</h3>
          <p v-if="subtitle" class="xh-card__subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <div class="xh-card__header-right">
        <slot name="extra">
          <el-tag v-if="tag" :type="tagType" size="small">{{ tag }}</el-tag>
        </slot>
      </div>
    </div>

    <!-- 封面图 -->
    <div v-if="cover || $slots.cover" class="xh-card__cover">
      <slot name="cover">
        <el-image
          :src="cover"
          :fit="coverFit"
          :preview-src-list="preview ? [cover] : []"
          class="xh-card__cover-image"
        >
          <template #error>
            <div class="xh-card__cover-placeholder">
              <el-icon :size="40"><Picture /></el-icon>
            </div>
          </template>
        </el-image>
        <div v-if="coverTag" class="xh-card__cover-tag">{{ coverTag }}</div>
      </slot>
    </div>

    <!-- 卡片内容 -->
    <div class="xh-card__body" :style="bodyStyle">
      <slot />
    </div>

    <!-- 卡片操作区 -->
    <div v-if="$slots.actions" class="xh-card__actions">
      <slot name="actions" />
    </div>

    <!-- 卡片底部 -->
    <div v-if="$slots.footer || footer" class="xh-card__footer">
      <slot name="footer">{{ footer }}</slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import type { CSSProperties } from 'vue'

type CardVariant = 'default' | 'primary' | 'success' | 'warning' | 'danger' | 'info'
type CardSize = 'small' | 'default' | 'large'
type TagType = '' | 'success' | 'warning' | 'danger' | 'info'

interface Props {
  /** 卡片标题 */
  title?: string
  /** 副标题 */
  subtitle?: string
  /** 卡片底部内容 */
  footer?: string
  /** 图标名称 */
  icon?: string
  /** 图标大小 */
  iconSize?: number
  /** 标签文本 */
  tag?: string
  /** 标签类型 */
  tagType?: TagType
  /** 封面图片URL */
  cover?: string
  /** 封面标签 */
  coverTag?: string
  /** 封面图片填充模式 */
  coverFit?: 'fill' | 'contain' | 'cover' | 'none' | 'scale-down'
  /** 是否开启预览 */
  preview?: boolean
  /** 卡片变体 */
  variant?: CardVariant
  /** 卡片尺寸 */
  size?: CardSize
  /** 是否显示边框 */
  bordered?: boolean
  /** 是否开启悬浮效果 */
  hoverable?: boolean
  /** 是否加载中 */
  loading?: boolean
  /** 是否选中 */
  selected?: boolean
  /** 自定义宽度 */
  width?: string | number
  /** 自定义高度 */
  height?: string | number
  /** 背景色 */
  background?: string
  /** 内边距 */
  padding?: string | number
  /** 阴影等级 */
  shadow?: 'never' | 'hover' | 'always'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  size: 'default',
  tagType: '',
  coverFit: 'cover',
  bordered: true,
  hoverable: false,
  loading: false,
  selected: false,
  preview: false,
  iconSize: 20,
  shadow: 'hover',
})

const emit = defineEmits<{
  (e: 'click', event: MouseEvent): void
  (e: 'mouseenter', event: MouseEvent): void
  (e: 'mouseleave', event: MouseEvent): void
}>()

const cardStyle = computed<CSSProperties>(() => {
  const style: CSSProperties = {}
  if (props.width) {
    style.width = typeof props.width === 'number' ? `${props.width}px` : props.width
  }
  if (props.height) {
    style.height = typeof props.height === 'number' ? `${props.height}px` : props.height
  }
  if (props.background) {
    style.backgroundColor = props.background
  }
  return style
})

const bodyStyle = computed<CSSProperties>(() => {
  const style: CSSProperties = {}
  if (props.padding) {
    style.padding = typeof props.padding === 'number' ? `${props.padding}px` : props.padding
  }
  return style
})

const handleClick = (event: MouseEvent) => {
  emit('click', event)
}

const handleMouseEnter = (event: MouseEvent) => {
  emit('mouseenter', event)
}

const handleMouseLeave = (event: MouseEvent) => {
  emit('mouseleave', event)
}
</script>

<style scoped lang="scss">
.xh-card {
  position: relative;
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  // 边框
  &--bordered {
    border: 1px solid #e8e8ef;
  }

  // 悬浮效果
  &--hoverable {
    cursor: pointer;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(26, 26, 46, 0.12);
    }
  }

  // 选中状态
  &--selected {
    border-color: #ff6b6b;
    box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.2);
  }

  // 变体样式
  &--primary {
    background: linear-gradient(135deg, #fff0f0 0%, #ffffff 100%);
    border-color: #ffc1c1;

    .xh-card__title {
      color: #ff6b6b;
    }
  }

  &--success {
    background: linear-gradient(135deg, #e6faf5 0%, #ffffff 100%);
    border-color: #1dd1a1;

    .xh-card__title {
      color: #1dd1a1;
    }
  }

  &--warning {
    background: linear-gradient(135deg, #fff5eb 0%, #ffffff 100%);
    border-color: #ff9f43;

    .xh-card__title {
      color: #ff9f43;
    }
  }

  &--danger {
    background: linear-gradient(135deg, #fff0f0 0%, #ffffff 100%);
    border-color: #ff6b6b;

    .xh-card__title {
      color: #e55a5a;
    }
  }

  &--info {
    background: linear-gradient(135deg, #f0f5ff 0%, #ffffff 100%);
    border-color: #54a0ff;

    .xh-card__title {
      color: #54a0ff;
    }
  }

  // 尺寸样式
  &--small {
    .xh-card__header {
      padding: 12px 16px;
    }

    .xh-card__body {
      padding: 12px 16px;
    }

    .xh-card__actions {
      padding: 8px 16px;
    }

    .xh-card__footer {
      padding: 12px 16px;
    }

    .xh-card__title {
      font-size: 14px;
    }
  }

  &--default {
    .xh-card__header {
      padding: 16px 20px;
    }

    .xh-card__body {
      padding: 16px 20px;
    }

    .xh-card__actions {
      padding: 12px 20px;
    }

    .xh-card__footer {
      padding: 16px 20px;
    }

    .xh-card__title {
      font-size: 16px;
    }
  }

  &--large {
    .xh-card__header {
      padding: 20px 24px;
    }

    .xh-card__body {
      padding: 20px 24px;
    }

    .xh-card__actions {
      padding: 16px 24px;
    }

    .xh-card__footer {
      padding: 20px 24px;
    }

    .xh-card__title {
      font-size: 18px;
    }
  }

  // 加载状态
  &--loading {
    pointer-events: none;

    .xh-card__loading {
      position: absolute;
      inset: 0;
      background: rgba(255, 255, 255, 0.8);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 10;
      padding: 20px;
    }
  }

  // 阴影
  box-shadow: v-bind('props.shadow === "always" ? "0 4px 12px rgba(26, 26, 46, 0.08)" : "none"');

  &:hover {
    box-shadow: v-bind('props.shadow === "hover" || props.shadow === "always" ? "0 4px 12px rgba(26, 26, 46, 0.08)" : "none"');
  }
}

.xh-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid transparent;

  .xh-card--bordered & {
    border-bottom-color: #e8e8ef;
  }
}

.xh-card__header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.xh-card__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #fff0f0;
  color: #ff6b6b;
  flex-shrink: 0;
}

.xh-card__title-wrapper {
  flex: 1;
  min-width: 0;
}

.xh-card__title {
  margin: 0;
  font-weight: 600;
  color: #1a1a2e;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.xh-card__subtitle {
  margin: 4px 0 0;
  font-size: 12px;
  color: #8a8aa3;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.xh-card__header-right {
  flex-shrink: 0;
  margin-left: 12px;
}

.xh-card__cover {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;

  &-image {
    width: 100%;
    height: 100%;
    transition: transform 0.3s ease;

    .xh-card--hoverable:hover & {
      transform: scale(1.05);
    }
  }

  &-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5fa;
    color: #b8b8cc;
  }

  &-tag {
    position: absolute;
    top: 12px;
    left: 12px;
    padding: 4px 12px;
    background: #ff6b6b;
    color: #ffffff;
    font-size: 12px;
    font-weight: 500;
    border-radius: 9999px;
  }
}

.xh-card__body {
  flex: 1;
}

.xh-card__actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  border-top: 1px solid #e8e8ef;
}

.xh-card__footer {
  border-top: 1px solid #e8e8ef;
  font-size: 12px;
  color: #8a8aa3;
}
</style>
