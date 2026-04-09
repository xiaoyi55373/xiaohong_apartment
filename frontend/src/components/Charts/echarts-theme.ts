/**
 * ECharts 主题配置文件 - 小红公寓claw
 * 采用珊瑚红品牌色系
 * @author 小红
 */

// 品牌色彩系统
export const brandColors = {
  // 主色调 - 珊瑚红
  primary: '#FF6B6B',
  primaryLight: '#FF8E8E',
  primaryDark: '#E55A5A',
  
  // 辅助色
  secondary: '#FF9F43',
  secondaryLight: '#FFB366',
  
  // 功能色
  success: '#1DD1A1',
  successLight: '#48DBAA',
  warning: '#FF9F43',
  warningLight: '#FFB366',
  danger: '#FF6B6B',
  dangerLight: '#FF8E8E',
  info: '#54A0FF',
  infoLight: '#74B9FF',
  
  // 中性色
  textPrimary: '#1A1A2E',
  textSecondary: '#4A4A68',
  textMuted: '#8A8AA3',
  border: '#E8E8EF',
  background: '#F5F5FA',
  white: '#FFFFFF',
}

// 图表配色方案
export const chartColorPalette = [
  '#FF6B6B', // 珊瑚红
  '#54A0FF', // 天蓝
  '#1DD1A1', // 翠绿
  '#FF9F43', // 橙黄
  '#A29BFE', // 淡紫
  '#FD79A8', // 粉红
  '#00B894', // 青绿
  '#E17055', // 橙红
  '#74B9FF', // 浅蓝
  '#FDCB6E', // 金黄
]

// ECharts 主题配置
export const xiaohongEchartsTheme = {
  // 颜色调色板
  color: chartColorPalette,
  
  // 背景色
  backgroundColor: 'transparent',
  
  // 文本样式
  textStyle: {
    fontFamily: '"PingFang SC", "Microsoft YaHei", sans-serif',
    color: brandColors.textPrimary,
  },
  
  // 标题样式
  title: {
    textStyle: {
      color: brandColors.textPrimary,
      fontWeight: 'bold',
      fontSize: 16,
    },
    subtextStyle: {
      color: brandColors.textSecondary,
      fontSize: 12,
    },
  },
  
  // 线图样式
  line: {
    smooth: true,
    symbol: 'circle',
    symbolSize: 8,
    lineStyle: {
      width: 3,
    },
    itemStyle: {
      borderWidth: 2,
      borderColor: '#fff',
    },
    emphasis: {
      scale: 1.5,
      itemStyle: {
        shadowBlur: 10,
        shadowColor: 'rgba(0, 0, 0, 0.2)',
      },
    },
  },
  
  // 柱状图样式
  bar: {
    barWidth: '60%',
    itemStyle: {
      borderRadius: [4, 4, 0, 0],
    },
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowColor: 'rgba(0, 0, 0, 0.2)',
      },
    },
  },
  
  // 饼图样式
  pie: {
    itemStyle: {
      borderRadius: 8,
      borderColor: '#fff',
      borderWidth: 2,
    },
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.2)',
      },
      label: {
        show: true,
        fontSize: 16,
        fontWeight: 'bold',
      },
    },
  },
  
  // 散点图样式
  scatter: {
    symbolSize: 12,
    itemStyle: {
      borderWidth: 2,
      borderColor: '#fff',
    },
    emphasis: {
      scale: 1.5,
      itemStyle: {
        shadowBlur: 10,
        shadowColor: 'rgba(0, 0, 0, 0.2)',
      },
    },
  },
  
  // 雷达图样式
  radar: {
    axisLine: {
      lineStyle: {
        color: brandColors.border,
      },
    },
    splitLine: {
      lineStyle: {
        color: brandColors.border,
      },
    },
    splitArea: {
      areaStyle: {
        color: ['rgba(255, 255, 255, 0.8)', 'rgba(245, 245, 250, 0.8)'],
      },
    },
  },
  
  // 坐标轴样式
  categoryAxis: {
    axisLine: {
      lineStyle: {
        color: brandColors.border,
      },
    },
    axisTick: {
      show: false,
    },
    axisLabel: {
      color: brandColors.textMuted,
      fontSize: 12,
    },
    splitLine: {
      show: false,
    },
  },
  
  valueAxis: {
    axisLine: {
      show: false,
    },
    axisTick: {
      show: false,
    },
    axisLabel: {
      color: brandColors.textMuted,
      fontSize: 12,
    },
    splitLine: {
      lineStyle: {
        color: '#F0F0F5',
        type: 'dashed',
      },
    },
  },
  
  // 图例样式
  legend: {
    textStyle: {
      color: brandColors.textSecondary,
      fontSize: 12,
    },
    pageTextStyle: {
      color: brandColors.textSecondary,
    },
  },
  
  // 提示框样式
  tooltip: {
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: brandColors.border,
    borderWidth: 1,
    padding: [12, 16],
    textStyle: {
      color: brandColors.textPrimary,
      fontSize: 13,
    },
    extraCssText: 'box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); border-radius: 8px;',
    axisPointer: {
      lineStyle: {
        color: brandColors.primary,
        width: 2,
      },
      crossStyle: {
        color: brandColors.textMuted,
      },
    },
  },
  
  // 工具箱样式
  toolbox: {
    iconStyle: {
      borderColor: brandColors.textMuted,
    },
    emphasis: {
      iconStyle: {
        borderColor: brandColors.primary,
      },
    },
  },
  
  // 数据区域缩放
  dataZoom: {
    backgroundColor: 'rgba(255, 255, 255, 0.8)',
    dataBackgroundColor: brandColors.border,
    fillerColor: 'rgba(255, 107, 107, 0.1)',
    handleStyle: {
      color: brandColors.primary,
    },
    textStyle: {
      color: brandColors.textSecondary,
    },
  },
  
  // 视觉映射
  visualMap: {
    textStyle: {
      color: brandColors.textSecondary,
    },
  },
  
  // 时间轴
  timeline: {
    lineStyle: {
      color: brandColors.border,
    },
    itemStyle: {
      color: brandColors.primary,
    },
    label: {
      color: brandColors.textSecondary,
    },
    controlStyle: {
      color: brandColors.primary,
      borderColor: brandColors.primary,
    },
  },
  
  // 区域缩放选择器
  brush: {
    toolbox: ['rect', 'polygon', 'clear'],
    brushStyle: {
      borderWidth: 1,
      color: 'rgba(255, 107, 107, 0.1)',
      borderColor: brandColors.primary,
    },
  },
}

