# 小红公寓claw - 消息通知组件文档

> 🔔 Toast、Message、Dialog 组件使用指南

---

## 组件概述

消息通知组件用于向用户展示操作反馈、状态提示和交互确认。包含三个核心组件：

| 组件 | 用途 | 适用场景 |
|------|------|----------|
| Toast | 轻提示 | 操作成功/失败反馈、加载状态 |
| Message | 消息提示 | 页面顶部通知、重要信息提醒 |
| Dialog | 对话框 | 确认操作、重要信息展示 |

---

## 1. Toast 轻提示

### 1.1 功能特性

- ✅ 支持 5 种类型：success、error、warning、info、loading
- ✅ 支持 3 种位置：top、center、bottom
- ✅ 支持标题+内容双行展示
- ✅ 自动关闭，支持手动关闭
- ✅ 支持多个 Toast 堆叠显示

### 1.2 使用方法

#### 后台管理系统 / H5

```typescript
import { Toast } from '@/components'

// 成功提示
Toast.success('操作成功')

// 错误提示
Toast.error('操作失败，请重试')

// 警告提示
Toast.warning('请注意检查信息')

// 信息提示
Toast.info('这是一条消息')

// 加载提示
Toast.loading('加载中...')

// 自定义配置
Toast.show({
  type: 'success',
  title: '提交成功',
  message: '您的申请已提交审核',
  duration: 3000,
  position: 'top'
})

// 关闭指定 Toast
const id = Toast.loading('加载中...')
Toast.close(id)

// 关闭所有 Toast
Toast.clear()
```

#### 微信小程序

```typescript
import { Toast } from '@/components'

// 使用 uni.showToast 封装
Toast.success('操作成功')
Toast.error('操作失败')
Toast.loading('加载中...')

// 关闭
Toast.close()
```

### 1.3 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| type | ToastType | 'info' | 类型：success/error/warning/info/loading |
| title | string | - | 标题（可选） |
| message | string | 必填 | 消息内容 |
| duration | number | 2000 | 显示时长(ms)，0表示不自动关闭 |
| position | ToastPosition | 'center' | 位置：top/center/bottom |

---

## 2. Message 消息提示

### 2.1 功能特性

- ✅ 页面顶部固定位置显示
- ✅ 支持 4 种类型：success、error、warning、info
- ✅ 不同类型对应不同背景色
- ✅ 自动关闭，可手动关闭
- ✅ 支持多个 Message 堆叠

### 2.2 使用方法

#### 后台管理系统 / H5

```typescript
import { Message } from '@/components'

// 快捷方法
Message.success('保存成功')
Message.error('保存失败')
Message.warning('请注意')
Message.info('新消息提醒')

// 自定义配置
Message.show({
  type: 'success',
  message: '数据已更新',
  duration: 3000,
  showClose: true
})

// 关闭所有
Message.closeAll()
```

#### 微信小程序

```typescript
import { Message } from '@/components'

Message.success('保存成功')
Message.error('保存失败')
```

### 2.3 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| type | MessageType | 'info' | 类型：success/error/warning/info |
| message | string | 必填 | 消息内容 |
| duration | number | 2000 | 显示时长(ms) |
| showClose | boolean | false | 是否显示关闭按钮 |

---

## 3. Dialog 对话框

### 3.1 功能特性

- ✅ 支持 5 种类型：default、success、error、warning、confirm
- ✅ 支持自定义标题、内容
- ✅ 支持确认/取消按钮自定义
- ✅ 支持类型图标自动显示
- ✅ 支持 Promise 异步操作
- ✅ 支持点击遮罩关闭

### 3.2 使用方法

#### 后台管理系统 / H5

