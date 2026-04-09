# 小红公寓claw - 动画过渡库使用文档

> 🎬 版本: v1.0.0  
> 📅 创建日期: 2026-04-03  
> 🏠 项目: 小红公寓claw

---

## 目录

1. [快速开始](#快速开始)
2. [基础动画](#基础动画)
3. [页面过渡](#页面过渡)
4. [列表动画](#列表动画)
5. [交互效果](#交互效果)
6. [加载状态](#加载状态)
7. [滚动动画](#滚动动画)
8. [Vue 过渡组件](#vue-过渡组件)

---

## 快速开始

### 引入动画库

#### 后台管理系统 (Element Plus)

```scss
// 在 main.ts 或全局样式中引入
import '@/styles/animations.scss';
```

#### 移动端 H5 (Vant)

```less
// 在 main.ts 或全局样式中引入
import '@/styles/animations.less';
```

#### 微信小程序 (uni-app)

```scss
// 在 App.vue 的 style 中引入
@import './styles/animations.scss';
```

---

## 基础动画

### 使用动画类

```html
<!-- 淡入动画 -->
<div class="animate animate-fade-in">内容</div>

<!-- 上滑淡入，慢速 -->
<div class="animate animate-slide-up animate-slow">内容</div>

<!-- 弹跳动画，无限循环 -->
<div class="animate animate-bounce animate-infinite">内容</div>

<!-- 弹性进入，延迟0.3秒 -->
<div class="animate animate-elastic-in animate-delay-3">内容</div>
```

### 动画类型

| 动画类 | 效果 | 说明 |
|--------|------|------|
| `animate-fade-in` | 淡入 | 透明度从0到1 |
| `animate-fade-out` | 淡出 | 透明度从1到0 |
| `animate-slide-up` | 上滑淡入 | 从下方20px滑入 |
| `animate-slide-down` | 下滑淡入 | 从上方20px滑入 |
| `animate-slide-left` | 左滑淡入 | 从右方20px滑入 |
| `animate-slide-right` | 右滑淡入 | 从左方20px滑入 |
| `animate-zoom-in` | 缩放淡入 | 从0.9缩放到1 |
| `animate-zoom-out` | 缩放淡出 | 从1缩放到0.9 |
| `animate-pulse` | 脉冲 | 缩放1.05倍，适合提示 |
| `animate-bounce` | 弹跳 | 上下弹跳效果 |
| `animate-shake` | 摇摆 | 左右摇摆，适合错误提示 |
| `animate-spin` | 旋转 | 360度旋转 |
| `animate-flash` | 闪烁 | 透明度闪烁 |
| `animate-elastic-in` | 弹性进入 | 带弹性效果的进入 |
| `animate-flip-in` | 翻转进入 | Y轴翻转进入 |
| `animate-float` | 浮动 | 上下浮动，适合装饰 |
| `animate-heartbeat` | 心跳 | 心跳效果，适合点赞 |

### 动画修饰类

| 修饰类 | 说明 |
|--------|------|
| `animate-fast` | 150ms 快速 |
| `animate-normal` | 250ms 标准 |
| `animate-slow` | 350ms 慢速 |
| `animate-slower` | 500ms 更慢 |
| `animate-ease` | 默认缓动 |
| `animate-ease-in` | 渐入缓动 |
| `animate-ease-out` | 渐出缓动 |
| `animate-bounce` | 弹跳缓动 |
| `animate-spring` | 弹性缓动 |
| `animate-infinite` | 无限循环 |
| `animate-delay-1` | 延迟0.1s |
| `animate-delay-2` | 延迟0.2s |
| `animate-delay-3` | 延迟0.3s |
| `animate-delay-4` | 延迟0.4s |
| `animate-delay-5` | 延迟0.5s |

---

## 页面过渡

### Vue Router 过渡

#### 后台管理系统

```vue
<template>
  <router-view v-slot="{ Component }">
    <transition name="page-fade" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
/* 使用预定义的页面过渡 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 500ms ease;
}
.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}
</style>
```

#### 移动端 H5

```vue
<template>
  <router-view v-slot="{ Component }">
    <transition :name="transitionName" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const transitionName = ref('page-fade');

watch(() => route.meta.index, (toIndex, fromIndex) => {
  if (toIndex > fromIndex) {
    transitionName.value = 'page-slide-left';
  } else {
    transitionName.value = 'page-slide-right';
  }
});
</script>
```

#### 微信小程序

```vue
<template>
  <view class="page-slide-up">
    <!-- 页面内容 -->
  </view>
</template>
```

### 页面过渡类型

| 过渡名 | 效果 |
|--------|------|
| `page-fade` | 淡入淡出 |
| `page-slide-left` | 从左滑入 |
| `page-slide-right` | 从右滑入 |
| `page-slide-up` | 从上滑入 (uni-app) |
| `page-slide-down` | 从下滑入 (uni-app) |
| `page-zoom` | 缩放过渡 |

---

## 列表动画

### Vue TransitionGroup

```vue
<template>
  <transition-group name="list-item" tag="ul">
    <li v-for="(item, index) in list" :key="item.id" class="stagger-{{ index + 1 }}">
      {{ item.name }}
    </li>
  </transition-group>
</template>

<style>
.list-item-enter-active {
  transition: all 250ms ease;
}
.list-item-leave-active {
  transition: all 250ms ease-in;
  position: absolute;
}
.list-item-move {
  transition: transform 350ms ease;
}
.list-item-enter-from,
.list-item-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
```

### 交错动画

```html
<!-- 列表项依次进入 -->
<ul>
  <li class="animate animate-slide-up stagger-1">项目1</li>
  <li class="animate animate-slide-up stagger-2">项目2</li>
  <li class="animate animate-slide-up stagger-3">项目3</li>
  <li class="animate animate-slide-up stagger-4">项目4</li>
  <li class="animate animate-slide-up stagger-5">项目5</li>
</ul>
```

---

## 交互效果

### 悬浮效果

```html
<!-- 悬浮上浮 -->
<div class="hover-lift">卡片内容</div>

<!-- 悬浮放大 -->
<div class="hover-scale">图片</div>

<!-- 悬浮发光（品牌色） -->
<div class="hover-glow">按钮</div>
```

### 按钮效果

```html
<!-- 点击缩放 -->
<button class="btn-click">点击我</button>

<!-- 链接下划线动画 -->
<a href="#" class="link-underline">链接文字</a>
```

### 图片效果

```html
<!-- 悬停放大 -->
<div class="img-zoom">
  <img src="image.jpg" alt="图片">
</div>
```

### 卡片翻转

```html
<div class="card-flip">
  <div class="card-flip-inner">
    <div class="card-flip-front">正面</div>
    <div class="card-flip-back">背面</div>
  </div>
</div>
```

---

## 加载状态

### 骨架屏

```html
<!-- 骨架屏加载 -->
<div class="skeleton" style="width: 200px; height: 20px;"></div>
```

### 加载圆点

```html
<!-- 后台管理系统 / H5 -->
<div class="loading-dots">
  <span></span>
  <span></span>
  <span></span>
</div>

<!-- 微信小程序 -->
<view class="loading-dots">
  <view class="dot"></view>
  <view class="dot"></view>
  <view class="dot"></view>
</view>
```

### 品牌加载动画

```html
<div class="loading-brand"></div>
```

---

## 滚动动画

### 使用 Intersection Observer

```vue
<template>
  <div ref="element" class="scroll-reveal" :class="{ 'is-visible': isVisible }">
    滚动到此处时显示
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const element = ref(null);
const isVisible = ref(false);

onMounted(() => {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        isVisible.value = true;
        observer.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1 });
  
  if (element.value) {
    observer.observe(element.value);
  }
});
</script>
```

### 滚动动画类型

| 类名 | 效果 |
|------|------|
| `scroll-reveal` | 从下方淡入 |
| `scroll-reveal-left` | 从左方淡入 |
| `scroll-reveal-right` | 从右方淡入 |
| `scroll-reveal-zoom` | 缩放淡入 |

---

## Vue 过渡组件

### 封装过渡组件

```vue
<!-- FadeTransition.vue -->
<template>
  <transition name="fade" mode="out-in">
    <slot />
  </transition>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 250ms ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
```

### 使用过渡组件

```vue
<template>
  <FadeTransition>
    <div v-if="show">内容</div>
  </FadeTransition>
  
  <SlideTransition direction="up">
    <div v-if="show">上滑内容</div>
  </SlideTransition>
</template>
```

---

## 最佳实践

### 1. 性能优化

```css
/* 使用 will-change 提示浏览器优化 */
.animated-element {
  will-change: transform, opacity;
}

/* 动画结束后移除 will-change */
.animated-element {
  will-change: auto;
}
```

### 2. 无障碍支持

```css
/* 尊重用户的减少动画偏好 */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
```

### 3. 动画时长建议

| 场景 | 建议时长 |
|------|----------|
| 微交互（按钮点击） | 150ms |
| 标准过渡 | 250ms |
| 复杂动画 | 350ms |
| 页面切换 | 500ms |

### 4. 避免过度动画

- 不要同时动画太多元素
- 避免大范围的布局变化动画
- 谨慎使用无限循环动画

---

## 示例代码

### 登录页动画

```vue
<template>
  <div class="login-page">
    <!-- Logo 浮动动画 -->
    <div class="logo animate animate-float animate-infinite">
      <img src="@/assets/logo.svg" alt="Logo">
    </div>
    
    <!-- 表单弹性进入 -->
    <div class="form animate animate-elastic-in animate-delay-2">
      <!-- 表单内容 -->
    </div>
    
    <!-- 装饰元素延迟浮动 -->
    <div class="decoration animate animate-float animate-infinite animate-delay-3"></div>
  </div>
</template>
```

### 列表加载动画

```vue
<template>
  <transition-group name="list-item" tag="ul">
    <li 
      v-for="(item, index) in items" 
      :key="item.id"
      class="item hover-lift"
      :style="{ animationDelay: `${index * 0.05}s` }"
    >
      {{ item.name }}
    </li>
  </transition-group>
</template>
```

---

## 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.0 | 2026-04-03 | 初始版本，创建完整动画库 |

---

> 💡 **提示**: 动画库已同步到三个前端项目，保持一致的动画体验。如有新动画需求，请更新此文档并同步到所有项目。
