# 小红公寓claw 导航组件文档

> 🧭 统一的导航组件系统，适用于后台管理系统、移动端H5和微信小程序

---

## 📦 组件概览

| 平台 | 组件 | 路径 |
|------|------|------|
| 后台管理系统 | SideBar, NavBar, TabsBar | `components/Navigation/` |
| 移动端H5 | NavBar, TabBar | `components/Navigation/` |
| 微信小程序 | NavBar, TabBar | `components/Navigation/` |

---

## 🖥️ 后台管理系统

### SideBar 侧边栏

```vue
<template>
  <SideBar />
</template>

<script setup>
import { SideBar } from '@/components/Navigation'
</script>
```

**特性：**
- 深色渐变背景 (#1A1A2E → #252540)
- 珊瑚红品牌Logo区域
- 动态菜单高亮效果
- 折叠/展开动画
- 底部折叠按钮

### NavBar 顶部导航栏

```vue
<template>
  <NavBar />
</template>

<script setup>
import { NavBar } from '@/components/Navigation'
</script>
```

**特性：**
- 面包屑导航
- 全局搜索框
- 全屏切换
- 主题切换（亮色/暗色）
- 通知消息抽屉
- 用户下拉菜单

### TabsBar 标签页

```vue
<template>
  <TabsBar />
</template>

<script setup>
import { TabsBar } from '@/components/Navigation'
</script>
```

**特性：**
- 多标签页管理
- 右键菜单（刷新/关闭当前/关闭其他/关闭全部）
- 标签图标支持
- 动画过渡效果

---

## 📱 移动端H5

### NavBar 导航栏

```vue
<template>
  <NavBar
    title="页面标题"
    :show-back="true"
    :show-right="true"
    right-icon="share"
    @back="onBack"
    @right-click="onRightClick"
  />
</template>

<script setup>
import { NavBar } from '@/components/Navigation'
</script>
```

**Props：**

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| title | string | '' | 标题文字 |
| showBack | boolean | true | 显示返回按钮 |
| showRight | boolean | false | 显示右侧按钮 |
| rightIcon | string | '' | 右侧图标名称 |
| rightText | string | '' | 右侧文字 |
| fixed | boolean | true | 固定定位 |
| transparent | boolean | false | 透明背景 |
| bgColor | string | '#FFFFFF' | 背景颜色 |
| textColor | string | '#1A1A2E' | 文字颜色 |
| zIndex | number | 100 | 层级 |

### TabBar 底部标签栏

```vue
<template>
  <TabBar
    :tabs="tabs"
    :active-index="activeIndex"
    :center-button="centerButton"
    @change="onTabChange"
    @center-click="onCenterClick"
  />
</template>

<script setup>
import { ref } from 'vue'
import { TabBar } from '@/components/Navigation'

const tabs = ref([
  { label: '首页', icon: 'home', activeIcon: 'home-fill', path: '/' },
  { label: '找房', icon: 'search', path: '/search' },
  { label: '消息', icon: 'message', path: '/message', badge: 3 },
  { label: '我的', icon: 'user', path: '/user' }
])

const activeIndex = ref(0)
const centerButton = ref({
  icon: 'plus',
  bgColor: 'linear-gradient(135deg, #FF6B6B 0%, #FF9F43 100%)'
})
</script>
```

**Props：**

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| tabs | TabItem[] | [] | 标签项数组 |
| activeIndex | number | 0 | 当前激活索引 |
| centerButton | CenterButton | - | 中间凸起按钮 |
| bgColor | string | '#FFFFFF' | 背景颜色 |
| activeColor | string | '#FF6B6B' | 激活颜色 |
| inactiveColor | string | '#8A8AA3' | 未激活颜色 |
| safeArea | boolean | true | 安全区域适配 |
| border | boolean | true | 显示顶部边框 |

**TabItem：**

```typescript
interface TabItem {
  label: string        // 标签文字
  icon: string         // 图标名称
  activeIcon?: string  // 激活图标
  path?: string        // 跳转路径
  iconSize?: number    // 图标大小
  badge?: number | boolean // 角标
}
```

---

## 💚 微信小程序

### NavBar 自定义导航栏

```vue
<template>
  <NavBar
    title="页面标题"
    :show-back="true"
    :fixed="true"
    :placeholder="true"
    bg-color="#FFFFFF"
    text-color="#1A1A2E"
  />
</template>

<script setup>
import { NavBar } from '@/components/Navigation'
</script>
```

**使用说明：**
1. 在 `pages.json` 中配置 `"navigationStyle": "custom"`
2. 组件会自动计算状态栏和导航栏高度
3. 支持透明导航栏效果

### TabBar 自定义标签栏

```vue
<template>
  <TabBar
    :tabs="tabs"
    :active-index="activeIndex"
    @change="onTabChange"
  />
</template>

<script setup>
import { ref } from 'vue'
import { TabBar } from '@/components/Navigation'

const tabs = ref([
  { label: '首页', icon: 'icon-home', activeIcon: 'icon-home-fill', pagePath: 'pages/index/index' },
  { label: '我的', icon: 'icon-user', activeIcon: 'icon-user-fill', pagePath: 'pages/user/user', badge: true }
])

const activeIndex = ref(0)
</script>
```

**使用说明：**
1. 在 `pages.json` 中配置 `"tabBar": { "custom": true }`
2. 在 `App.vue` 中引入组件
3. 组件会自动适配安全区域

---

## 🎨 主题定制

### 品牌色彩

```css
/* 主色调 */
--color-primary: #FF6B6B;
--color-primary-light: #FF9F43;

/* 文字色 */
--color-text-primary: #1A1A2E;
--color-text-secondary: #4A4A68;
--color-text-tertiary: #8A8AA3;

/* 背景色 */
--color-bg-primary: #FFFFFF;
--color-bg-secondary: #F5F5FA;
```

### 自定义样式

```scss
// 覆盖默认样式
.sidebar-container {
  --sidebar-bg: #1A1A2E;
  --sidebar-active-color: #FF6B6B;
}

.navbar-h5 {
  --navbar-height: 46px;
  --navbar-bg: #FFFFFF;
}
```

---

## 📱 响应式适配

### 断点定义

| 断点 | 宽度 | 设备 |
|------|------|------|
| xs | < 576px | 手机 |
| sm | ≥ 576px | 大屏手机 |
| md | ≥ 768px | 平板 |
| lg | ≥ 992px | 小型桌面 |
| xl | ≥ 1200px | 标准桌面 |

### 适配示例

```vue
<template>
  <!-- 移动端隐藏侧边栏 -->
  <SideBar v-if="!isMobile" />
  
  <!-- 移动端显示底部TabBar -->
  <TabBar v-if="isMobile" />
</template>

<script setup>
import { computed } from 'vue'
import { useWindowSize } from '@vueuse/core'

const { width } = useWindowSize()
const isMobile = computed(() => width.value < 768)
</script>
```

---

## 🔧 常见问题

### Q: 导航栏被内容遮挡？

A: 确保设置了正确的 `z-index`，对于固定定位的导航栏，建议 `z-index: 100`

### Q: 小程序自定义导航栏高度计算错误？

A: 检查 `pages.json` 中是否正确配置了 `"navigationStyle": "custom"`

### Q: TabBar 角标不显示？

A: 确保 `badge` 属性为数字或 `true`（显示红点）

### Q: 如何修改主题色？

A: 通过 CSS 变量覆盖默认颜色，或修改组件中的品牌色配置

---

## 📚 相关文档

- [设计系统](./design-system.md)
- [主题配置](./theme-variables.css)
- [动画库](./animations.md)

---

> 💡 **提示**: 所有导航组件都遵循小红公寓claw设计系统规范，确保品牌一致性。
