<template>
  <div class="advanced-filter">
    <!-- 已选筛选标签展示 -->
    <div class="filter-tags" v-if="showTags && selectedTags.length > 0">
      <div class="tags-label">已选条件:</div>
      <div class="tags-list">
        <el-tag
          v-for="tag in selectedTags"
          :key="tag.key"
          closable
          size="small"
          effect="light"
          @close="removeTag(tag)"
        >
          {{ tag.label }}: {{ tag.value }}
        </el-tag>
        <el-button
          type="primary"
          link
          size="small"
          @click="clearAllTags"
          class="clear-all-btn"
        >
          清除全部
        </el-button>
      </div>
    </div>

    <!-- 高级筛选面板 -->
    <el-collapse-transition>
      <div v-show="isExpanded" class="advanced-panel">
        <div class="panel-header">
          <span class="panel-title">
            <el-icon><Filter /></el-icon>
            高级筛选
          </span>
          <el-button type="primary" link @click="saveAsPreset">
            <el-icon><Star /></el-icon>
            保存为预设
          </el-button>
        </div>

        <div class="panel-content">
          <el-form :model="localSearchParam" label-position="top">
            <Grid :cols="{ xs: 1, sm: 2, md: 3, lg: 4, xl: 4 }" :gap="[20, 16]">
              <GridItem
                v-for="item in advancedColumns"
                :key="item.prop"
                :span="item.search?.span || 1"
              >
                <el-form-item :label="item.label">
                  <SearchFormItem :column="item" :searchParam="localSearchParam" />
                </el-form-item>
              </GridItem>
            </Grid>
          </el-form>
        </div>

        <!-- 预设筛选 -->
        <div class="preset-filters" v-if="presets.length > 0">
          <div class="preset-label">筛选预设:</div>
          <div class="preset-list">
            <el-tag
              v-for="preset in presets"
              :key="preset.name"
              size="small"
              :effect="currentPreset === preset.name ? 'dark' : 'plain'"
              :type="currentPreset === preset.name ? 'primary' : 'info'"
              class="preset-tag"
              @click="applyPreset(preset)"
              closable
              @close="removePreset(preset)"
            >
              {{ preset.name }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-collapse-transition>

    <!-- 筛选历史 -->
    <el-collapse-transition>
      <div v-show="showHistory && filterHistory.length > 0" class="filter-history">
        <div class="history-header">
          <span class="history-title">
            <el-icon><Clock /></el-icon>
            筛选历史
          </span>
          <el-button type="info" link size="small" @click="clearHistory">
            清空历史
          </el-button>
        </div>
        <div class="history-list">
          <div
            v-for="(history, index) in filterHistory"
            :key="index"
            class="history-item"
            @click="applyHistory(history)"
          >
            <span class="history-time">{{ formatTime(history.time) }}</span>
            <span class="history-content">{{ formatHistory(history) }}</span>
          </div>
        </div>
      </div>
    </el-collapse-transition>
  </div>
</template>

<script setup lang="ts" name="AdvancedFilter">
import { ref, computed, watch } from 'vue'
import { Filter, Star, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ColumnProps } from '@/components/ProTable/src/types'
import Grid from '@/components/Grid/src/Grid.vue'
import GridItem from '@/components/Grid/src/components/GridItem.vue'
import SearchFormItem from './SearchFormItem.vue'

interface FilterPreset {
  name: string
  params: Record<string, any>
  columns: string[]
}

interface FilterHistory {
  time: number
  params: Record<string, any>
  tags: { key: string; label: string; value: string }[]
}

interface Props {
  columns: ColumnProps[]
  searchParam: Record<string, any>
  showTags?: boolean
  showHistory?: boolean
  isExpanded?: boolean
  maxHistory?: number
}

const props = withDefaults(defineProps<Props>(), {
  showTags: true,
  showHistory: true,
  isExpanded: false,
  maxHistory: 5,
})

const emit = defineEmits<{
  'update:searchParam': [params: Record<string, any>]
  'tag-remove': [key: string]
  'preset-apply': [preset: FilterPreset]
}>()

// 本地搜索参数
const localSearchParam = computed({
  get: () => props.searchParam,
  set: (val) => emit('update:searchParam', val),
})

// 高级筛选列（排除基础搜索列）
const advancedColumns = computed(() => {
  return props.columns.filter((col) => col.search?.el && col.search?.advanced)
})

// 已选筛选标签
const selectedTags = computed(() => {
  const tags: { key: string; label: string; value: string }[] = []
  props.columns.forEach((col) => {
    if (col.search?.el && col.prop) {
      const value = props.searchParam[col.prop]
      if (value !== undefined && value !== '' && value !== null) {
        let displayValue = value
        // 处理枚举类型
        if (col.enum) {
          const enumItem = col.enum.find((e: any) => e.value === value || e.id === value)
          if (enumItem) {
            displayValue = enumItem.label || enumItem.name
          }
        }
        // 处理日期范围
        if (Array.isArray(value)) {
          displayValue = value.join(' ~ ')
        }
        tags.push({
          key: col.prop,
          label: col.label || col.prop,
          value: String(displayValue),
        })
      }
    }
  })
  return tags
})

// 预设筛选列表
const presets = ref<FilterPreset[]>([])
const currentPreset = ref('')

// 筛选历史
const filterHistory = ref<FilterHistory[]>([])

// 从 localStorage 加载数据
const loadFromStorage = () => {
  const savedPresets = localStorage.getItem('xiaohong_filter_presets')
  if (savedPresets) {
    try {
      presets.value = JSON.parse(savedPresets)
    } catch (e) {
      console.error('加载预设失败:', e)
    }
  }
  const savedHistory = localStorage.getItem('xiaohong_filter_history')
  if (savedHistory) {
    try {
      filterHistory.value = JSON.parse(savedHistory)
    } catch (e) {
      console.error('加载历史失败:', e)
    }
  }
}

// 保存到 localStorage
const saveToStorage = () => {
  localStorage.setItem('xiaohong_filter_presets', JSON.stringify(presets.value))
  localStorage.setItem('xiaohong_filter_history', JSON.stringify(filterHistory.value))
}

// 移除标签
const removeTag = (tag: { key: string; label: string; value: string }) => {
  emit('tag-remove', tag.key)
  const newParams = { ...props.searchParam }
  delete newParams[tag.key]
  emit('update:searchParam', newParams)
}

// 清除全部标签
const clearAllTags = () => {
  const newParams = { ...props.searchParam }
  props.columns.forEach((col) => {
    if (col.prop && col.search?.el) {
      delete newParams[col.prop]
    }
  })
  emit('update:searchParam', newParams)
  currentPreset.value = ''
}

// 保存为预设
const saveAsPreset = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入预设名称', '保存预设', {
      confirmButtonText: '保存',
      cancelButtonText: '取消',
      inputValidator: (value) => {
        if (!value) return '请输入预设名称'
        if (presets.value.some((p) => p.name === value)) return '预设名称已存在'
        return true
      },
    })
    
    const activeColumns = props.columns
      .filter((col) => col.prop && props.searchParam[col.prop] !== undefined)
      .map((col) => col.prop as string)
    
    presets.value.push({
      name: value,
      params: { ...props.searchParam },
      columns: activeColumns,
    })
    saveToStorage()
    ElMessage.success('预设保存成功')
  } catch {
    // 用户取消
  }
}

