# 小红公寓claw 图标系统 (Icon System)

> 🎨 版本: v1.0.0
> 📅 创建日期: 2026-04-03
> 🏠 项目: 小红公寓claw

---

## 1. 概述

本文档定义了小红公寓claw项目的图标系统，包括Logo变体和通用图标库。

### 1.1 图标规范

- **风格**: 线性图标 (Outline)
- **描边**: 1.5px
- **尺寸**: 16px, 20px, 24px, 32px
- **端点**: 圆角 (round)
- **连接**: 圆角 (round)

---

## 2. Logo 系统

### 2.1 Logo 变体

| 文件名 | 用途 | 尺寸 |
|--------|------|------|
| `logo-main.svg` | 主Logo（横向） | 200x100 |
| `logo-white.svg` | 白色版本（深色背景） | 200x100 |
| `logo-icon.svg` | 图标版本 | 100x100 |
| `logo-square.svg` | 方形版本（应用图标） | 100x100 |

### 2.2 Logo 使用场景

```
┌─────────────────────────────────────────────────────────┐
│  场景                    │  推荐Logo                      │
├─────────────────────────────────────────────────────────┤
│  网站Header              │  logo-main.svg                 │
│  登录页                  │  logo-icon.svg / logo-main.svg │
│  深色背景                │  logo-white.svg                │
│  App图标                 │  logo-square.svg               │
│  Favicon                 │  logo-icon.svg                 │
│  小程序                  │  logo-square.svg               │
└─────────────────────────────────────────────────────────┘
```

### 2.3 Logo 存放位置

```
后台管理系统: src/assets/logo/
移动端H5:     src/assets/logo/
微信小程序:   src/static/logo/
```

---

## 3. 图标库

### 3.1 图标列表

#### 基础图标

| 图标名 | 文件名 | 用途 |
|--------|--------|------|
| 🏠 | `xiaohong-home.svg` | 首页 |
| 🔍 | `xiaohong-search.svg` | 搜索 |
| 📍 | `xiaohong-location.svg` | 位置 |
| 🗺️ | `xiaohong-map.svg` | 地图 |
| 👤 | `xiaohong-user.svg` | 用户 |
| ❤️ | `xiaohong-heart.svg` | 收藏/喜欢 |
| ⭐ | `xiaohong-star.svg` | 评分/收藏 |
| 📅 | `xiaohong-calendar.svg` | 日历/预约 |
| 📞 | `xiaohong-phone.svg` | 电话 |
| 💬 | `xiaohong-message.svg` | 消息 |
| ↗️ | `xiaohong-share.svg` | 分享 |
| ⚙️ | `xiaohong-filter.svg` | 筛选 |
| 🔑 | `xiaohong-key.svg` | 钥匙/租约 |
| 📄 | `xiaohong-contract.svg` | 合同 |

#### 操作图标

| 图标名 | 文件名 | 用途 |
|--------|--------|------|
| → | `xiaohong-arrow-right.svg` | 向右箭头 |
| ← | `xiaohong-arrow-left.svg` | 向左箭头 |
| ✓ | `xiaohong-check.svg` | 确认/完成 |
| ✕ | `xiaohong-close.svg` | 关闭/删除 |
| + | `xiaohong-plus.svg` | 添加 |
| - | `xiaohong-minus.svg` | 减少 |
| ☰ | `xiaohong-menu.svg` | 菜单 |
| ⋯ | `xiaohong-more.svg` | 更多 |
| ⚙️ | `xiaohong-settings.svg` | 设置 |
| 🔔 | `xiaohong-bell.svg` | 通知 |
| 🖼️ | `xiaohong-image.svg` | 图片 |
| ⬆️ | `xiaohong-upload.svg` | 上传 |

#### 业务图标

| 图标名 | 文件名 | 用途 |
|--------|--------|------|
| 🏢 | `xiaohong-apartment.svg` | 公寓 |

### 3.2 图标存放位置

```
后台管理系统: src/assets/icons/xiaohong/
移动端H5:     src/assets/icons/xiaohong/
微信小程序:   src/static/icons/xiaohong/
```

---

## 4. 使用方式

### 4.1 Vue3 组件中使用

```vue
<template>
  <!-- 直接使用 SVG -->
  <img src="@/assets/icons/xiaohong/xiaohong-home.svg" alt="首页" />
  
  <!-- 作为组件使用 -->
  <svg-icon name="xiaohong-home" />
</template>
```

### 4.2 CSS 背景使用

```css
.icon-home {
  background-image: url('@/assets/icons/xiaohong/xiaohong-home.svg');
  width: 24px;
  height: 24px;
  background-size: contain;
}
```

### 4.3 小程序中使用

```html
<!-- 微信小程序 -->
<image src="/static/icons/xiaohong/xiaohong-home.svg" mode="aspectFit" />
```

---

## 5. 图标尺寸规范

| 尺寸 | 用途 |
|------|------|
| 16px | 内联图标、标签 |
| 20px | 按钮图标 |
| 24px | 导航图标、功能图标（默认） |
| 32px | 空状态、大图标 |
| 48px | 超大图标、特色功能 |

---

## 6. 颜色规范

图标默认使用 `currentColor`，可随父元素颜色变化。

### 品牌色使用

```css
/* 主色图标 */
.icon-primary {
  color: #FF6B6B;
}

/* 次要色图标 */
.icon-secondary {
  color: #4A4A68;
}

/* 禁用状态 */
.icon-disabled {
  color: #B8B8CC;
}
```

---

## 7. 新增图标流程

1. 按照规范设计图标（24x24 视图框，1.5px 描边）
2. 命名格式：`xiaohong-{name}.svg`
3. 放入三个项目的对应目录
4. 更新本文档

### 7.1 SVG 模板

```svg
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" 
     fill="none" stroke="currentColor" stroke-width="1.5" 
     stroke-linecap="round" stroke-linejoin="round">
  <!-- 图标路径 -->
</svg>
```

---

## 8. 文件结构

```
assets/
├── logo/
│   ├── logo-main.svg       # 主Logo（横向）
│   ├── logo-white.svg      # 白色版本
│   ├── logo-icon.svg       # 图标版本
│   └── logo-square.svg     # 方形版本
└── icons/
    └── xiaohong/
        ├── xiaohong-home.svg
        ├── xiaohong-search.svg
        ├── xiaohong-location.svg
        ├── xiaohong-map.svg
        ├── xiaohong-user.svg
        ├── xiaohong-heart.svg
        ├── xiaohong-star.svg
        ├── xiaohong-calendar.svg
        ├── xiaohong-phone.svg
        ├── xiaohong-message.svg
        ├── xiaohong-share.svg
        ├── xiaohong-filter.svg
        ├── xiaohong-key.svg
        ├── xiaohong-contract.svg
        ├── xiaohong-apartment.svg
        ├── xiaohong-arrow-right.svg
        ├── xiaohong-arrow-left.svg
        ├── xiaohong-check.svg
        ├── xiaohong-close.svg
        ├── xiaohong-plus.svg
        ├── xiaohong-minus.svg
        ├── xiaohong-menu.svg
        ├── xiaohong-more.svg
        ├── xiaohong-settings.svg
        ├── xiaohong-bell.svg
        ├── xiaohong-image.svg
        └── xiaohong-upload.svg
```

---

## 9. 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.0 | 2026-04-03 | 初始版本，创建Logo变体和26个通用图标 |

---

> 💡 **使用提示**: 所有图标都遵循统一的设计规范，确保在不同平台和场景下保持一致性。
