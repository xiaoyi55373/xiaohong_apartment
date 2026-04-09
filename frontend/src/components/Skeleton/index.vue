<template>
  <div class="skeleton-wrapper" :class="{ 'skeleton--animated': animated, [`skeleton--${variant}`]: true }">
    <!-- 文本骨架 -->
    <template v-if="variant === 'text'">
      <div 
        v-for="i in rows" 
        :key="i" 
        class="skeleton-text"
        :style="{ width: i === rows && lastLineWidth ? lastLineWidth : '100%', height: `${lineHeight}px` }"
      />
    </template>

    <!-- 圆形骨架 -->
    <div 
      v-else-if="variant === 'circle'" 
      class="skeleton-circle"
      :style="{ width: `${size}px`, height: `${size}px` }"
    />

    <!-- 矩形骨架 -->
    <div 
      v-else-if="variant === 'rect'" 
      class="skeleton-rect"
      :style="{ width: width, height: height }"
    />

    <!-- 卡片骨架 -->
    <div v-else-if="variant === 'card'" class="skeleton-card">
      <div v-if="showImage" class="skeleton-card__image" :style="{ height: imageHeight }" />
      <div class="skeleton-card__content">
        <div class="skeleton-card__title" :style="{ width: titleWidth }" />
        <div v-for="i in contentLines" :key="i" class="skeleton-card__line" />
      </div>
    </div>

    <!-- 列表骨架 -->
    <div v-else-if="variant === 'list'" class="skeleton-list">
      <div v-for="i in listItems" :key="i" class="skeleton-list__item">
        <div v-if="showAvatar" class="skeleton-list__avatar" :style="{ width: `${avatarSize}px`, height: `${avatarSize}px` }" />
        <div class="skeleton-list__content">
          <div class="skeleton-list__title" :style="{ width: titleWidth }" />
          <div class="skeleton-list__subtitle" :style="{ width: subtitleWidth }" />
        </div>
      </div>
    </div>

    <!-- 表格骨架 -->
    <div v-else-if="variant === 'table'" class="skeleton-table">
      <div class="skeleton-table__header">
        <div v-for="i in columns" :key="i" class="skeleton-table__cell" :style="{ width: `${100 / columns}%` }" />
      </div>
      <div v-for="row in tableRows" :key="row" class="skeleton-table__row">
        <div v-for="i in columns" :key="i" class="skeleton-table__cell" :style="{ width: `${100 / columns}%` }" />
      </div>
    </div>

    <!-- 仪表盘骨架 -->
    <div v-else-if="variant === 'dashboard'" class="skeleton-dashboard">
      <div class="skeleton-dashboard__stats">
        <div v-for="i in 4" :key="i" class="skeleton-dashboard__stat-card">
          <div class="skeleton-dashboard__stat-icon" />
          <div class="skeleton-dashboard__stat-content">
            <div class="skeleton-dashboard__stat-value" />
            <div class="skeleton-dashboard__stat-label" />
          </div>
        </div>
      </div>
      <div class="skeleton-dashboard__charts">
        <div class="skeleton-dashboard__chart" :style="{ height: chartHeight }" />
        <div class="skeleton-dashboard__chart" :style="{ height: chartHeight }" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 小红公寓claw - Skeleton 骨架屏组件
 * @description 用于在内容加载时展示占位符，提升用户体验
 * @author 小红
 */

interface Props {
  /** 骨架屏变体类型 */
  variant?: 'text' | 'circle' | 'rect' | 'card' | 'list' | 'table' | 'dashboard'
  /** 是否显示动画效果 */
  animated?: boolean
  
  // 文本变体参数
  /** 文本行数 */
  rows?: number
  /** 行高 */
  lineHeight?: number
  /** 最后一行宽度 */
  lastLineWidth?: string
  
  // 圆形/矩形变体参数
  /** 尺寸（圆形） */
  size?: number
  /** 宽度（矩形） */
  width?: string
  /** 高度（矩形） */
  height?: string
  
  // 卡片变体参数
  /** 是否显示图片 */
  showImage?: boolean
  /** 图片高度 */
  imageHeight?: string
  /** 标题宽度 */
  titleWidth?: string
  /** 内容行数 */
  contentLines?: number
  
  // 列表变体参数
  /** 列表项数量 */
  listItems?: number
  /** 是否显示头像 */
  showAvatar?: boolean
  /** 头像尺寸 */
  avatarSize?: number
  /** 副标题宽度 */
  subtitleWidth?: string
  
