# 小红公寓claw 小程序包体积优化指南

> 📦 本文档记录小程序包体积优化策略和最佳实践

## 📊 当前包体积状况

| 指标 | 数值 | 状态 |
|------|------|------|
| 总包大小 | ~272KB | ✅ 优秀 |
| JavaScript | ~124KB (45.5%) | ✅ 正常 |
| WXSS样式 | ~72KB (26.4%) | ✅ 正常 |
| WXML模板 | ~49KB (18.1%) | ✅ 正常 |
| 图片资源 | ~23KB (8.6%) | ✅ 优秀 |

## 🎯 优化策略

### 1. 代码层面优化

#### 1.1 Tree Shaking
- 确保使用 ES Module 导入
- 避免全量导入组件库
- 使用按需加载

```typescript
// ❌ 不推荐
import uviewPlus from 'uview-plus';

// ✅ 推荐
import { Button, Cell } from 'uview-plus';
```

#### 1.2 代码压缩
- 启用 Terser 压缩
- 移除 console 和 debugger
- 启用死代码消除

```javascript
// vite.config.ts
terserOptions: {
  compress: {
    drop_console: true,
    drop_debugger: true,
    dead_code: true
  }
}
```

#### 1.3 分包加载
- 已配置分包策略
- 主包只保留核心页面
- 使用预加载规则

```json
// pages.json
{
  "subPackages": [
    {
      "root": "pages/apartment",
      "pages": [...]
    }
  ],
  "preloadRule": {
    "pages/index/index": {
      "network": "all",
      "packages": ["pages/apartment"]
    }
  }
}
```

### 2. 资源优化

#### 2.1 图片压缩
- SVG 文件已优化（节省 ~1.5KB）
- PNG 图片建议使用 tinypng.com 进一步压缩
- 使用 WebP 格式（如平台支持）

#### 2.2 图标优化
- 使用 SVG 图标代替 PNG
- 图标已统一使用 xiaohong 品牌图标库
- 避免重复图标资源

#### 2.3 字体优化
- 使用系统默认字体
- 如需自定义字体，使用字体子集化

### 3. 构建优化

#### 3.1 Vite 配置
```typescript
// vite.config.ts
export default defineConfig({
  build: {
    minify: 'terser',
    assetsInlineLimit: 4096, // 小于4KB资源内联
    rollupOptions: {
      output: {
        manualChunks: {
          // 代码分割
        }
      }
    }
  }
});
```

#### 3.2 分包配置
- 已启用 `optimization.subPackages`
- 已配置 `lazyCodeLoading: "requiredComponents"`

### 4. 运行时优化

#### 4.1 组件按需加载
```typescript
// 使用动态导入
const HeavyComponent = defineAsyncComponent(() => 
  import('./components/HeavyComponent.vue')
);
```

#### 4.2 图片懒加载
- 使用 `lazy-load` 属性
- 长列表使用虚拟滚动

## 🛠️ 优化工具

### 1. 包体积分析
```bash
# 分析构建后的包体积
npm run analyze

# 或使用脚本
node scripts/analyze-bundle.js
```

### 2. 图片优化
```bash
# 优化 SVG 文件
node scripts/optimize-images.js
```

### 3. 构建分析
```bash
# 生成可视化分析报告
npm run build:analyze
```

## 📈 监控指标

### 关键指标
| 指标 | 目标值 | 当前值 |
|------|--------|--------|
| 主包大小 | < 1MB | 272KB ✅ |
| 单个分包 | < 2MB | - |
| 总包大小 | < 5MB | 272KB ✅ |
| 首屏加载 | < 2s | - |

### 性能预算
- JavaScript: < 200KB
- CSS: < 100KB
- 图片: < 100KB

## 🔧 常用命令

```bash
# 开发模式
npm run dev:mp-weixin

# 生产构建
npm run build:mp-weixin

# 包体积分析
npm run analyze

# 图片优化
npm run optimize:images

# 构建所有平台
npm run build:all
```

## 📝 最佳实践

1. **定期分析包体积**
   - 每次发版前运行分析
   - 关注包体积变化趋势

2. **代码审查**
   - 禁止提交 console.log
   - 检查是否有重复代码
   - 避免引入大型依赖

3. **资源管理**
   - 图片上传前压缩
   - 删除无用资源文件
   - 使用 CDN 托管大文件

4. **分包策略**
   - 按业务模块分包
   - 非核心功能放入分包
   - 合理配置预加载

## 🚀 持续优化

### 待优化项
- [ ] 集成图片自动压缩到 CI/CD
- [ ] 添加包体积变化检测
- [ ] 优化 vendor.js 体积
- [ ] 评估 uview-plus 全量引入影响

### 优化记录
| 日期 | 优化内容 | 效果 |
|------|----------|------|
| 2026-04-03 | SVG 文件优化 | 节省 1.5KB |
| 2026-04-03 | Vite 配置优化 | 启用 Terser 压缩 |
| 2026-04-03 | 分包策略优化 | 已配置分包和预加载 |

---

*文档创建时间: 2026-04-03*
*作者: 小红*
