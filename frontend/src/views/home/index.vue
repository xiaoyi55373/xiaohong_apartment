<template>
  <div class="dashboard-container">
    <!-- 顶部欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-left">
          <el-avatar :size="64" :src="userInfo.avatarUrl || logoPng" class="user-avatar" />
          <div class="welcome-text">
            <h1 class="welcome-title">{{ timeFix() }}{{ userInfo?.name }}，{{ welcome() }}</h1>
            <p class="welcome-subtitle">{{ getEnvByName('VITE_APP_TITLE') }} · 数据可视化中心</p>
          </div>
        </div>
        <div class="welcome-right">
          <div class="date-info">
            <div class="date-day">{{ currentDay }}</div>
            <div class="date-detail">{{ currentDate }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" v-for="(item, index) in statsData" :key="index">
          <div class="stat-card" :class="`stat-card-${index}`" v-loading="loading">
            <div class="stat-icon-wrapper" :style="{ background: item.bgColor }">
              <el-icon :size="28" :color="item.iconColor">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-trend" :class="item.trend > 0 ? 'up' : 'down'">
                <el-icon><ArrowUp v-if="item.trend > 0" /><ArrowDown v-else /></el-icon>
                <span>{{ Math.abs(item.trend) }}%</span>
                <span class="trend-text">较上月</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 趋势图 -->
        <el-col :xs="24" :lg="16">
          <ChartCard
            title="业务趋势分析"
            icon="TrendCharts"
            v-model="trendTimeRange"
            :time-ranges="timeRanges"
            body-height="320px"
          >
            <LineChart
              :x-axis="trendData.xAxis"
              :series="trendData.series"
              :loading="loading"
              height="320px"
              @click="handleChartClick"
            />
          </ChartCard>
        </el-col>

        <!-- 分布图 -->
        <el-col :xs="24" :lg="8">
          <ChartCard
            title="房源分布"
            icon="PieChart"
            body-height="320px"
          >
            <XHPieChart
              :data="pieData"
              :loading="loading"
              :donut="true"
              height="320px"
              @click="handleChartClick"
            />
          </ChartCard>
        </el-col>
      </el-row>
    </div>

    <!-- 底部区域 -->
    <div class="bottom-section">
      <el-row :gutter="20">
        <!-- 快捷操作 -->
        <el-col :xs="24" :md="12">
          <el-card class="action-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Operation /></el-icon>
                <span>快捷操作</span>
              </div>
            </template>
            <div class="quick-actions">
              <div 
                v-for="(action, index) in quickActions" 
                :key="index" 
                class="action-item"
                @click="handleQuickAction(action)"
              >
                <div class="action-icon" :style="{ background: action.bgColor }">
                  <el-icon :size="20" :color="action.iconColor">
                    <component :is="action.icon" />
                  </el-icon>
                </div>
                <div class="action-info">
                  <div class="action-name">{{ action.name }}</div>
                  <div class="action-desc">{{ action.desc }}</div>
                </div>
                <el-icon class="action-arrow"><ArrowRight /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 最近动态 -->
        <el-col :xs="24" :md="12">
          <el-card class="activity-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Bell /></el-icon>
                <span>最近动态</span>
                <el-tag type="primary" size="small" class="activity-count">{{ activities.length }} 条新动态</el-tag>
              </div>
            </template>
            <div class="activity-list">
              <div 
                v-for="(activity, index) in activities" 
                :key="index" 
                class="activity-item"
              >
                <div class="activity-dot" :style="{ background: activity.color }"></div>
                <div class="activity-content">
                  <div class="activity-title">{{ activity.title }}</div>
                  <div class="activity-desc">{{ activity.desc }}</div>
                  <div class="activity-time">{{ activity.time }}</div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart as ELineChart, BarChart, PieChart as EPieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  ToolboxComponent,
} from 'echarts/components'
import { useUserStore } from '@/store/modules/user'
import { timeFix, welcome } from '@/utils/index'
import { getEnvByName } from '@/utils/getEnv'
import logoPng from '@/assets/images/logo.png'
import { useRouter } from 'vue-router'
import {
  OfficeBuilding,
  House,
  Calendar,
  DocumentChecked,
  ArrowUp,
  ArrowDown,
  TrendCharts,
  PieChart,
  Operation,
  ArrowRight,
  Bell,
  Plus,
  User,
} from '@element-plus/icons-vue'
import {
  getApartmentList,
  getRoomList,
} from '@/api/apartmentManagement'
import {
  getAppointmentInfoList,
  getAgreementInfoList,
} from '@/api/rentManagement'

