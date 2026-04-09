<template>
  <div class="xh-detail-item" :class="itemClasses">
    <!-- 标签区域 -->
    <div class="xh-detail-item__label" :style="labelStyle">
      <el-icon v-if="icon" class="xh-detail-item__icon">
        <component :is="icon" />
      </el-icon>
      <span class="xh-detail-item__label-text">{{ label }}</span>
      <span v-if="required" class="xh-detail-item__required">*</span>
    </div>
    
    <!-- 内容区域 -->
    <div class="xh-detail-item__content" :style="contentStyle">
      <!-- 默认内容 -->
      <slot>
        <!-- 文本内容 -->
        <template v-if="type === 'text'">
          <span class="xh-detail-item__text" :class="{ 'is-empty': !value }">
            {{ displayValue }}
          </span>
        </template>
        
        <!-- 标签内容 -->
        <template v-else-if="type === 'tag'">
          <el-tag 
            :type="tagType" 
            :effect="tagEffect"
            :size="tagSize"
            class="xh-detail-item__tag"
          >
            {{ displayValue }}
          </el-tag>
        </template>
        
        <!-- 图片内容 -->
        <template v-else-if="type === 'image'">
          <div class="xh-detail-item__images">
            <el-image
              v-for="(img, index) in imageList"
              :key="index"
              :src="img"
              :preview-src-list="imageList"
              :initial-index="index"
              fit="cover"
              class="xh-detail-item__image"
              :style="imageStyle"
            />
          </div>
        </template>
        
        <!-- 链接内容 -->
        <template v-else-if="type === 'link'">
          <a 
            :href="value" 
            target="_blank" 
            class="xh-detail-item__link"
          >
            {{ displayValue }}
            <el-icon><Link /></el-icon>
          </a>
        </template>
        
        <!-- 金额内容 -->
        <template v-else-if="type === 'money'">
          <span class="xh-detail-item__money">
            <span class="xh-detail-item__currency">¥</span>
            <span class="xh-detail-item__amount">{{ formatMoney(value) }}</span>
          </span>
        </template>
        
        <!-- 日期内容 -->
        <template v-else-if="type === 'date'">
          <span class="xh-detail-item__date">
            <el-icon><Calendar /></el-icon>
            {{ displayValue }}
          </span>
        </template>
        
        <!-- 状态内容 -->
        <template v-else-if="type === 'status'">
          <div class="xh-detail-item__status" :class="`is-${statusType}`">
            <span class="xh-detail-item__status-dot"></span>
            <span class="xh-detail-item__status-text">{{ displayValue }}</span>
          </div>
        </template>
        
        <!-- 列表内容 -->
        <template v-else-if="type === 'list'">
          <div class="xh-detail-item__list">
            <el-tag
              v-for="(item, index) in listValue"
              :key="index"
              :type="listTagType"
              effect="light"
              size="small"
              class="xh-detail-item__list-item"
            >
              {{ item }}
            </el-tag>
          </div>
        </template>
      </slot>
    </div>
    
    <!-- 额外内容 -->
    <div v-if="$slots.extra" class="xh-detail-item__extra">
      <slot name="extra" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Link, Calendar } from '@element-plus/icons-vue'

export type DetailItemType = 'text' | 'tag' | 'image' | 'link' | 'money' | 'date' | 'status' | 'list'
export type DetailItemLayout = 'horizontal' | 'vertical' | 'inline'

export interface DetailItemProps {
  /** 标签文本 */
  label?: string
  /** 值 */
  value?: any
  /** 类型 */
  type?: DetailItemType
  /** 布局方式 */
  layout?: DetailItemLayout
  /** 标签宽度 */
  labelWidth?: string | number
  /** 内容宽度 */
  contentWidth?: string | number
  /** 图标 */
  icon?: string
  /** 是否必填 */
  required?: boolean
  /** 为空时显示文本 */
  emptyText?: string
  
  // Tag 类型专属属性
  /** 标签类型 */
  tagType?: 'success' | 'warning' | 'danger' | 'info' | 'primary'
  /** 标签效果 */
  tagEffect?: 'light' | 'dark' | 'plain'
  /** 标签尺寸 */
  tagSize?: 'large' | 'default' | 'small'
  
