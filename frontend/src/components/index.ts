import type { App, Component } from 'vue'

import { SvgIcon } from './SvgIcon'
import { SearchForm } from './SearchForm'
import { ProTable } from './ProTable'
import { SwitchDark } from './SwitchDark'
import { IconifyIcon } from './IconifyIcon'
import { Skeleton } from './Skeleton'
import { Loading } from './Loading'
import { Empty } from './Empty'
import { Toast } from './Toast'
import { Message } from './Message'
import { Dialog } from './Dialog'
import XhButton from './Button/index.vue'
import XhForm from './Form/index.vue'
import XhFormItem from './Form/FormItem.vue'
import XhInput from './Form/Input.vue'
import XhCard from './Card/index.vue'
import XhList from './List/index.vue'

const Components: {
  [propName: string]: Component
} = {
  SvgIcon,
  SearchForm,
  ProTable,
  SwitchDark,
  IconifyIcon,
  Skeleton,
  Loading,
  Empty,
  Toast,
  Message,
  Dialog,
  XhButton,
  XhForm,
  XhFormItem,
  XhInput,
  XhCard,
  XhList,
}

export default {
  install: (app: App) => {
    Object.keys(Components).forEach((key) => {
      app.component(key, Components[key])
    })
    // 挂载全局方法
    app.config.globalProperties.$toast = Toast
    app.config.globalProperties.$message = Message
    app.config.globalProperties.$dialog = Dialog
  },
}

// 导出组件和方法供按需导入
export { Toast, Message, Dialog, XhButton, XhForm, XhFormItem, XhInput, XhCard, XhList }
