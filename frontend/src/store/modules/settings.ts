/*
 * @Author: 小红
 * @Date: 2026-04-03
 * @Description: 系统设置 (支持响应式)
 */

import { defineStore } from 'pinia'
import { DEFAULT_PRIMARY } from '@/config/config'
import type { SettingsState, ThemeConfigProps, DeviceType } from './model/settingsModel'

// 断点定义
const breakpoints = {
  mobile: 768,
  tablet: 992,
}

export const useSettingsStore = defineStore({
  id: 'app-settings',
  state: (): SettingsState => ({
    collapse: false,
    refresh: false,
    themeConfig: {
      primary: DEFAULT_PRIMARY,
      isDark: false,
    },
    device: 'desktop',
    isMobile: false,
    sidebarOpened: true,
  }),

  actions: {
    changeCollapse() {
      this.collapse = !this.collapse
    },
    setCollapse(collapse: boolean) {
      this.collapse = collapse
    },
    setRefresh() {
      this.refresh = !this.refresh
    },
    setThemeConfig(themeConfig: ThemeConfigProps) {
      this.themeConfig = themeConfig
    },
    // 设置设备类型
    setDevice(device: DeviceType) {
      this.device = device
      this.isMobile = device === 'mobile'
      
      // 根据设备自动调整侧边栏
      if (device === 'mobile') {
        this.sidebarOpened = false
        this.collapse = false
      } else if (device === 'tablet') {
        this.collapse = true
        this.sidebarOpened = true
      } else {
        this.sidebarOpened = true
      }
    },
    // 切换侧边栏（移动端专用）
    toggleSidebar() {
      this.sidebarOpened = !this.sidebarOpened
    },
    // 关闭侧边栏
    closeSidebar() {
      this.sidebarOpened = false
    },
    // 根据窗口宽度自动检测设备
    autoDetectDevice(width: number) {
      let device: DeviceType = 'desktop'
      if (width < breakpoints.mobile) {
        device = 'mobile'
      } else if (width < breakpoints.tablet) {
        device = 'tablet'
      }
      
      if (this.device !== device) {
        this.setDevice(device)
      }
    },
  },
  persist: true,
})

export default useSettingsStore
