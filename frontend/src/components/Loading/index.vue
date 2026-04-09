<template>
  <div 
    v-if="visible" 
    class="loading-wrapper"
    :class="{ 
      'loading--fullscreen': fullscreen,
      'loading--overlay': overlay,
      [`loading--${size}`]: true,
      [`loading--${type}`]: true 
    }"
    :style="customStyle"
  >
    <div class="loading-container">
      <!-- 品牌加载动画 -->
      <template v-if="type === 'brand'">
        <div class="loading-brand">
          <div class="loading-brand__circle">
            <div class="loading-brand__inner" />
          </div>
          <div class="loading-brand__text">{{ text || '小红公寓' }}</div>
        </div>
      </template>

      <!-- 圆点加载动画 -->
      <template v-else-if="type === 'dots'">
        <div class="loading-dots">
          <span v-for="i in 3" :key="i" class="loading-dots__dot" :style="{ animationDelay: `${(i - 1) * 0.16}s` }" />
        </div>
      </template>

      <!-- 旋转加载动画 -->
      <template v-else-if="type === 'spinner'">
        <div class="loading-spinner">
          <svg class="loading-spinner__svg" viewBox="0 0 50 50">
            <circle 
              class="loading-spinner__circle" 
              cx="25" 
              cy="25" 
              r="20" 
              fill="none" 
              stroke-width="4"
            />
          </svg>
        </div>
      </template>

      <!-- 脉冲加载动画 -->
      <template v-else-if="type === 'pulse'">
        <div class="loading-pulse">
          <div v-for="i in 3" :key="i" class="loading-pulse__ring" :style="{ animationDelay: `${(i - 1) * 0.3}s` }" />
        </div>
      </template>

      <!-- 进度条加载 -->
      <template v-else-if="type === 'bar'">
        <div class="loading-bar">
          <div class="loading-bar__track">
            <div class="loading-bar__fill" :style="{ width: `${progress}%` }" />
          </div>
          <div v-if="showProgress" class="loading-bar__text">{{ progress }}%</div>
        </div>
      </template>

      <!-- 骨架屏加载 -->
      <template v-else-if="type === 'skeleton'">
        <Skeleton v-bind="skeletonProps" />
      </template>

      <!-- 加载文本 -->
      <div v-if="text && type !== 'brand'" class="loading-text">{{ text }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 小红公寓claw - Loading 加载组件
 * @description 多种加载动画效果，提升用户等待体验
 * @author 小红
 */
import { computed } from 'vue'
import Skeleton from '../Skeleton/index.vue'

interface SkeletonProps {
  variant?: 'text' | 'circle' | 'rect' | 'card' | 'list' | 'table' | 'dashboard'
  rows?: number
  animated?: boolean
}

interface Props {
  /** 是否显示加载 */
  visible?: boolean
  /** 加载类型 */
  type?: 'brand' | 'dots' | 'spinner' | 'pulse' | 'bar' | 'skeleton'
  /** 尺寸 */
  size?: 'small' | 'default' | 'large'
  /** 加载文本 */
  text?: string
  /** 是否全屏显示 */
  fullscreen?: boolean
  /** 是否显示遮罩层 */
  overlay?: boolean
  /** 遮罩层颜色 */
  overlayColor?: string
  /** 自定义背景色 */
  background?: string
  
  // 进度条参数
  /** 进度值 (0-100) */
  progress?: number
  /** 是否显示进度文本 */
  showProgress?: boolean
  
  // 骨架屏参数
  /** 骨架屏配置 */
  skeletonProps?: SkeletonProps
}

const props = withDefaults(defineProps<Props>(), {
  visible: true,
  type: 'brand',
  size: 'default',
  text: '',
  fullscreen: false,
  overlay: true,
  overlayColor: 'rgba(255, 255, 255, 0.9)',
  background: 'transparent',
  progress: 0,
  showProgress: true,
  skeletonProps: () => ({
    variant: 'dashboard',
    animated: true
  })
})

const customStyle = computed(() => {
  const style: Record<string, string> = {}
  if (props.overlay && props.overlayColor) {
    style.background = props.overlayColor
  } else if (props.background) {
    style.background = props.background
  }
  return style
})
</script>

<style scoped lang="scss">
// 品牌色变量
$brand-primary: #FF6B6B;
$brand-secondary: #FF9F43;
$brand-gradient: linear-gradient(135deg, $brand-primary 0%, $brand-secondary 100%);

// 基础样式
.loading-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.loading-text {
  font-size: 14px;
  color: #606266;
  text-align: center;
}

// 全屏模式
.loading--fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
}

