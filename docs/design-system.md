# 小红公寓claw 设计系统 (Design System)

> 🎨 版本: v1.0.0
> 📅 创建日期: 2026-04-03
> 🏠 项目: 小红公寓claw

---

## 1. 品牌概述

### 1.1 品牌理念

**小红公寓claw** - "让租房更简单"

- **小红**: 代表温暖、活力、亲切的服务态度
- **claw**: 象征精准抓取、高效匹配，为用户找到理想居所

### 1.2 品牌个性

| 属性 | 描述 |
|------|------|
| 温暖 | 像家一样的温馨感受 |
| 活力 | 年轻、积极向上的品牌形象 |
| 专业 | 可靠的租房服务专家 |
| 简洁 | 高效、不繁琐的用户体验 |

---

## 2. 色彩系统 (Color System)

### 2.1 主色调

#### 品牌主色 - 珊瑚红 (Coral Red)

```
主色: #FF6B6B
RGB: 255, 107, 107
CMYK: 0, 58, 58, 0
```

**色阶扩展:**

| 色阶 | 色值 | 用途 |
|------|------|------|
| 50 | #FFF0F0 | 背景色、hover状态 |
| 100 | #FFE0E0 | 轻量背景 |
| 200 | #FFC1C1 | 禁用状态边框 |
| 300 | #FFA2A2 | 次要强调 |
| 400 | #FF8383 | 悬停状态 |
| 500 | #FF6B6B | **主色** |
| 600 | #E55A5A | 点击状态 |
| 700 | #CC4A4A | 深色调 |
| 800 | #B33B3B | 文字强调 |
| 900 | #992C2C | 最深色 |

### 2.2 辅助色

#### 活力橙 (Energy Orange)
```
#FF9F43 - 用于标签、促销、高亮
```

#### 清新绿 (Fresh Green)
```
#1DD1A1 - 用于成功状态、通过、可用
```

#### 天空蓝 (Sky Blue)
```
#54A0FF - 用于信息提示、链接、地图
```

#### 优雅紫 (Elegant Purple)
```
#A29BFE - 用于会员、VIP、特殊标识
```

### 2.3 中性色

| 名称 | 色值 | 用途 |
|------|------|------|
| 标题文字 | #1A1A2E | 主标题、重要文字 |
| 正文文字 | #4A4A68 | 正文、描述 |
| 次要文字 | #8A8AA3 | 辅助说明、placeholder |
| 禁用文字 | #B8B8CC | 禁用状态 |
| 边框浅色 | #E8E8EF | 分割线、边框 |
| 背景灰 | #F5F5FA | 页面背景 |
| 纯白 | #FFFFFF | 卡片背景 |
| 纯黑 | #000000 | 强调（少用） |

### 2.4 功能色

| 状态 | 色值 | 用途 |
|------|------|------|
| 成功 | #1DD1A1 | 操作成功、已签约 |
| 警告 | #FF9F43 | 待处理、提醒 |
| 错误 | #FF6B6B | 错误提示、已取消 |
| 信息 | #54A0FF | 提示信息 |

---

## 3. 字体系统 (Typography)

### 3.1 字体家族

```css
/* 中文 */
--font-zh: "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;

/* 英文/数字 */
--font-en: "Inter", -apple-system, BlinkMacSystemFont, sans-serif;

/* 代码/数据 */
--font-mono: "SF Mono", "Fira Code", monospace;
```

### 3.2 字体层级

| 层级 | 大小 | 字重 | 行高 | 用途 |
|------|------|------|------|------|
| H1 | 32px | 700 | 1.3 | 页面大标题 |
| H2 | 24px | 600 | 1.4 | 区块标题 |
| H3 | 20px | 600 | 1.4 | 卡片标题 |
| H4 | 18px | 600 | 1.5 | 小标题 |
| Body | 16px | 400 | 1.6 | 正文 |
| Small | 14px | 400 | 1.5 | 辅助文字 |
| Caption | 12px | 400 | 1.4 | 标签、说明 |

### 3.3 字体规范

```css
/* 页面标题 */
.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1A1A2E;
  line-height: 1.3;
}

/* 卡片标题 */
.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1A1A2E;
  line-height: 1.5;
}

/* 正文 */
.body-text {
  font-size: 16px;
  font-weight: 400;
  color: #4A4A68;
  line-height: 1.6;
}

/* 辅助文字 */
.helper-text {
  font-size: 14px;
  font-weight: 400;
  color: #8A8AA3;
  line-height: 1.5;
}
```

---

## 4. 间距系统 (Spacing)

### 4.1 基础间距

以 4px 为基础单位:

| Token | 值 | 用途 |
|-------|-----|------|
| space-1 | 4px | 图标间距、紧凑元素 |
| space-2 | 8px | 小间距、行内元素 |
| space-3 | 12px | 按钮内边距、小卡片 |
| space-4 | 16px | 标准间距、卡片内边距 |
| space-5 | 20px | 中等间距 |
| space-6 | 24px | 大间距、区块间距 |
| space-8 | 32px | 页面间距 |
| space-10 | 40px | 大区块间距 |
| space-12 | 48px | 超大间距 |

### 4.2 布局间距

```css
/* 页面内边距 */
--page-padding: 24px;

/* 卡片内边距 */
--card-padding: 16px;

/* 列表项间距 */
--list-gap: 12px;

/* 表单间距 */
--form-gap: 20px;

/* 区块间距 */
--section-gap: 32px;
```

---

## 5. 圆角系统 (Border Radius)

| Token | 值 | 用途 |
|-------|-----|------|
| radius-sm | 4px | 小按钮、标签 |
| radius-md | 8px | 标准按钮、输入框 |
| radius-lg | 12px | 卡片、对话框 |
| radius-xl | 16px | 大卡片、模态框 |
| radius-full | 9999px | 胶囊按钮、头像 |