// 导入图表组件
import { LineChart, PieChart as XHPieChart, ChartCard, registerTheme } from '@/components/Charts'
import * as echarts from 'echarts/core'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  ELineChart,
  BarChart,
  EPieChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  ToolboxComponent,
])

// 注册小红公寓主题
registerTheme(echarts)

const router = useRouter()
const userStore = useUserStore()
const userInfo = userStore.userInfo
const loading = ref(false)
const trendTimeRange = ref('month')

// 时间范围选项
const timeRanges = [
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '全年', value: 'year' },
]

// 当前日期
const currentDay = computed(() => {
  return new Date().getDate().toString().padStart(2, '0')
})

const currentDate = computed(() => {
  const date = new Date()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${date.getFullYear()}年${date.getMonth() + 1}月 · ${weekDays[date.getDay()]}`
})

// 统计数据
const statsData = ref([
  {
    icon: 'OfficeBuilding',
    label: '公寓总数',
    value: 0,
    trend: 12,
    bgColor: 'linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%)',
    iconColor: '#FFFFFF',
  },
  {
    icon: 'House',
    label: '房间总数',
    value: 0,
    trend: 8,
    bgColor: 'linear-gradient(135deg, #54A0FF 0%, #74B9FF 100%)',
    iconColor: '#FFFFFF',
  },
  {
    icon: 'Calendar',
    label: '预约看房',
    value: 0,
    trend: -5,
    bgColor: 'linear-gradient(135deg, #FF9F43 0%, #FFB366 100%)',
    iconColor: '#FFFFFF',
  },
  {
    icon: 'DocumentChecked',
    label: '签约租约',
    value: 0,
    trend: 15,
    bgColor: 'linear-gradient(135deg, #1DD1A1 0%, #48DBAA 100%)',
    iconColor: '#FFFFFF',
  },
])

// 快捷操作
const quickActions = [
  {
    name: '新增公寓',
    desc: '快速录入新公寓信息',
    icon: 'Plus',
    bgColor: '#FFF0F0',
    iconColor: '#FF6B6B',
    route: '/apartmentManagement/apartmentManagement',
  },
  {
    name: '预约管理',
    desc: '查看和处理看房预约',
    icon: 'Calendar',
    bgColor: '#FFF5EB',
    iconColor: '#FF9F43',
    route: '/rentManagement/appointment',
  },
  {
    name: '租约管理',
    desc: '管理租赁合同',
    icon: 'DocumentChecked',
    bgColor: '#E6FAF5',
    iconColor: '#1DD1A1',
    route: '/rentManagement/agreement',
  },
  {
    name: '用户管理',
    desc: '查看注册用户数据',
    icon: 'User',
    bgColor: '#E8F4FF',
    iconColor: '#54A0FF',
    route: '/userManagement',
  },
]

// 最近动态
const activities = ref([
  {
    title: '新预约看房',
    desc: '张先生预约了阳光花园公寓看房',
    time: '10分钟前',
    color: '#FF9F43',
  },
  {
    title: '租约签约成功',
    desc: '李女士完成了世纪新城房间的签约',
    time: '30分钟前',
    color: '#1DD1A1',
  },
  {
    title: '新公寓上架',
    desc: '管理员发布了滨江花园新公寓',
    time: '1小时前',
    color: '#FF6B6B',
  },
  {
    title: '房间状态更新',
    desc: '5个房间状态更新为已租出',
    time: '2小时前',
    color: '#54A0FF',
  },
  {
    title: '用户注册',
    desc: '新增3位注册用户',
    time: '3小时前',
    color: '#A29BFE',
  },
])

// 趋势图表数据
const trendData = computed(() => {
  const dataMap = {
    week: {
      xAxis: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      series: [
        { 
          name: '预约数', 
          data: [12, 15, 8, 20, 18, 25, 22],
          color: '#FF9F43',
          area: true,
        },
        { 
          name: '签约数', 
          data: [5, 8, 6, 12, 10, 15, 13],
          color: '#1DD1A1',
          area: true,
        },
      ],
    },
    month: {
      xAxis: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'],
      series: [
        { 
          name: '预约数', 
          data: [45, 52, 38, 65, 58, 72, 68],
          color: '#FF9F43',
          area: true,
        },
        { 
          name: '签约数', 
          data: [18, 25, 20, 35, 30, 42, 38],
          color: '#1DD1A1',
          area: true,
        },
      ],
    },
    year: {
      xAxis: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      series: [
        { 
          name: '预约数', 
          data: [120, 145, 180, 165, 200, 235, 210, 195, 220, 245, 230, 260],
          color: '#FF9F43',
          area: true,
        },
        { 
          name: '签约数', 
          data: [60, 75, 90, 85, 105, 125, 110, 100, 115, 130, 120, 140],
          color: '#1DD1A1',
          area: true,
        },
      ],
    },
  }

  return dataMap[trendTimeRange.value as keyof typeof dataMap]
})

// 饼图数据
const pieData = [
  { value: 35, name: '一居室', color: '#FF6B6B' },
  { value: 28, name: '两居室', color: '#54A0FF' },
  { value: 20, name: '三居室', color: '#1DD1A1' },
  { value: 12, name: '四居室', color: '#FF9F43' },
  { value: 5, name: '其他', color: '#A29BFE' },
]

// 获取统计数据
const fetchStatsData = async () => {
  loading.value = true
  try {
    // 获取公寓数量
    const apartmentRes = await getApartmentList({ pageNum: 1, pageSize: 1 })
    if (apartmentRes && apartmentRes.data) {
      statsData.value[0].value = apartmentRes.data.total || 0
    }

    // 获取房间数量
    const roomRes = await getRoomList({ pageNum: 1, pageSize: 1 })
    if (roomRes && roomRes.data) {
      statsData.value[1].value = roomRes.data.total || 0
    }

    // 获取预约数量
    const appointmentRes = await getAppointmentInfoList({ pageNum: 1, pageSize: 1 })
    if (appointmentRes && appointmentRes.data) {
      statsData.value[2].value = appointmentRes.data.total || 0
    }

    // 获取租约数量
    const agreementRes = await getAgreementInfoList({ pageNum: 1, pageSize: 1 })
    if (agreementRes && agreementRes.data) {
      statsData.value[3].value = agreementRes.data.total || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 使用模拟数据
    statsData.value[0].value = 128
    statsData.value[1].value = 456
    statsData.value[2].value = 89
    statsData.value[3].value = 234
  } finally {
    loading.value = false
  }
}

// 处理快捷操作
const handleQuickAction = (action: typeof quickActions[0]) => {
  router.push(action.route)
}

// 处理图表点击
const handleChartClick = (params: any) => {
  console.log('图表点击:', params)
  // 可以在这里添加跳转逻辑
}

onMounted(() => {
  fetchStatsData()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 24px;
  background: #F5F5FA;
  min-height: 100vh;
}

// 欢迎区域
.welcome-section {
  margin-bottom: 24px;
  
  .welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: linear-gradient(135deg, #FF6B6B 0%, #FF9F43 100%);
    border-radius: 16px;
    padding: 32px;
    color: white;
    box-shadow: 0 8px 32px rgba(255, 107, 107, 0.3);
  }

  .welcome-left {
    display: flex;
    align-items: center;
    gap: 20px;

    .user-avatar {
      border: 4px solid rgba(255, 255, 255, 0.3);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .welcome-text {
      .welcome-title {
        font-size: 24px;
        font-weight: 600;
        margin: 0 0 8px 0;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      .welcome-subtitle {
        font-size: 14px;
        opacity: 0.9;
        margin: 0;
      }
    }
  }

  .welcome-right {
    .date-info {
      text-align: right;

      .date-day {
        font-size: 48px;
        font-weight: 700;
        line-height: 1;
        margin-bottom: 4px;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      .date-detail {
        font-size: 14px;
        opacity: 0.9;
      }
    }
  }
}

// 统计卡片
.stats-section {
  margin-bottom: 24px;

  .stat-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 16px;
    box-shadow: 0 4px 12px rgba(26, 26, 46, 0.08);
    transition: all 0.3s ease;
    border: 1px solid #E8E8EF;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(26, 26, 46, 0.12);
    }

    .stat-icon-wrapper {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .stat-content {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: #1A1A2E;
        line-height: 1.2;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        color: #8A8AA3;
        margin-bottom: 8px;
      }

      .stat-trend {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        font-weight: 500;

        &.up {
          color: #1DD1A1;
        }

        &.down {
          color: #FF6B6B;
        }

        .trend-text {
          color: #B8B8CC;
          font-weight: 400;
          margin-left: 4px;
        }
      }
    }
  }
}

// 图表区域
.charts-section {
  margin-bottom: 24px;
}

// 底部区域
.bottom-section {
  .action-card,
  .activity-card {
    border-radius: 16px;
    border: 1px solid #E8E8EF;
    box-shadow: 0 4px 12px rgba(26, 26, 46, 0.08);
    height: 100%;

    :deep(.el-card__header) {
      padding: 20px 24px;
      border-bottom: 1px solid #F0F0F5;
    }

    :deep(.el-card__body) {
      padding: 16px 24px;
    }

    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #1A1A2E;

      .el-icon {
        color: #FF6B6B;
        font-size: 20px;
      }

      .activity-count {
        margin-left: auto;
        background: #FFF0F0;
        border-color: #FF6B6B;
        color: #FF6B6B;
      }
    }
  }

  // 快捷操作
  .quick-actions {
    .action-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.2s ease;
      margin-bottom: 8px;

      &:last-child {
        margin-bottom: 0;
      }

      &:hover {
        background: #F8F8FC;

        .action-arrow {
          transform: translateX(4px);
          color: #FF6B6B;
        }
      }

      .action-icon {
        width: 44px;
        height: 44px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
      }

      .action-info {
        flex: 1;

        .action-name {
          font-size: 15px;
          font-weight: 600;
          color: #1A1A2E;
          margin-bottom: 4px;
        }

        .action-desc {
          font-size: 13px;
          color: #8A8AA3;
        }
      }

      .action-arrow {
        color: #B8B8CC;
        transition: all 0.2s ease;
      }
    }
  }

  // 最近动态
  .activity-list {
    .activity-item {
      display: flex;
      gap: 16px;
      padding: 16px 0;
      border-bottom: 1px solid #F0F0F5;

      &:last-child {
        border-bottom: none;
      }

      .activity-dot {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        margin-top: 6px;
        flex-shrink: 0;
        box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.5);
      }

      .activity-content {
        flex: 1;

        .activity-title {
          font-size: 14px;
          font-weight: 600;
          color: #1A1A2E;
          margin-bottom: 4px;
        }

        .activity-desc {
          font-size: 13px;
          color: #4A4A68;
          margin-bottom: 6px;
        }

        .activity-time {
          font-size: 12px;
          color: #8A8AA3;
        }
      }
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .welcome-section {
    .welcome-content {
      flex-direction: column;
      text-align: center;
      padding: 24px;

      .welcome-left {
        flex-direction: column;
        margin-bottom: 20px;
      }

      .welcome-right {
        .date-info {
          text-align: center;

          .date-day {
            font-size: 36px;
          }
        }
      }
    }
  }

  .stats-section {
    .el-col {
      margin-bottom: 16px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }

  .charts-section {
    .el-col {
      margin-bottom: 16px;

      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}
</style>
