<template>
  <div
    :class="[
      'xh-list',
      `xh-list--${layout}`,
      `xh-list--${size}`,
      {
        'xh-list--bordered': bordered,
        'xh-list--loading': loading,
        'xh-list--empty': isEmpty,
      },
    ]"
  >
    <!-- 列表头部 -->
    <div v-if="$slots.header || title" class="xh-list__header">
      <slot name="header">
        <div class="xh-list__title">{{ title }}</div>
        <div v-if="extra" class="xh-list__extra">{{ extra }}</div>
      </slot>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="xh-list__loading">
      <slot name="loading">
        <div v-for="i in loadingCount" :key="i" class="xh-list__loading-item">
          <el-skeleton :rows="2" animated />
        </div>
      </slot>
    </div>

    <!-- 空状态 -->
    <div v-else-if="isEmpty" class="xh-list__empty">
      <slot name="empty">
        <el-empty :description="emptyText" />
      </slot>
    </div>

    <!-- 列表内容 -->
    <template v-else>
      <!-- 网格布局 -->
      <div
        v-if="layout === 'grid'"
        class="xh-list__grid"
        :style="gridStyle"
      >
        <div
          v-for="(item, index) in dataSource"
          :key="getRowKey(item, index)"
          :class="['xh-list__grid-item', { 'xh-list__grid-item--hover': hover }]"
          @click="handleItemClick(item, index, $event)"
        >
          <slot name="renderItem" :item="item" :index="index">
            <XhCard
              :title="item[titleField]"
              :subtitle="item[subtitleField]"
              :hoverable="hover"
              @click="handleItemClick(item, index, $event)"
            >
              <template v-if="item[descriptionField]">
                {{ item[descriptionField] }}
              </template>
            </XhCard>
          </slot>
        </div>
      </div>

      <!-- 列表布局 -->
      <div v-else class="xh-list__items">
        <div
          v-for="(item, index) in dataSource"
          :key="getRowKey(item, index)"
          :class="[
            'xh-list__item',
            {
              'xh-list__item--hover': hover,
              'xh-list__item--active': isActive(item, index),
              'xh-list__item--disabled': isDisabled(item, index),
            },
          ]"
          @click="handleItemClick(item, index, $event)"
        >
          <slot name="renderItem" :item="item" :index="index">
            <div class="xh-list__item-content">
              <div v-if="item[avatarField]" class="xh-list__item-avatar">
                <el-avatar :src="item[avatarField]" :size="avatarSize" />
              </div>
              <div class="xh-list__item-main">
                <div class="xh-list__item-title">{{ item[titleField] }}</div>
                <div v-if="item[descriptionField]" class="xh-list__item-description">
                  {{ item[descriptionField] }}
                </div>
              </div>
              <div v-if="item[extraField]" class="xh-list__item-extra">
                {{ item[extraField] }}
              </div>
            </div>
          </slot>
        </div>
      </div>
    </template>

    <!-- 分页 -->
    <div v-if="pagination && !loading" class="xh-list__pagination">
      <slot name="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="pageSizes"
          :layout="paginationLayout"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </slot>
    </div>

    <!-- 加载更多 -->
    <div
      v-if="loadMore && !loading && !isEmpty"
      class="xh-list__load-more"
      @click="handleLoadMore"
    >
      <slot name="loadMore">
        <el-button
          :loading="loadingMore"
          type="primary"
          link
          :icon="loadingMore ? 'Loading' : 'ArrowDown'"
        >
          {{ loadingMore ? '加载中...' : '加载更多' }}
        </el-button>
      </slot>
    </div>

    <!-- 列表底部 -->
    <div v-if="$slots.footer" class="xh-list__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import XhCard from '../Card/index.vue'

type ListLayout = 'list' | 'grid'
type ListSize = 'small' | 'default' | 'large'
type AvatarSize = 'small' | 'default' | 'large' | number

interface ListItem {
  [key: string]: any
}

interface PaginationConfig {
  current?: number
  pageSize?: number
  total?: number
  pageSizes?: number[]
  layout?: string
}

interface Props {
  /** 列表数据源 */
  dataSource: ListItem[]
  /** 列表标题 */
  title?: string
  /** 额外内容 */
  extra?: string
  /** 布局方式 */
  layout?: ListLayout
  /** 列表尺寸 */
  size?: ListSize
  /** 是否显示边框 */
  bordered?: boolean
  /** 是否开启悬浮效果 */
  hover?: boolean
  /** 是否加载中 */
  loading?: boolean
  /** 加载占位数量 */
  loadingCount?: number
  /** 空状态文本 */
  emptyText?: string
  /** 唯一标识字段 */
  rowKey?: string | ((item: ListItem, index: number) => string | number)
  /** 标题字段 */
  titleField?: string
  /** 副标题字段 */
  subtitleField?: string
  /** 描述字段 */
  descriptionField?: string
  /** 额外信息字段 */
  extraField?: string
  /** 头像字段 */
  avatarField?: string
  /** 头像尺寸 */
  avatarSize?: AvatarSize
  /** 当前选中项 */
  activeKey?: string | number
  /** 禁用判断函数 */
  disabled?: (item: ListItem, index: number) => boolean
  /** 分页配置 */
  pagination?: boolean | PaginationConfig
  /** 是否支持加载更多 */
  loadMore?: boolean
  /** 加载更多中 */
  loadingMore?: boolean
  /** 网格列数 */
  grid?: number | { gutter?: number; xs?: number; sm?: number; md?: number; lg?: number; xl?: number }
}

