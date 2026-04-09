<template>
  <div class="line-chart-wrapper" :class="{ 'chart-loading': loading }">
    <div v-if="loading" class="chart-skeleton">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="rect" style="width: 100%; height: 100%" />
        </template>
      </el-skeleton>
    </div>
    <v-chart
      v-else
      class="line-chart"
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
import { chartOptionGenerators, brandColors } from './echarts-theme'

interface SeriesItem {
  name: string
  data: number[]
  color?: string
  area?: boolean
  smooth?: boolean
}

interface Props {
  xAxis: string[]
  series: SeriesItem[]
  title?: string
  loading?: boolean
  theme?: string
  showArea?: boolean
  showLegend?: boolean
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  theme: 'xiaohong',
  showArea: true,
  showLegend: true,
  height: '320px',
})

const emit = defineEmits<{
  click: [params: any]
}>()

const chartOption = computed(() => {
  const baseOption = chartOptionGenerators.lineChart({
    xAxis: props.xAxis,
    series: props.series.map(s => ({
      ...s,
      area: props.showArea && (s.area !== false),
    })),
    title: props.title,
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
.line-chart-wrapper {
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
  
  .line-chart {
    width: 100%;
    height: 100%;
  }
}
</style>
