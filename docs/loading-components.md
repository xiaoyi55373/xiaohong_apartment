# 小红公寓claw - 加载状态组件使用文档

> 为三个前端项目（后台管理系统、移动端H5、微信小程序）提供统一的 Skeleton 骨架屏和 Loading 加载组件

---

## 📦 组件清单

### Skeleton 骨架屏组件
用于在内容加载时展示占位符，减少用户等待焦虑。

### Loading 加载组件
提供多种加载动画效果，适用于不同场景。

---

## 🖥️ 后台管理系统 (Vue3 + Element Plus)

### Skeleton 组件

#### 基础用法

```vue
<template>
  <!-- 文本骨架 -->
  <Skeleton variant="text" :rows="3" />
  
  <!-- 圆形骨架 -->
  <Skeleton variant="circle" :size="40" />
  
  <!-- 矩形骨架 -->
  <Skeleton variant="rect" width="100%" height="200px" />
  
  <!-- 卡片骨架 -->
  <Skeleton variant="card" :showImage="true" imageHeight="200px" />
  
  <!-- 列表骨架 -->
  <Skeleton variant="list" :listItems="4" :showAvatar="true" />
  
  <!-- 表格骨架 -->
  <Skeleton variant="table" :columns="4" :tableRows="5" />
  
  <!-- 仪表盘骨架 -->
  <Skeleton variant="dashboard" chartHeight="300px" />
</template>

<script setup>
import { Skeleton } from '@/components/Skeleton'
</script>
```

#### Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| variant | 骨架屏变体类型 | `'text' \| 'circle' \| 'rect' \| 'card' \| 'list' \| 'table' \| 'dashboard'` | `'text'` |
| animated | 是否显示动画效果 | `boolean` | `true` |
| rows | 文本行数 | `number` | `3` |
| lineHeight | 行高 | `number` | `16` |
| lastLineWidth | 最后一行宽度 | `string` | `'60%'` |
| size | 圆形尺寸 | `number` | `40` |
| width | 矩形宽度 | `string` | `'100%'` |
| height | 矩形高度 | `string` | `'100px'` |
| showImage | 是否显示图片 | `boolean` | `true` |
| imageHeight | 图片高度 | `string` | `'200px'` |
| listItems | 列表项数量 | `number` | `4` |
| showAvatar | 是否显示头像 | `boolean` | `true` |
| avatarSize | 头像尺寸 | `number` | `40` |
| columns | 表格列数 | `number` | `4` |
| tableRows | 表格行数 | `number` | `5` |
| chartHeight | 图表高度 | `string` | `'300px'` |

### Loading 组件

#### 基础用法

```vue
<template>
  <!-- 品牌加载 -->
  <Loading type="brand" text="小红公寓" />
  
  <!-- 圆点加载 -->
  <Loading type="dots" />
  
  <!-- 旋转加载 -->
  <Loading type="spinner" size="large" />
  
  <!-- 脉冲加载 -->
  <Loading type="pulse" />
  
  <!-- 进度条加载 -->
  <Loading type="bar" :progress="75" />
  
  <!-- 全屏加载 -->
  <Loading type="brand" fullscreen text="加载中..." />
  
  <!-- 带遮罩的加载 -->
  <Loading type="brand" overlay text="提交中..." />
</template>

<script setup>
import { Loading } from '@/components/Loading'
</script>
```

#### Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| visible | 是否显示加载 | `boolean` | `true` |
| type | 加载类型 | `'brand' \| 'dots' \| 'spinner' \| 'pulse' \| 'bar' \| 'skeleton'` | `'brand'` |
| size | 尺寸 | `'small' \| 'default' \| 'large'` | `'default'` |
| text | 加载文本 | `string` | `''` |
| fullscreen | 是否全屏显示 | `boolean` | `false` |
| overlay | 是否显示遮罩层 | `boolean` | `true` |
| overlayColor | 遮罩层颜色 | `string` | `'rgba(255, 255, 255, 0.9)'` |
| progress | 进度值 (0-100) | `number` | `0` |
| showProgress | 是否显示进度文本 | `boolean` | `true` |

---

## 📱 移动端 H5 (Vue3 + Vant)

### Skeleton 组件

