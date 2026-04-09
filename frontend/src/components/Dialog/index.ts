/**
 * 小红公寓claw - Dialog 对话框组件
 * @description 模态对话框，支持确认/取消、自定义内容
 * @author 小红
 */
import type { App } from 'vue'
import DialogComponent from './index.vue'
import { createVNode, render } from 'vue'
import type { DialogType, DialogSize } from './index.vue'

// 导出类型
export type { DialogType, DialogSize }

export interface DialogOptions {
  /** 对话框类型 */
  type?: DialogType
  /** 标题 */
  title?: string
  /** 内容文本 */
  content?: string
  /** 尺寸 */
  size?: DialogSize
  /** 是否显示关闭按钮 */
  showClose?: boolean
  /** 点击遮罩是否关闭 */
  closeOnClickMask?: boolean
  /** 是否显示取消按钮 */
  showCancel?: boolean
  /** 是否显示确认按钮 */
  showConfirm?: boolean
  /** 取消按钮文字 */
  cancelText?: string
  /** 确认按钮文字 */
  confirmText?: string
  /** 确认按钮类型 */
  confirmButtonType?: 'primary' | 'success' | 'warning' | 'danger'
  /** 内容居中 */
  centerContent?: boolean
  /** 底部居中 */
  centerFooter?: boolean
  /** 是否显示类型图标 */
  showTypeIcon?: boolean
  /** 自定义宽度 */
  width?: string | number
  /** 确认回调 */
  onConfirm?: () => void | Promise<void>
  /** 取消回调 */
  onCancel?: () => void
  /** 关闭回调 */
  onClose?: () => void
}

// 创建 Dialog
const createDialog = (options: DialogOptions) => {
  const container = document.createElement('div')
  document.body.appendChild(container)

  return new Promise<void>((resolve, reject) => {
    const props = {
      visible: true,
      ...options,
      'onUpdate:visible': (val: boolean) => {
        if (!val) {
          props.visible = false
        }
      },
      onConfirm: () => {
        options.onConfirm?.()
        resolve()
        close()
      },
      onCancel: () => {
        options.onCancel?.()
        reject(new Error('cancel'))
        close()
      },
      onClose: () => {
        options.onClose?.()
        reject(new Error('close'))
        close()
      },
      onClosed: () => {
        render(null, container)
        document.body.removeChild(container)
      }
    }

    const close = () => {
      props.visible = false
    }

    const vnode = createVNode(DialogComponent, props)
    render(vnode, container)
  })
}

// Dialog 方法
export const Dialog = {
  /**
   * 显示 Dialog
   * @param options - 配置选项
   */
  show(options: DialogOptions) {
    return createDialog(options)
  },

  /**
   * 确认对话框
   * @param content - 内容文本
   * @param title - 标题
   * @param options - 其他选项
   */
  confirm(content: string, title: string = '确认', options?: Omit<DialogOptions, 'type' | 'content' | 'title'>) {
    return createDialog({
      type: 'confirm',
      title,
      content,
      showTypeIcon: true,
      ...options
    })
  },

  /**
   * 成功提示框
   * @param content - 内容文本
   * @param title - 标题
   * @param options - 其他选项
   */
  success(content: string, title: string = '成功', options?: Omit<DialogOptions, 'type' | 'content' | 'title'>) {
    return createDialog({
      type: 'success',
      title,
      content,
      showCancel: false,
      confirmText: '知道了',
      confirmButtonType: 'success',
      ...options
    })
  },

  /**
   * 错误提示框
   * @param content - 内容文本
   * @param title - 标题
   * @param options - 其他选项
   */
  error(content: string, title: string = '错误', options?: Omit<DialogOptions, 'type' | 'content' | 'title'>) {
    return createDialog({
      type: 'error',
      title,
      content,
      showCancel: false,
      confirmText: '知道了',
      confirmButtonType: 'danger',
      ...options
    })
  },

  /**
   * 警告提示框
   * @param content - 内容文本
   * @param title - 标题
   * @param options - 其他选项
   */
  warning(content: string, title: string = '警告', options?: Omit<DialogOptions, 'type' | 'content' | 'title'>) {
    return createDialog({
      type: 'warning',
      title,
      content,
      confirmButtonType: 'warning',
      ...options
    })
  },

  /**
   * 提示框
   * @param content - 内容文本
   * @param title - 标题
   * @param options - 其他选项
   */
  alert(content: string, title: string = '提示', options?: Omit<DialogOptions, 'type' | 'content' | 'title'>) {
    return createDialog({
      title,
      content,
      showCancel: false,
      ...options
    })
  }
}

// 组件安装
DialogComponent.install = (app: App) => {
  app.component('Dialog', DialogComponent)
  // 挂载到全局属性
  app.config.globalProperties.$dialog = Dialog
}

export default DialogComponent