```typescript
import { Dialog } from '@/components'

// 确认对话框
await Dialog.confirm('确定要删除该房源吗？', '删除确认')

// 成功提示
Dialog.success('房源发布成功！')

// 错误提示
Dialog.error('发布失败，请重试')

// 警告提示
await Dialog.warning('此操作不可撤销，是否继续？')

// 普通提示
Dialog.alert('请完善房源信息后再发布')

// 自定义对话框
await Dialog.show({
  type: 'confirm',
  title: '确认预约',
  content: '您确定要预约看房吗？',
  confirmText: '立即预约',
  cancelText: '再想想',
  confirmButtonType: 'primary',
  onConfirm: async () => {
    await submitBooking()
  }
})
```

#### 微信小程序

```typescript
import { Dialog } from '@/components'

// 确认对话框
await Dialog.confirm('确定要删除吗？', '提示')

// 成功提示
Dialog.success('操作成功')

// 错误提示
Dialog.error('操作失败')
```

### 3.3 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| type | DialogType | 'default' | 类型：success/error/warning/confirm/default |
| title | string | '提示' | 标题 |
| content | string | - | 内容文本 |
| showClose | boolean | false | 是否显示关闭按钮 |
| closeOnClickMask | boolean | true | 点击遮罩是否关闭 |
| showCancel | boolean | true | 是否显示取消按钮 |
| showConfirm | boolean | true | 是否显示确认按钮 |
| cancelText | string | '取消' | 取消按钮文字 |
| confirmText | string | '确定' | 确认按钮文字 |
| confirmButtonType | string | 'primary' | 确认按钮类型：primary/success/warning/danger |
| confirmLoading | boolean | false | 确认按钮加载状态 |
| showTypeIcon | boolean | true | 是否显示类型图标 |

---

## 4. 组件样式

### 4.1 色彩规范

| 类型 | 色值 | 用途 |
|------|------|------|
| 成功 | #1DD1A1 | 操作成功、通过 |
| 错误 | #FF4757 | 操作失败、错误 |
| 警告 | #FF9F43 | 警告、提醒 |
| 信息 | #54A0FF | 提示信息 |
| 主色 | #FF6B6B | 品牌色、加载中 |

### 4.2 动画效果

- **Toast**: 淡入 + 缩放，从上方滑入
- **Message**: 从顶部滑入
- **Dialog**: 遮罩淡入 + 内容缩放弹出

---

## 5. 最佳实践

### 5.1 使用建议

```typescript
// ✅ 好的实践
Toast.success('保存成功')
Toast.error('网络连接失败，请检查网络')

// ❌ 避免使用
Toast.info('成功')  // 过于简单，使用 success 更合适
alert('操作成功')   // 避免使用原生 alert
```

### 5.2 异步操作

```typescript
// ✅ 使用 loading 提示异步操作
const loadingId = Toast.loading('提交中...')
try {
  await submitData()
  Toast.close(loadingId)
  Toast.success('提交成功')
} catch (error) {
  Toast.close(loadingId)
  Toast.error('提交失败：' + error.message)
}
```

### 5.3 确认操作

```typescript
// ✅ 危险操作使用确认对话框
try {
  await Dialog.confirm('删除后不可恢复，确定删除吗？', '删除确认')
  await deleteItem(id)
  Toast.success('删除成功')
} catch {
  // 用户取消，无需处理
}
```

---

## 6. 平台差异

| 特性 | 后台管理系统 | H5 | 微信小程序 |
|------|-------------|-----|-----------|
| Toast 位置 | 6种位置 | 3种位置 | 居中 |
| Message 位置 | 顶部居中 | 顶部全宽 | 顶部居中 |
| Dialog 动画 | 完整 | 完整 | 简化 |
| 多实例堆叠 | 支持 | 支持 | 不支持 |
| 底层实现 | Vue组件 | Vue组件 | uni API |

---

## 7. 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.0 | 2026-04-03 | 初始版本，创建 Toast/Message/Dialog 组件 |

---

> 💡 **提示**: 所有组件均采用珊瑚红品牌色系，确保视觉一致性。