#### 基础用法

```vue
<template>
  <!-- 文本骨架 -->
  <Skeleton variant="text" :rows="3" />
  
  <!-- 公寓卡片骨架 -->
  <Skeleton variant="card" imageHeight="200px" />
  
  <!-- 详情页骨架 -->
  <Skeleton variant="detail" galleryHeight="250px" />
  
  <!-- 个人中心骨架 -->
  <Skeleton variant="profile" :avatarSize="80" :menuItems="5" />
</template>

<script setup>
import { Skeleton } from '@/components/Skeleton'
</script>
```

#### 变体说明

- `text`: 文本段落骨架
- `circle`: 圆形头像骨架
- `rect`: 矩形骨架
- `card`: 公寓卡片骨架（带图片、标题、价格、标签）
- `list`: 列表骨架
- `detail`: 详情页骨架（轮播图、标题、标签、信息区块）
- `profile`: 个人中心骨架（头像、统计、菜单）

### Loading 组件

#### 基础用法

```vue
<template>
  <!-- 品牌加载 -->
  <Loading type="brand" text="小红公寓" subtext="让租房更简单" />
  
  <!-- 圆点加载 -->
  <Loading type="dots" />
  
  <!-- 旋转加载 -->
  <Loading type="spinner" />
  
  <!-- 脉冲加载 -->
  <Loading type="pulse" />
  
  <!-- 圆形进度加载 -->
  <Loading type="circular" :progress="75" />
  
  <!-- 全屏加载 -->
  <Loading type="brand" fullscreen />
</template>

<script setup>
import { Loading } from '@/components/Loading'
</script>
```

---

## 💚 微信小程序 (uni-app)

### Skeleton 组件

#### 基础用法

```vue
<template>
  <!-- 文本骨架 -->
  <Skeleton variant="text" :rows="3" />
  
  <!-- 公寓卡片骨架 -->
  <Skeleton variant="card" imageHeight="400rpx" />
  
  <!-- 详情页骨架 -->
  <Skeleton variant="detail" galleryHeight="500rpx" />
  
  <!-- 个人中心骨架 -->
  <Skeleton variant="profile" :avatarSize="160" :menuItems="5" />
</template>

<script setup>
import Skeleton from '@/components/Skeleton/Skeleton.vue'
</script>
```

**注意**: 小程序使用 `rpx` 单位，尺寸值需要乘以 2（如 40px = 80rpx）

### Loading 组件

#### 基础用法

```vue
<template>
  <!-- 品牌加载 -->
  <Loading type="brand" text="小红公寓" subtext="让租房更简单" />
  
  <!-- 圆点加载 -->
  <Loading type="dots" />
  
  <!-- 旋转加载 -->
  <Loading type="spinner" />
  
  <!-- 脉冲加载 -->
  <Loading type="pulse" />
  
  <!-- 全屏加载 -->
  <Loading type="brand" fullscreen />
</template>

<script setup>
import Loading from '@/components/Loading/Loading.vue'
</script>
```

---

## 🎨 设计规范

### 品牌色
- 主色: `#FF6B6B` (珊瑚红)
- 辅色: `#FF9F43` (橙色)
- 渐变: `linear-gradient(135deg, #FF6B6B 0%, #FF9F43 100%)`

### 骨架屏颜色
- 基础色: `#f0f2f5`
- 高亮色: `#e4e7ed`

### 动画
- 加载动画: 1.5s ease-in-out infinite
- 呼吸动画: 2s ease-in-out infinite
- 旋转动画: 1s linear infinite

---

## 💡 使用建议

1. **骨架屏使用场景**
   - 页面首次加载
   - 列表数据加载
   - 图片加载占位

2. **Loading 使用场景**
   - 表单提交
   - 数据刷新
   - 长时间操作

3. **最佳实践**
   - 骨架屏和 Loading 不要同时使用
   - 根据场景选择合适的变体类型
   - 保持动画流畅，避免过度使用

---

## 📝 更新日志

### v1.0.0 (2026-04-03)
- 初始版本发布
- 支持 Skeleton 7 种变体
- 支持 Loading 5 种动画类型
- 适配三个前端项目

---

*文档由 Kimi 自动生成*