---

## 6. 阴影系统 (Shadow)

```css
/* 轻微阴影 - 卡片默认 */
--shadow-sm: 0 1px 2px rgba(26, 26, 46, 0.05);

/* 标准阴影 - 卡片悬浮 */
--shadow-md: 0 4px 12px rgba(26, 26, 46, 0.08);

/* 明显阴影 - 下拉菜单、弹窗 */
--shadow-lg: 0 8px 24px rgba(26, 26, 46, 0.12);

/* 强烈阴影 - 模态框、抽屉 */
--shadow-xl: 0 16px 48px rgba(26, 26, 46, 0.16);

/* 品牌阴影 - 主色发光效果 */
--shadow-brand: 0 4px 16px rgba(255, 107, 107, 0.3);
```

---

## 7. 组件规范

### 7.1 按钮 (Button)

#### 主按钮

```css
.btn-primary {
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  border: none;
  transition: all 0.2s ease;
}

.btn-primary:hover {
  background: #E55A5A;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.3);
}

.btn-primary:active {
  background: #CC4A4A;
  transform: translateY(1px);
}
```

#### 次要按钮

```css
.btn-secondary {
  background: #FFFFFF;
  color: #FF6B6B;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  border: 1px solid #FF6B6B;
}
```

#### 文字按钮

```css
.btn-text {
  background: transparent;
  color: #FF6B6B;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
}
```

### 7.2 卡片 (Card)

```css
.card {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(26, 26, 46, 0.08);
  border: 1px solid #E8E8EF;
}

.card-hover:hover {
  box-shadow: 0 8px 24px rgba(26, 26, 46, 0.12);
  transform: translateY(-2px);
  transition: all 0.3s ease;
}
```

### 7.3 输入框 (Input)

```css
.input {
  height: 44px;
  padding: 0 16px;
  border: 1px solid #E8E8EF;
  border-radius: 8px;
  font-size: 16px;
  color: #1A1A2E;
  background: #FFFFFF;
  transition: all 0.2s ease;
}

.input:focus {
  border-color: #FF6B6B;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
  outline: none;
}

.input::placeholder {
  color: #8A8AA3;
}
```

### 7.4 标签 (Tag)

```css
.tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 500;
}

.tag-primary {
  background: #FFF0F0;
  color: #FF6B6B;
}

.tag-success {
  background: #E6FAF5;
  color: #1DD1A1;
}

.tag-warning {
  background: #FFF5EB;
  color: #FF9F43;
}
```

---

## 8. 图标系统 (Iconography)

### 8.1 图标规范

- **风格**: 线性图标 (Outline)
- **描边**: 1.5px
- **尺寸**: 16px, 20px, 24px, 32px
- **端点**: 圆角

### 8.2 图标尺寸

| 尺寸 | 用途 |
|------|------|
| 16px | 内联图标、标签 |
| 20px | 按钮图标 |
| 24px | 导航图标、功能图标 |
| 32px | 空状态、大图标 |

---

## 9. 动画规范 (Animation)

### 9.1 过渡时间

```css
--duration-fast: 150ms;    /* 微交互 */
--duration-normal: 250ms;  /* 标准过渡 */
--duration-slow: 350ms;    /* 复杂动画 */
```

### 9.2 缓动函数

```css
--ease-default: cubic-bezier(0.4, 0, 0.2, 1);
--ease-in: cubic-bezier(0.4, 0, 1, 1);
--ease-out: cubic-bezier(0, 0, 0.2, 1);
--ease-bounce: cubic-bezier(0.68, -0.55, 0.265, 1.55);
```

### 9.3 常用动画

```css
/* 淡入 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* 上滑淡入 */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 脉冲（用于提示） */
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}
```

---

## 10. 响应式断点

| 断点 | 宽度 | 设备 |
|------|------|------|
| xs | < 576px | 手机 |
| sm | ≥ 576px | 大屏手机 |
| md | ≥ 768px | 平板 |
| lg | ≥ 992px | 小型桌面 |
| xl | ≥ 1200px | 标准桌面 |
| xxl | ≥ 1400px | 大屏桌面 |

---

## 11. 应用示例

### 11.1 品牌Logo使用

- 主Logo: 珊瑚红圆形背景 + 白色"claw"文字
- 反白Logo: 白色背景 + 珊瑚红"claw"文字
- 简化版: 仅爪形图标

### 11.2 典型页面配色

**登录页:**
- 背景: 渐变 (#FF6B6B → #FF9F43)
- 卡片: 白色
- 主按钮: 珊瑚红

**首页:**
- 背景: #F5F5FA
- 卡片: 白色
- 强调: 珊瑚红

**详情页:**
- 背景: 白色
- 标题: #1A1A2E
- 正文: #4A4A68
- 标签: 珊瑚红/活力橙

---

## 12. 文件资源

### 12.1 Logo文件

```
assets/
├── logo/
│   ├── logo-main.svg       # 主Logo
│   ├── logo-white.svg      # 白色版本
│   ├── logo-icon.svg       # 图标版本
│   ├── logo-horizontal.svg # 横向版本
│   └── favicon.ico         # 网站图标
```

### 12.2 设计稿

- Figma: [设计稿链接]
- 原型: [原型链接]

---

## 13. 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.0 | 2026-04-03 | 初始版本，建立完整设计系统 |

---

> 💡 **使用提示**: 本设计系统适用于小红公寓claw项目的所有前端开发，包括后台管理系统、移动端H5和微信小程序。请确保所有UI实现都遵循本规范，保持品牌一致性。
