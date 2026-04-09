/* AuthState */
export interface SettingsState {
  collapse: boolean
  refresh: boolean
  themeConfig: ThemeConfigProps
  device: DeviceType
  isMobile: boolean
  sidebarOpened: boolean
}

export interface ThemeConfigProps {
  primary: string
  isDark: boolean
}

export type DeviceType = 'desktop' | 'tablet' | 'mobile'
