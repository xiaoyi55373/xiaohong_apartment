/**
 * @Description: 响应式布局 Hooks - 小红公寓claw
 * @Author: 小红
 * @Date: 2026-04-03
 */

import { ref, computed, onMounted, onUnmounted } from 'vue'

// 断点定义
export const breakpoints = {
  xs: 576,
  sm: 768,
  md: 992,
  lg: 1200,
  xl: 1600,
}

export type Breakpoint = 'xs' | 'sm' | 'md' | 'lg' | 'xl' | 'xxl'

/**
 * 响应式布局 Hook
 */
export function useResponsive() {
  // 当前视口宽度
  const screenWidth = ref(window.innerWidth)
  
  // 当前断点
  const breakpoint = computed<Breakpoint>(() => {
    const width = screenWidth.value
    if (width < breakpoints.xs) return 'xs'
    if (width < breakpoints.sm) return 'sm'
    if (width < breakpoints.md) return 'md'
    if (width < breakpoints.lg) return 'lg'
    if (width < breakpoints.xl) return 'xl'
    return 'xxl'
  })

  // 是否是移动端
  const isMobile = computed(() => screenWidth.value < breakpoints.sm)
  
  // 是否是平板
  const isTablet = computed(() => screenWidth.value >= breakpoints.sm && screenWidth.value < breakpoints.md)
  
  // 是否是桌面
  const isDesktop = computed(() => screenWidth.value >= breakpoints.md)
  
  // 是否小于指定宽度
  const isLessThan = (width: number) => computed(() => screenWidth.value < width)
  
  // 是否大于指定宽度
  const isGreaterThan = (width: number) => computed(() => screenWidth.value >= width)

  // 监听窗口大小变化
  let resizeListener: (() => void) | null = null

  onMounted(() => {
    resizeListener = () => {
      screenWidth.value = window.innerWidth
    }
    window.addEventListener('resize', resizeListener)
  })

  onUnmounted(() => {
    if (resizeListener) {
      window.removeEventListener('resize', resizeListener)
    }
  })

  return {
    screenWidth,
    breakpoint,
    isMobile,
    isTablet,
    isDesktop,
    isLessThan,
    isGreaterThan,
  }
}

/**
 * 侧边栏响应式 Hook
 */
export function useSidebarResponsive() {
  const { isMobile, isTablet } = useResponsive()
  
  // 是否自动折叠
  const shouldCollapse = computed(() => {
    return isMobile.value || isTablet.value
  })

  // 侧边栏宽度
  const sidebarWidth = computed(() => {
    if (isMobile.value) return 0
    if (isTablet.value) return 64
    return 256
  })

  return {
    isMobile,
    isTablet,
    shouldCollapse,
    sidebarWidth,
  }
}

/**
 * 表格响应式 Hook
 */
export function useTableResponsive() {
  const { isMobile, isTablet } = useResponsive()
  
  // 表格高度
  const tableHeight = computed(() => {
    if (isMobile.value) return 'calc(100vh - 280px)'
    if (isTablet.value) return 'calc(100vh - 250px)'
    return 'calc(100vh - 320px)'
  })

  // 分页大小
  const pageSize = computed(() => {
    if (isMobile.value) return 10
    if (isTablet.value) return 15
    return 20
  })

  // 是否显示分页器
  const showPagination = computed(() => true)

  return {
    tableHeight,
    pageSize,
    showPagination,
    isMobile,
    isTablet,
  }
}

/**
 * 表单响应式 Hook
 */
export function useFormResponsive() {
  const { isMobile, isTablet, isDesktop } = useResponsive()
  
  // 表单项布局
  const formItemLayout = computed(() => {
    if (isMobile.value) {
      return {
        labelPosition: 'top' as const,
        labelWidth: 'auto',
      }
    }
    return {
      labelPosition: 'right' as const,
      labelWidth: '100px',
    }
  })

  // 表单列数
  const formCols = computed(() => {
    if (isMobile.value) return 1
    if (isTablet.value) return 1
    if (isDesktop.value) return 2
    return 3
  })

  return {
    formItemLayout,
    formCols,
    isMobile,
    isTablet,
    isDesktop,
  }
}

export default useResponsive