const props = withDefaults(defineProps<Props>(), {
  layout: 'list',
  size: 'default',
  bordered: false,
  hover: true,
  loading: false,
  loadingCount: 3,
  emptyText: '暂无数据',
  titleField: 'title',
  subtitleField: 'subtitle',
  descriptionField: 'description',
  extraField: 'extra',
  avatarField: 'avatar',
  avatarSize: 'default',
  loadMore: false,
  loadingMore: false,
  grid: 3,
})

const emit = defineEmits<{
  (e: 'click', item: ListItem, index: number, event: MouseEvent): void
  (e: 'change', page: number, pageSize: number): void
  (e: 'loadMore'): void
}>()

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

// 监听分页配置
watch(
  () => props.pagination,
  (val) => {
    if (typeof val === 'object') {
      currentPage.value = val.current || 1
      pageSize.value = val.pageSize || 10
    }
  },
  { immediate: true }
)

const isEmpty = computed(() => !props.dataSource || props.dataSource.length === 0)

const total = computed(() => {
  if (typeof props.pagination === 'object') {
    return props.pagination.total || props.dataSource.length
  }
  return props.dataSource.length
})

const pageSizes = computed(() => {
  if (typeof props.pagination === 'object') {
    return props.pagination.pageSizes || [10, 20, 50, 100]
  }
  return [10, 20, 50, 100]
})

const paginationLayout = computed(() => {
  if (typeof props.pagination === 'object') {
    return props.pagination.layout || 'total, sizes, prev, pager, next, jumper'
  }
  return 'total, sizes, prev, pager, next, jumper'
})

const gridStyle = computed(() => {
  const style: Record<string, string> = {}
  const grid = props.grid

  if (typeof grid === 'number') {
    style['--grid-columns'] = String(grid)
    style['--grid-gap'] = '16px'
  } else if (typeof grid === 'object') {
    style['--grid-columns'] = String(grid.lg || 3)
    style['--grid-gap'] = `${grid.gutter || 16}px`
  }

  return style
})

const getRowKey = (item: ListItem, index: number): string | number => {
  if (typeof props.rowKey === 'function') {
    return props.rowKey(item, index)
  }
  if (typeof props.rowKey === 'string') {
    return item[props.rowKey] ?? index
  }
  return index
}

const isActive = (item: ListItem, index: number): boolean => {
  const key = getRowKey(item, index)
  return key === props.activeKey
}

const isDisabled = (item: ListItem, index: number): boolean => {
  if (typeof props.disabled === 'function') {
    return props.disabled(item, index)
  }
  return false
}

const handleItemClick = (item: ListItem, index: number, event: MouseEvent) => {
  if (isDisabled(item, index)) return
  emit('click', item, index, event)
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  emit('change', currentPage.value, val)
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  emit('change', val, pageSize.value)
}

const handleLoadMore = () => {
  emit('loadMore')
}
</script>

<style scoped lang="scss">
.xh-list {
  width: 100%;

  &--bordered {
    border: 1px solid #e8e8ef;
    border-radius: 12px;
    overflow: hidden;
  }

  &--loading {
    .xh-list__items,
    .xh-list__grid {
      opacity: 0.6;
    }
  }
}

.xh-list__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8ef;
  background: #fafafa;
}

.xh-list__title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.xh-list__extra {
  font-size: 14px;
  color: #8a8aa3;
}

.xh-list__loading {
  padding: 20px;
}

.xh-list__loading-item {
  padding: 16px 0;
  border-bottom: 1px solid #e8e8ef;

  &:last-child {
    border-bottom: none;
  }
}

.xh-list__empty {
  padding: 40px 20px;
}

// 列表布局
.xh-list__items {
  .xh-list--small & {
    .xh-list__item {
      padding: 12px 16px;
    }
  }

  .xh-list--default & {
    .xh-list__item {
      padding: 16px 20px;
    }
  }

  .xh-list--large & {
    .xh-list__item {
      padding: 20px 24px;
    }
  }
}

.xh-list__item {
  display: flex;
  align-items: center;
  transition: all 0.2s ease;
  border-bottom: 1px solid #e8e8ef;

  &:last-child {
    border-bottom: none;
  }

  &--hover {
    cursor: pointer;

    &:hover:not(.xh-list__item--disabled) {
      background: #fafafa;
    }
  }

  &--active {
    background: #fff0f0;

    &:hover {
      background: #ffe0e0;
    }
  }

  &--disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.xh-list__item-content {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 12px;
}

.xh-list__item-avatar {
  flex-shrink: 0;
}

.xh-list__item-main {
  flex: 1;
  min-width: 0;
}

.xh-list__item-title {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.xh-list__item-description {
  margin-top: 4px;
  font-size: 12px;
  color: #8a8aa3;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.xh-list__item-extra {
  flex-shrink: 0;
  font-size: 12px;
  color: #8a8aa3;
}

// 网格布局
.xh-list__grid {
  display: grid;
  grid-template-columns: repeat(var(--grid-columns, 3), 1fr);
  gap: var(--grid-gap, 16px);
  padding: 20px;
}

.xh-list__grid-item {
  &--hover {
    cursor: pointer;
  }
}

// 分页
.xh-list__pagination {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid #e8e8ef;
}

// 加载更多
.xh-list__load-more {
  display: flex;
  justify-content: center;
  padding: 16px 20px;
  border-top: 1px solid #e8e8ef;
}

.xh-list__footer {
  padding: 16px 20px;
  border-top: 1px solid #e8e8ef;
  background: #fafafa;
}
</style>
