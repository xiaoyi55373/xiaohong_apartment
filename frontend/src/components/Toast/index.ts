/**
 * 小红公寓claw - Toast 轻提示组件
 * @description 轻量级消息提示，支持多种类型和位置
 * @author 小红
 */
import type { App } from 'vue'
import ToastComponent from './index.vue'
import { createVNode, render, ref } from 'vue'
import type { ToastType, ToastPosition, ToastSize, ToastOptions } from './index.vue'

// 导出类型
export type { ToastType, ToastPosition, ToastSize, ToastOptions }

// 单例实例
let toastInstance: any = null
let toastContainer: HTMLElement | null = null

// 获取或创建 Toast 实例
const getToastInstance = () => {
  if (toastInstance) return toastInstance

  // 创建容器
  toastContainer = document.createElement('div')
  document.body.appendChild(toastContainer)

  // 创建 vnode
  const vnode = createVNode(ToastComponent)
  render(vnode, toastContainer)

  toastInstance = vnode.component?.exposed
  return toastInstance
}

// Toast 方法
export const Toast = {
  /**
   * 显示 Toast
   * @param options - 配置选项
   */
  show(options: ToastOptions) {
    const instance = getToastInstance()
    return instance?.add(options)
  },

  /**
   * 成功提示
   * @param message - 消息内容
   * @param options - 其他选项
   */
  success(message: string, options?: Omit<ToastOptions, 'type' | 'message'>) {
    const instance = getToastInstance()
    return instance?.success(message, options)
  },

  /**
   * 错误提示
   * @param message - 消息内容
   * @param options - 其他选项
   */
  error(message: string, options?: Omit<ToastOptions, 'type' | 'message'>) {
    const instance = getToastInstance()
    return instance?.error(message, options)
  },

  /**
   * 警告提示
   * @param message - 消息内容
   * @param options - 其他选项
   */
  warning(message: string, options?: Omit<ToastOptions, 'type' | 'message'>) {
    const instance = getToastInstance()
    return instance?.warning(message, options)
  },

  /**
   * 信息提示
   * @param message - 消息内容
   * @param options - 其他选项
   */
  info(message: string, options?: Omit<ToastOptions, 'type' | 'message'>) {
    const instance = getToastInstance()
    return instance?.info(message, options)
  },

  /**
   * 加载提示
   * @param message - 消息内容
   * @param options - 其他选项
   */
  loading(message: string, options?: Omit<ToastOptions, 'type' | 'message'>) {
    const instance = getToastInstance()
    return instance?.loading(message, options)
  },

  /**
   * 关闭指定 Toast
   * @param id - Toast ID
   */
  close(id: string) {
    const instance = getToastInstance()
    instance?.remove(id)
  },

  /**
   * 关闭所有 Toast
   */
  clear() {
    const instance = getToastInstance()
    instance?.clear()
  }
}

// 组件安装
ToastComponent.install = (app: App) => {
  app.component('Toast', ToastComponent)
  // 挂载到全局属性
  app.config.globalProperties.$toast = Toast
}

export default ToastComponent
