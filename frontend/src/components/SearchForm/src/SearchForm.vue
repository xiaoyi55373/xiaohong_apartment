<template>
  <div class="search-form-wrapper">
    <!-- 基础搜索区域 -->
    <div class="card table-search" v-if="columns.length">
      <div class="search-header">
        <div class="search-title">
          <el-icon><Search /></el-icon>
          <span>条件筛选</span>
        </div>
        <div class="search-actions">
          <el-button
            v-if="advancedColumns.length > 0"
            type="primary"
            link
            @click="showAdvanced = !showAdvanced"
          >
            <el-icon><Filter /></el-icon>
            {{ showAdvanced ? '收起高级筛选' : '高级筛选' }}
          </el-button>
          <el-tooltip content="筛选历史" placement="top">
            <el-icon class="action-icon" @click="showHistory = !showHistory">
              <Clock />
            </el-icon>
          </el-tooltip>
        </div>
      </div>

      <el-form ref="formRef" :model="searchParam">
        <Grid
          ref="gridRef"
          :collapsed="collapsed"
          :gap="[20, 0]"
          :cols="searchCol"
        >
          <GridItem
            v-for="(item, index) in columns"
            :key="item.prop"
            v-bind="getResponsive(item)"
            :index="index"
          >
            <el-form-item :label="`${item.label} :`">
              <SearchFormItem :column="item" :searchParam="searchParam" />
            </el-form-item>
          </GridItem>
          <GridItem suffix>
            <div class="operation">
              <el-button type="primary" :icon="SearchIcon" @click="search" class="search-btn">
                搜索
              </el-button>
              <el-button :icon="Delete" @click="reset" class="reset-btn">
                重置
              </el-button>
              <el-button
                v-if="showCollapse"
                type="primary"
                link
                class="search-isOpen"
                @click="collapsed = !collapsed"
              >
                {{ collapsed ? '展开' : '合并' }}
                <el-icon class="el-icon--right">
                  <component :is="collapsed ? ArrowDown : ArrowUp"></component>
                </el-icon>
              </el-button>
            </div>
          </GridItem>
        </Grid>
      </el-form>
    </div>

    <!-- 高级筛选面板 -->
    <AdvancedFilter
      v-if="advancedColumns.length > 0"
      v-model:searchParam="localSearchParam"
      :columns="allColumns"
      :show-tags="showTags"
      :show-history="showHistory"
      :is-expanded="showAdvanced"
      @tag-remove="handleTagRemove"
      @preset-apply="handlePresetApply"
    />

    <!-- 快速筛选标签 -->
    <div class="quick-filters" v-if="quickFilters.length > 0">
      <div class="quick-filter-label">快速筛选:</div>
      <div class="quick-filter-list">
        <el-tag
          v-for="filter in quickFilters"
          :key="filter.name"
          size="small"
          :effect="isQuickFilterActive(filter) ? 'dark' : 'plain'"
          :type="isQuickFilterActive(filter) ? 'primary' : 'info'"
          class="quick-filter-tag"
          @click="applyQuickFilter(filter)"
        >
          {{ filter.name }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="SearchForm">
import { ref, computed, watch } from 'vue'
import { Delete, Search as SearchIcon, ArrowDown, ArrowUp, Filter, Clock } from '@element-plus/icons-vue'
import type { ColumnProps } from '@/components/ProTable/src/types'
import type { BreakPoint } from '@/components/Grid/src/types'

import Grid from '@/components/Grid/src/Grid.vue'
import GridItem from '@/components/Grid/src/components/GridItem.vue'
import SearchFormItem from './components/SearchFormItem.vue'
import AdvancedFilter from './components/AdvancedFilter.vue'

interface QuickFilter {
  name: string
  params: Record<string, any>
}

interface ProTableProps {
  columns?: ColumnProps[]
  allColumns?: ColumnProps[]
  searchParam?: { [key: string]: any }
  searchCol?: number | Record<BreakPoint, number>
  search: (params: any) => void
  reset: (params: any) => void
  showTags?: boolean
  quickFilters?: QuickFilter[]
}

// 默认值
const props = withDefaults(defineProps<ProTableProps>(), {
  columns: () => [],
  allColumns: () => [],
  searchParam: () => ({}),
  showTags: true,
  quickFilters: () => [],
})

const emit = defineEmits<{
  'update:searchParam': [params: { [key: string]: any }]
}>()

// 本地搜索参数
const localSearchParam = computed({
  get: () => props.searchParam,
  set: (val) => emit('update:searchParam', val),
})

// 高级筛选列
const advancedColumns = computed(() => {
  return props.allColumns.filter((col) => col.search?.el && col.search?.advanced)
})

// 获取响应式设置
const getResponsive = (item: ColumnProps) => {
  return {
    span: item.search?.span,
    offset: item.search?.offset ?? 0,
    xs: item.search?.xs,
    sm: item.search?.sm,
    md: item.search?.md,
    lg: item.search?.lg,
    xl: item.search?.xl,
  }
}

// 是否默认折叠搜索项
const collapsed = ref(true)

// 是否显示高级筛选
const showAdvanced = ref(false)

// 是否显示历史记录
const showHistory = ref(false)

// 获取响应式断点
const gridRef = ref()
const breakPoint = computed<BreakPoint>(() => gridRef.value?.breakPoint)

// 判断是否显示 展开/合并 按钮
const showCollapse = computed(() => {
  let show = false
  props.columns.reduce((prev, current) => {
    prev +=
      (current.search![breakPoint.value]?.span ?? current.search?.span ?? 1) +
      (current.search![breakPoint.value]?.offset ?? current.search?.offset ?? 0)
    if (typeof props.searchCol !== 'number') {
      if (prev >= props.searchCol[breakPoint.value]) show = true
    } else {
      if (prev > props.searchCol) show = true
    }
    return prev
  }, 0)
  return show
})

// 搜索
const search = () => {
  props.search(localSearchParam.value)
}

// 重置
const reset = () => {
  // 清除所有搜索参数
  const newParams = { ...localSearchParam.value }
  Object.keys(newParams).forEach((key) => {
    delete newParams[key]
  })
  emit('update:searchParam', newParams)
  props.reset(newParams)
}

// 处理标签移除
const handleTagRemove = (key: string) => {
  const newParams = { ...localSearchParam.value }
  delete newParams[key]
  emit('update:searchParam', newParams)
}

// 处理预设应用
const handlePresetApply = (preset: any) => {
  emit('update:searchParam', { ...preset.params })
  props.search(preset.params)
}

// 当前激活的快速筛选
const activeQuickFilter = ref('')

// 检查快速筛选是否激活
const isQuickFilterActive = (filter: QuickFilter) => {
  return activeQuickFilter.value === filter.name
}

// 应用快速筛选
const applyQuickFilter = (filter: QuickFilter) => {
  if (isQuickFilterActive(filter)) {
    // 如果已激活，则取消
    activeQuickFilter.value = ''
    const newParams = { ...localSearchParam.value }
    Object.keys(filter.params).forEach((key) => {
      delete newParams[key]
    })
    emit('update:searchParam', newParams)
  } else {
    // 激活筛选
    activeQuickFilter.value = filter.name
    emit('update:searchParam', { ...localSearchParam.value, ...filter.params })
  }
  props.search(localSearchParam.value)
}

// 监听搜索参数变化，同步到父组件
watch(
  () => localSearchParam.value,
  (newVal) => {
    emit('update:searchParam', newVal)
  },
  { deep: true }
)

defineExpose({
  collapsed,
  showAdvanced,
  showHistory,
})
</script>

<style scoped lang="scss">
@import './style/index';
@import './style/search-form-enhanced.scss';
</style>