  // 表格变体参数
  /** 列数 */
  columns?: number
  /** 行数 */
  tableRows?: number
  
  // 仪表盘变体参数
  /** 图表高度 */
  chartHeight?: string
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'text',
  animated: true,
  rows: 3,
  lineHeight: 16,
  lastLineWidth: '60%',
  size: 40,
  width: '100%',
  height: '100px',
  showImage: true,
  imageHeight: '200px',
  titleWidth: '40%',
  contentLines: 2,
  listItems: 4,
  showAvatar: true,
  avatarSize: 40,
  subtitleWidth: '70%',
  columns: 4,
  tableRows: 5,
  chartHeight: '300px'
})
</script>

<style scoped lang="scss">
// 品牌色变量
$brand-primary: #FF6B6B;
$brand-secondary: #FF9F43;
$skeleton-base: #f0f2f5;
$skeleton-highlight: #e4e7ed;

.skeleton-wrapper {
  width: 100%;
}

// 基础骨架样式
%skeleton-base {
  background: $skeleton-base;
  border-radius: 4px;
}

// 动画效果
.skeleton--animated {
  .skeleton-text,
  .skeleton-circle,
  .skeleton-rect,
  .skeleton-card__image,
  .skeleton-card__title,
  .skeleton-card__line,
  .skeleton-list__avatar,
  .skeleton-list__title,
  .skeleton-list__subtitle,
  .skeleton-table__cell,
  .skeleton-dashboard__stat-icon,
  .skeleton-dashboard__stat-value,
  .skeleton-dashboard__stat-label,
  .skeleton-dashboard__chart {
    background: linear-gradient(
      90deg,
      $skeleton-base 25%,
      $skeleton-highlight 50%,
      $skeleton-base 75%
    );
    background-size: 200% 100%;
    animation: skeleton-loading 1.5s ease-in-out infinite;
  }
}

@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

// 文本骨架
.skeleton-text {
  @extend %skeleton-base;
  margin-bottom: 12px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

// 圆形骨架
.skeleton-circle {
  @extend %skeleton-base;
  border-radius: 50%;
}

// 矩形骨架
.skeleton-rect {
  @extend %skeleton-base;
}

// 卡片骨架
.skeleton-card {
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

  &__image {
    @extend %skeleton-base;
    width: 100%;
  }

  &__content {
    padding: 16px;
  }

  &__title {
    @extend %skeleton-base;
    height: 20px;
    margin-bottom: 12px;
  }

  &__line {
    @extend %skeleton-base;
    height: 14px;
    margin-bottom: 8px;

    &:last-child {
      width: 60%;
    }
  }
}

// 列表骨架
.skeleton-list {
  &__item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }
  }

  &__avatar {
    @extend %skeleton-base;
    border-radius: 50%;
    margin-right: 12px;
    flex-shrink: 0;
  }

  &__content {
    flex: 1;
  }

  &__title {
    @extend %skeleton-base;
    height: 16px;
    margin-bottom: 8px;
  }

  &__subtitle {
    @extend %skeleton-base;
    height: 12px;
  }
}

// 表格骨架
.skeleton-table {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;

  &__header {
    display: flex;
    background: #f5f7fa;
    padding: 12px;
  }

  &__row {
    display: flex;
    padding: 12px;
    border-top: 1px solid #e4e7ed;
  }

  &__cell {
    @extend %skeleton-base;
    height: 16px;
    margin: 0 8px;
    flex: 1;

    &:first-child {
      margin-left: 0;
    }

    &:last-child {
      margin-right: 0;
    }
  }
}

// 仪表盘骨架
.skeleton-dashboard {
  &__stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  &__stat-card {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  }

  &__stat-icon {
    @extend %skeleton-base;
    width: 48px;
    height: 48px;
    border-radius: 8px;
    margin-right: 16px;
    flex-shrink: 0;
  }

  &__stat-content {
    flex: 1;
  }

  &__stat-value {
    @extend %skeleton-base;
    height: 24px;
    width: 60%;
    margin-bottom: 8px;
  }

  &__stat-label {
    @extend %skeleton-base;
    height: 14px;
    width: 40%;
  }

  &__charts {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;

    @media (max-width: 1200px) {
      grid-template-columns: 1fr;
    }
  }

  &__chart {
    @extend %skeleton-base;
    border-radius: 8px;
  }
}
</style>