// 图表类型默认配置生成器
export const chartOptionGenerators = {
  // 折线图配置
  lineChart: (data: {
    xAxis: string[]
    series: Array<{
      name: string
      data: number[]
      color?: string
      area?: boolean
    }>
    title?: string
  }) => {
    return {
      title: data.title ? { text: data.title, left: 'center' } : undefined,
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: brandColors.border,
        borderWidth: 1,
        textStyle: { color: brandColors.textPrimary },
      },
      legend: {
        data: data.series.map(s => s.name),
        bottom: 0,
        textStyle: { color: brandColors.textSecondary },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        top: '10%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: data.xAxis,
        axisLine: { lineStyle: { color: brandColors.border } },
        axisLabel: { color: brandColors.textMuted },
        axisTick: { show: false },
      },
      yAxis: {
        type: 'value',
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: brandColors.textMuted },
        splitLine: { lineStyle: { color: '#F0F0F5', type: 'dashed' } },
      },
      series: data.series.map((s, index) => ({
        name: s.name,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: s.data,
        itemStyle: { 
          color: s.color || chartColorPalette[index % chartColorPalette.length],
        },
        lineStyle: { width: 3 },
        areaStyle: s.area ? {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { 
                offset: 0, 
                color: s.color 
                  ? `${s.color}4D` // 30% opacity
                  : `${chartColorPalette[index % chartColorPalette.length]}4D`,
              },
              { 
                offset: 1, 
                color: s.color 
                  ? `${s.color}0D` // 5% opacity
                  : `${chartColorPalette[index % chartColorPalette.length]}0D`,
              },
            ],
          },
        } : undefined,
      })),
    }
  },
  
  // 柱状图配置
  barChart: (data: {
    xAxis: string[]
    series: Array<{
      name: string
      data: number[]
      color?: string
    }>
    title?: string
    horizontal?: boolean
  }) => {
    return {
      title: data.title ? { text: data.title, left: 'center' } : undefined,
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: brandColors.border,
        borderWidth: 1,
        textStyle: { color: brandColors.textPrimary },
      },
      legend: {
        data: data.series.map(s => s.name),
        bottom: 0,
        textStyle: { color: brandColors.textSecondary },
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        top: '10%',
        containLabel: true,
      },
      xAxis: data.horizontal ? {
        type: 'value',
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: brandColors.textMuted },
        splitLine: { lineStyle: { color: '#F0F0F5', type: 'dashed' } },
      } : {
        type: 'category',
        data: data.xAxis,
        axisLine: { lineStyle: { color: brandColors.border } },
        axisLabel: { color: brandColors.textMuted },
        axisTick: { show: false },
      },
      yAxis: data.horizontal ? {
        type: 'category',
        data: data.xAxis,
        axisLine: { lineStyle: { color: brandColors.border } },
        axisLabel: { color: brandColors.textMuted },
        axisTick: { show: false },
      } : {
        type: 'value',
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: brandColors.textMuted },
        splitLine: { lineStyle: { color: '#F0F0F5', type: 'dashed' } },
      },
      series: data.series.map((s, index) => ({
        name: s.name,
        type: 'bar',
        data: s.data,
        itemStyle: {
          color: s.color || chartColorPalette[index % chartColorPalette.length],
          borderRadius: data.horizontal ? [0, 4, 4, 0] : [4, 4, 0, 0],
        },
        barWidth: '60%',
      })),
    }
  },
  
  // 饼图配置
  pieChart: (data: {
    data: Array<{
      name: string
      value: number
      color?: string
    }>
    title?: string
    donut?: boolean
    rose?: boolean
  }) => {
    return {
      title: data.title ? { text: data.title, left: 'center' } : undefined,
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: brandColors.border,
        borderWidth: 1,
        textStyle: { color: brandColors.textPrimary },
        formatter: '{b}: {c} ({d}%)',
      },
      legend: {
        orient: 'horizontal',
        bottom: 0,
        textStyle: { color: brandColors.textSecondary },
      },
      series: [
        {
          name: data.title || '数据分布',
          type: 'pie',
          radius: data.donut ? ['40%', '70%'] : '70%',
          center: ['50%', '45%'],
          roseType: data.rose ? 'area' : false,
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff',
            borderWidth: 2,
          },
          label: {
            show: false,
            position: 'center',
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 18,
              fontWeight: 'bold',
              color: brandColors.textPrimary,
            },
          },
          labelLine: { show: false },
          data: data.data.map((d, index) => ({
            ...d,
            itemStyle: {
              color: d.color || chartColorPalette[index % chartColorPalette.length],
            },
          })),
        },
      ],
    }
  },
  
  // 雷达图配置
  radarChart: (data: {
    indicators: Array<{
      name: string
      max: number
    }>
    series: Array<{
      name: string
      value: number[]
      color?: string
    }>
    title?: string
  }) => {
    return {
      title: data.title ? { text: data.title, left: 'center' } : undefined,
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: brandColors.border,
        borderWidth: 1,
        textStyle: { color: brandColors.textPrimary },
      },
      legend: {
        data: data.series.map(s => s.name),
        bottom: 0,
        textStyle: { color: brandColors.textSecondary },
      },
      radar: {
        indicator: data.indicators,
        center: ['50%', '50%'],
        radius: '65%',
        axisName: {
          color: brandColors.textSecondary,
        },
        splitArea: {
          areaStyle: {
            color: ['rgba(255, 107, 107, 0.05)', 'rgba(255, 107, 107, 0.1)'],
          },
        },
        axisLine: {
          lineStyle: {
            color: brandColors.border,
          },
        },
        splitLine: {
          lineStyle: {
            color: brandColors.border,
          },
        },
      },
      series: [
        {
          name: data.title || '雷达图',
          type: 'radar',
          data: data.series.map((s, index) => ({
            ...s,
            itemStyle: {
              color: s.color || chartColorPalette[index % chartColorPalette.length],
            },
            areaStyle: {
              opacity: 0.3,
            },
          })),
        },
      ],
    }
  },
}

// 注册主题到 ECharts
export const registerTheme = (echarts: any) => {
  echarts.registerTheme('xiaohong', xiaohongEchartsTheme)
}

export default {
  brandColors,
  chartColorPalette,
  xiaohongEchartsTheme,
  chartOptionGenerators,
  registerTheme,
}
