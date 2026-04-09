/**
 * 小红公寓claw - Message 消息提示组件
 * @description 页面顶部居中消息提示，自动消失
 * @author 小红
 */
import type { App } from 'vue'
import MessageComponent from './index.vue'
import { createVNode, render } from 'vue'
import type { MessageType, MessageSize } from './index.vue'

// 导出类型
export type { MessageType, MessageSize }

export interface MessageOptions {
  /** 消息类型 */
  type?: MessageType
  /** 消息内容 */
  message: string
  /** 显示时长(ms)，0表示不自动关闭 */
  duration?: number
  /** 是否显示关闭按钮 */
  showClose?: boolean
  /** 尺寸 */
  size?: MessageSize
  /** 自定义样式 */
  customStyle?: Record<string, string>
  /** 关闭回调 */
  onClose?: () => void
}

// 消息实例列表
let messageInstances: any[] = []
let seed = 1

// 创建 Message
const createMessage = (options: MessageOptions) => {
  const id = `message_${seed++}`
  const container = document.createElement('div')
  container.id = id
  document.body.appendChild(container)

  // 计算垂直偏移
  const verticalOffset = 20 + messageInstances.length * 60

  const props = {
    ...options,
    customStyle: {
      top: `${verticalOffset}px`,
      ...options.customStyle
    },
    onDestroy: () => {
      // 移除实例
      const index = messageInstances.findIndex(item => item.id === id)
      if (index > -1) {
        messageInstances.splice(index, 1)
      }
      // 更新其他消息位置
      messageInstances.forEach((item, idx) => {
        item.vm.component.props.customStyle = {
          ...item.vm.component.props.customStyle,
          top: `${20 + idx * 60}px`
        }
      })
      // 清理 DOM
      render(null, container)
      document.body.removeChild(container)
    }
  }

  const vnode = createVNode(MessageComponent, props)
  render(vnode, container)

  const instance = {
    id,
    vm: vnode,
    close: () => {
      vnode.component?.exposed?.close()
    }
  }

  messageInstances.push(instance)
  return instance
}

// Message 方法
export const Message = {
  /**
   * 显示 Message
   * @param options - 配置选项或消息文本
   */
  show(options: MessageOptions | string) {
    if (typeof options === 'string') {
      options = { message: options }
    }
    return createMessage(options)
  },

  /**
   * 成功消息
   * @param message - 消息内容
   * @param options - 其他选项
   */
  success(message: string, options?: Omit<MessageOptions, 'type' | 'message'>) {
    return createMessage({ type: 'success', message, ...options })
  },

  /**
   * 错误消息
   * @param message - 消息内容
   * @param options - 其他选项
   */
  error(message: string, options?: Omit<MessageOptions, 'type' | 'message'>) {
    return createMessage({ type: 'error', message, ...options })
  },

  /**
   * 警告消息
   * @param message - 消息内容
   * @param options - 其他选项
   */
  warning(message: string, options?: Omit<MessageOptions, 'type' | 'message'>) {
    return createMessage({ type: 'warning', message, ...options })
  },

  /**
   * 信息消息
   * @param message - 消息内容
   * @param options - 其他选项
   */
  info(message: string, options?: Omit<MessageOptions, 'type' | 'message'>) {
    return createMessage({ type: 'info', message, ...options })
  },

  /**
   * 关闭所有 Message
   */
  closeAll() {
    messageInstances.forEach(instance => {
      instance.close()
    })
    messageInstances = []
  }
}

// 组件安装
MessageComponent.install = (app: App) => {
  app.component('Message', MessageComponent)
  // 挂载到全局属性
  app.config.globalProperties.$message = Message
}

export default MessageComponent
