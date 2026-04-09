<template>
  <div class="pie-chart-wrapper" :class="{ 'chart-loading': loading }">
    <div v-if="loading" class="chart-skeleton">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="rect" style="width: 100%; height: 100%" />
        </template>
      </el-skeleton>
    </div>
    <v-chart
      v-else
      class="pie-chart"
      :option="chartOption"
      :theme="theme"
      autoresize
      @click="handleClick"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { chartOptionGenerators } from './echarts-theme'

interface DataItem {
  name: string
  value: number
  color?: string
}

interface Props {
  data: DataItem[]
  title?: string
  loading?: boolean
  theme?: string
  donut?: boolean
  rose?: boolean
  showLegend?: boolean
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  theme: 'xiaohong',
  donut: true,
  rose: false,
  showLegend: true,
  height: '320px',
})

const emit = defineEmits<{
  click: [params: any]
}>()

const chartOption = computed(() => {
  const baseOption = chartOptionGenerators.pieChart({
    data: props.data,
    title: props.title,
    donut: props.donut,
    rose: props.rose,
  })
  
  // 自定义配置
  if (!props.showLegend) {
    baseOption.legend = { show: false }
  }
  
  return baseOption
})

const handleClick = (params: any) => {
  emit('click', params)
}
</script>

<style scoped lang="scss">
.pie-chart-wrapper {
  position: relative;
  width: 100%;
  height: v-bind(height);
  
  &.chart-loading {
    .chart-skeleton {
      width: 100%;
      height: 100%;
      padding: 20px;
    }
  }
  
  .pie-chart {
    width: 100%;
    height: 100%;
  }
}
</style>