// 遮罩模式
.loading--overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
}

// 尺寸变体
.loading--small {
  .loading-brand__circle {
    width: 32px;
    height: 32px;
  }
  
  .loading-dots__dot {
    width: 6px;
    height: 6px;
  }
  
  .loading-spinner__svg {
    width: 24px;
    height: 24px;
  }
  
  .loading-pulse__ring {
    width: 30px;
    height: 30px;
  }
  
  .loading-text {
    font-size: 12px;
  }
}

.loading--large {
  .loading-brand__circle {
    width: 64px;
    height: 64px;
  }
  
  .loading-dots__dot {
    width: 12px;
    height: 12px;
  }
  
  .loading-spinner__svg {
    width: 56px;
    height: 56px;
  }
  
  .loading-pulse__ring {
    width: 60px;
    height: 60px;
  }
  
  .loading-text {
    font-size: 16px;
  }
}

// ========== 品牌加载动画 ==========
.loading-brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;

  &__circle {
    width: 48px;
    height: 48px;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 50%;
      background: $brand-gradient;
      animation: brand-pulse 2s ease-in-out infinite;
    }
  }

  &__inner {
    position: absolute;
    inset: 4px;
    border-radius: 50%;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    
    &::after {
      content: '';
      width: 60%;
      height: 60%;
      border: 3px solid transparent;
      border-top-color: $brand-primary;
      border-radius: 50%;
      animation: brand-spin 1s linear infinite;
    }
  }

  &__text {
    font-size: 14px;
    font-weight: 500;
    color: $brand-primary;
    letter-spacing: 2px;
  }
}

@keyframes brand-pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

@keyframes brand-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// ========== 圆点加载动画 ==========
.loading-dots {
  display: flex;
  gap: 6px;

  &__dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: $brand-primary;
    animation: dot-bounce 1.4s ease-in-out infinite both;
  }
}

@keyframes dot-bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

// ========== 旋转加载动画 ==========
.loading-spinner {
  &__svg {
    width: 40px;
    height: 40px;
    animation: spinner-rotate 2s linear infinite;
  }

  &__circle {
    stroke: $brand-primary;
    stroke-linecap: round;
    animation: spinner-dash 1.5s ease-in-out infinite;
  }
}

@keyframes spinner-rotate {
  100% { transform: rotate(360deg); }
}

@keyframes spinner-dash {
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

// ========== 脉冲加载动画 ==========
.loading-pulse {
  position: relative;
  width: 40px;
  height: 40px;

  &__ring {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    border: 3px solid $brand-primary;
    opacity: 0;
    animation: pulse-ring 1.5s cubic-bezier(0.215, 0.61, 0.355, 1) infinite;
  }
}

@keyframes pulse-ring {
  0% {
    transform: scale(0.33);
    opacity: 1;
  }
  80%, 100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

// ========== 进度条加载 ==========
.loading-bar {
  width: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;

  &__track {
    width: 100%;
    height: 6px;
    background: #e4e7ed;
    border-radius: 3px;
    overflow: hidden;
  }

  &__fill {
    height: 100%;
    background: $brand-gradient;
    border-radius: 3px;
    transition: width 0.3s ease;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      width: 20px;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4));
      animation: bar-shine 1s linear infinite;
    }
  }

  &__text {
    font-size: 12px;
    color: $brand-primary;
    font-weight: 500;
  }
}

@keyframes bar-shine {
  from { transform: translateX(-20px); }
  to { transform: translateX(20px); }
}
</style>
