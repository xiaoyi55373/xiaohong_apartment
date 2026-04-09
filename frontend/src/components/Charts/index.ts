/**
 * 图表组件库 - 小红公寓claw
 * @author 小红
 */

// 导出主题配置
export {
  brandColors,
  chartColorPalette,
  xiaohongEchartsTheme,
  chartOptionGenerators,
  registerTheme,
} from './echarts-theme'

// 导出组件
export { default as LineChart } from './LineChart.vue'
export { default as BarChart } from './BarChart.vue'
export { default as PieChart } from './PieChart.vue'
export { default as RadarChart } from './RadarChart.vue'
export { default as ChartCard } from './ChartCard.vue'