  // 图片类型专属属性
  /** 图片列表 */
  images?: string[]
  /** 图片宽度 */
  imageWidth?: string | number
  /** 图片高度 */
  imageHeight?: string | number
  
  // 状态类型专属属性
  /** 状态类型 */
  statusType?: 'success' | 'warning' | 'danger' | 'info' | 'primary'
  
  // 列表类型专属属性
  /** 列表数据 */
  list?: string[]
  /** 列表标签类型 */
  listTagType?: 'success' | 'warning' | 'danger' | 'info' | 'primary'
}

const props = withDefaults(defineProps<DetailItemProps>(), {
  type: 'text',
  layout: 'horizontal',
  emptyText: '-',
  tagType: 'primary',
  tagEffect: 'light',
  tagSize: 'default',
  imageWidth: 80,
  imageHeight: 80,
  statusType: 'primary',
  listTagType: 'info'
})

// 计算属性
const itemClasses = computed(() => ({
  [`xh-detail-item--${props.layout}`]: true,
  [`xh-detail-item--${props.type}`]: true,
  'is-empty': !props.value && props.type === 'text'
}))

const labelStyle = computed(() => {
  const styles: Record<string, string> = {}
  if (props.labelWidth) {
    styles.width = typeof props.labelWidth === 'number' ? `${props.labelWidth}px` : props.labelWidth
    styles.flexShrink = '0'
  }
  return styles
})

const contentStyle = computed(() => {
  const styles: Record<string, string> = {}
  if (props.contentWidth) {
    styles.width = typeof props.contentWidth === 'number' ? `${props.contentWidth}px` : props.contentWidth
  }
  return styles
})

const displayValue = computed(() => {
  if (props.value === undefined || props.value === null || props.value === '') {
    return props.emptyText
  }
  return props.value
})

const imageList = computed(() => {
  if (props.images && props.images.length > 0) {
    return props.images
  }
  if (typeof props.value === 'string') {
    return [props.value]
  }
  if (Array.isArray(props.value)) {
    return props.value
  }
  return []
})

const imageStyle = computed(() => {
  const width = typeof props.imageWidth === 'number' ? `${props.imageWidth}px` : props.imageWidth
  const height = typeof props.imageHeight === 'number' ? `${props.imageHeight}px` : props.imageHeight
  return { width, height }
})

const listValue = computed(() => {
  if (props.list && props.list.length > 0) {
    return props.list
  }
  if (Array.isArray(props.value)) {
    return props.value
  }
  return []
})