// 应用预设
const applyPreset = (preset: FilterPreset) => {
  currentPreset.value = preset.name
  emit('update:searchParam', { ...preset.params })
  emit('preset-apply', preset)
}

// 移除预设
const removePreset = (preset: FilterPreset) => {
  const index = presets.value.findIndex((p) => p.name === preset.name)
  if (index > -1) {
    presets.value.splice(index, 1)
    if (currentPreset.value === preset.name) {
      currentPreset.value = ''
    }
    saveToStorage()
  }
}

// 添加到历史记录
const addToHistory = () => {
  if (selectedTags.value.length === 0) return
  
  const history: FilterHistory = {
    time: Date.now(),
    params: { ...props.searchParam },
    tags: [...selectedTags.value],
  }
  
  // 去重并限制数量
  filterHistory.value = filterHistory.value.filter(
    (h) => JSON.stringify(h.params) !== JSON.stringify(history.params)
  )
  filterHistory.value.unshift(history)
  if (filterHistory.value.length > props.maxHistory) {
    filterHistory.value = filterHistory.value.slice(0, props.maxHistory)
  }
  saveToStorage()
}

// 应用历史记录
const applyHistory = (history: FilterHistory) => {
  emit('update:searchParam', { ...history.params })
  currentPreset.value = ''
}

// 清空历史
const clearHistory = () => {
  filterHistory.value = []
  saveToStorage()
}

// 格式化时间
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(
    date.getMinutes()
  ).padStart(2, '0')}`
}

// 格式化历史记录
const formatHistory = (history: FilterHistory) => {
  return history.tags.map((t) => `${t.label}:${t.value}`).join(', ')
}

// 监听搜索参数变化，自动添加到历史
watch(
  () => props.searchParam,
  (newVal, oldVal) => {
    if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
      // 延迟添加，避免频繁输入时多次记录
      setTimeout(() => addToHistory(), 2000)
    }
  },
  { deep: true }
)

// 初始化
loadFromStorage()

defineExpose({
  addToHistory,
  clearAllTags,
})
</script>

<style scoped lang="scss">
@import '../style/advanced-filter.scss';
</style>