// 格式化金额
const formatMoney = (value: any) => {
  if (!value) return '0.00'
  const num = parseFloat(value)
  if (isNaN(num)) return '0.00'
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
</script>

<style scoped lang="scss">
.xh-detail-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 12px 0;
  transition: all 0.3s ease;

  // 布局变体
  &--horizontal {
    flex-direction: row;
    align-items: flex-start;

    .xh-detail-item__label {
      min-width: 100px;
      text-align: right;
    }

    .xh-detail-item__content {
      flex: 1;
    }
  }

  &--vertical {
    flex-direction: column;
    gap: 8px;

    .xh-detail-item__label {
      text-align: left;
    }

    .xh-detail-item__content {
      width: 100%;
    }
  }

  &--inline {
    display: inline-flex;
    padding: 8px 16px;
    background: #FAFAFC;
    border-radius: 8px;
    margin-right: 12px;
    margin-bottom: 12px;

    .xh-detail-item__label {
      min-width: auto;
      text-align: left;
    }
  }

  // 标签区域
  &__label {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #8A8AA3;
    font-size: 14px;
    font-weight: 500;
    line-height: 1.5;
  }

  &__icon {
    font-size: 16px;
    color: #FF6B6B;
  }

  &__label-text {
    white-space: nowrap;
  }

  &__required {
    color: #FF6B6B;
    font-weight: 600;
  }

  // 内容区域
  &__content {
    color: #1A1A2E;
    font-size: 14px;
    line-height: 1.6;
    word-break: break-all;
  }

  // 文本类型
  &__text {
    color: #1A1A2E;
    font-weight: 500;

    &.is-empty {
      color: #B8B8C8;
      font-weight: 400;
    }
  }

  // 标签类型
  &__tag {
    border-radius: 6px;
    font-weight: 500;
  }

  // 图片类型
  &__images {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }

  &__image {
    border-radius: 10px;
    overflow: hidden;
    border: 1px solid #E8E8EF;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      transform: scale(1.05);
      border-color: #FF8E8E;
      box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
    }

    :deep(img) {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  // 链接类型
  &__link {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    color: #FF6B6B;
    text-decoration: none;
    font-weight: 500;
    transition: all 0.3s ease;

    .el-icon {
      font-size: 14px;
    }

    &:hover {
      color: #FF5252;
      text-decoration: underline;
    }
  }

  // 金额类型
  &__money {
    display: inline-flex;
    align-items: baseline;
    gap: 2px;
  }

  &__currency {
    font-size: 14px;
    color: #FF6B6B;
    font-weight: 600;
  }

  &__amount {
    font-size: 18px;
    color: #FF6B6B;
    font-weight: 700;
    font-family: 'Roboto Mono', monospace;
  }

  // 日期类型
  &__date {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    color: #1A1A2E;
    font-weight: 500;

    .el-icon {
      font-size: 16px;
      color: #8A8AA3;
    }
  }

  // 状态类型
  &__status {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 500;

    &-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
    }

    &.is-success {
      background: #F6FFED;
      color: #52C41A;

      .xh-detail-item__status-dot {
        background: #52C41A;
        box-shadow: 0 0 0 2px rgba(82, 196, 26, 0.2);
      }
    }

    &.is-warning {
      background: #FFFBE6;
      color: #FAAD14;

      .xh-detail-item__status-dot {
        background: #FAAD14;
        box-shadow: 0 0 0 2px rgba(250, 173, 20, 0.2);
      }
    }

    &.is-danger {
      background: #FFF2F0;
      color: #FF4D4F;

      .xh-detail-item__status-dot {
        background: #FF4D4F;
        box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.2);
      }
    }

    &.is-info {
      background: #F0F5FF;
      color: #4A7CFF;

      .xh-detail-item__status-dot {
        background: #4A7CFF;
        box-shadow: 0 0 0 2px rgba(74, 124, 255, 0.2);
      }
    }

    &.is-primary {
      background: #FFF0F0;
      color: #FF6B6B;

      .xh-detail-item__status-dot {
        background: #FF6B6B;
        box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.2);
      }
    }
  }

  // 列表类型
  &__list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  &__list-item {
    border-radius: 6px;
  }

  // 额外内容
  &__extra {
    margin-left: auto;
    flex-shrink: 0;
  }
}

// 详情信息组
.xh-detail-group {
  display: grid;
  gap: 0 24px;

  &--2 {
    grid-template-columns: repeat(2, 1fr);
  }

  &--3 {
    grid-template-columns: repeat(3, 1fr);
  }

  &--4 {
    grid-template-columns: repeat(4, 1fr);
  }

  @media (max-width: 1200px) {
    &--4 {
      grid-template-columns: repeat(3, 1fr);
    }
  }

  @media (max-width: 992px) {
    &--3,
    &--4 {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  @media (max-width: 768px) {
    &--2,
    &--3,
    &--4 {
      grid-template-columns: 1fr;
    }
  }
}

// 详情分隔线
.xh-detail-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, #E8E8EF 20%, #E8E8EF 80%, transparent 100%);
  margin: 16px 0;
}

// 详情区块标题
.xh-detail-section {
  margin-bottom: 24px;

  &__title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: #1A1A2E;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #F0F0F5;

    .el-icon {
      font-size: 18px;
      color: #FF6B6B;
    }
  }

  &__content {
    padding: 0 8px;
  }
}

// 响应式适配
@media (max-width: 768px) {
  .xh-detail-item {
    flex-direction: column;
    gap: 8px;
    padding: 16px 0;

    &__label {
      text-align: left;
      min-width: auto;
    }

    &__content {
      width: 100%;
    }

    &--horizontal {
      .xh-detail-item__label {
        text-align: left;
      }
    }

    &__images {
      justify-content: flex-start;
    }

    &__image {
      width: 70px !important;
      height: 70px !important;
    }
  }
}
</style>
