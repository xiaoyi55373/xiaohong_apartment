<template>
  <div class="empty-wrapper" :class="[`empty--${type}`, `empty--${size}`]">
    <div class="empty-illustration">
      <!-- 404 插画 -->
      <svg v-if="type === '404'" class="empty-svg" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
        <circle cx="100" cy="100" r="80" fill="#FFF5F5" />
        <circle cx="70" cy="85" r="8" fill="#FF6B6B" />
        <circle cx="130" cy="85" r="8" fill="#FF6B6B" />
        <path d="M75 115 Q100 100 125 115" stroke="#FF6B6B" stroke-width="4" fill="none" stroke-linecap="round" />
        <text x="100" y="165" text-anchor="middle" font-size="14" fill="#FF6B6B" font-weight="bold">404</text>
      </svg>

      <!-- 无数据 插画 -->
      <svg v-else-if="type === 'no-data'" class="empty-svg" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
        <rect x="50" y="60" width="100" height="80" rx="8" fill="#FFF5F5" stroke="#FF6B6B" stroke-width="2" stroke-dasharray="6 4" />
        <line x1="70" y1="90" x2="130" y2="90" stroke="#FFB4B4" stroke-width="3" stroke-linecap="round" />
        <line x1="70" y1="105" x2="110" y2="105" stroke="#FFB4B4" stroke-width="3" stroke-linecap="round" />
        <line x1="70" y1="120" x2="120" y2="120" stroke="#FFB4B4" stroke-width="3" stroke-linecap="round" />
        <circle cx="145" cy="55" r="15" fill="#FF6B6B" opacity="0.2" />
        <text x="145" y="60" text-anchor="middle" font-size="16" fill="#FF6B6B" font-weight="bold">?</text>
      </svg>

      <!-- 网络错误 插画 -->
      <svg v-else-if="type === 'network-error'" class="empty-svg" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
        <circle cx="100" cy="100" r="70" fill="#FFF5F5" />
        <path d="M60 100 L85 125 L140 70" stroke="#FF6B6B" stroke-width="5" fill="none" stroke-linecap="round" stroke-linejoin="round" opacity="0.3" />
        <path d="M60 100 L85 125 L140 70" stroke="#FF6B6B" stroke-width="5" fill="none" stroke-linecap="round" stroke-linejoin="round" stroke-dasharray="10 90" stroke-dashoffset="0">
          <animate attributeName="stroke-dashoffset" from="100" to="0" dur="1.5s" repeatCount="indefinite" />
        </path>
        <circle cx="145" cy="65" r="4" fill="#FF9F43" />
        <circle cx="60" cy="105" r="4" fill="#FF9F43" />
      </svg>

      <!-- 搜索为空 插画 -->
      <svg v-else-if="type === 'search-empty'" class="empty-svg" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
        <circle cx="85" cy="85" r="50" fill="none" stroke="#FF6B6B" stroke-width="3" />
        <line x1="122" y1="122" x2="155" y2="155" stroke="#FF6B6B" stroke-width="5" stroke-linecap="round" />
        <circle cx="75" cy="80" r="5" fill="#FF6B6B" />
        <circle cx="95" cy="80" r="5" fill="#FF6B6B" />
        <path d="M70 100 Q85 90 100 100" stroke="#FF6B6B" stroke-width="3" fill="none" stroke-linecap="round" />
      </svg>

      <!-- 通用错误 插画 -->
      <svg v-else-if="type === 'error'" class="empty-svg" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
        <circle cx="100" cy="100" r="70" fill="#FFF5F5" />
        <circle cx="75" cy="85" r="8" fill="#FF6B6B" />
        <circle cx="125" cy="85" r="8" fill="#FF6B6B" />
        <line x1="80" y1="125" x2="120" y2="125" stroke="#FF6B6B" stroke-width="4" stroke-linecap="round" />
      </svg>

      <!-- 无权限 插画 -->
      <svg v-else-if="type === 'unauthorized'" class="empty-svg" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
        <rect x="60" y="50" width="80" height="100" rx="8" fill="#FFF5F5" stroke="#FF6B6B" stroke-width="3" />
        <circle cx="100" cy="90" r="15" fill="#FF6B6B" />
        <rect x="92" y="82" width="16" height="16" rx="2" fill="#FFF5F5" />
        <line x1="100" y1="90" x2="100" y2="98" stroke="#FF6B6B" stroke-width="2" stroke-linecap="round" />
        <circle cx="100" cy="87" r="2" fill="#FF6B6B" />
        <path d="M80 120 Q100 110 120 120" stroke="#FF6B6B" stroke-width="3" fill="none" stroke-linecap="round" />
      </svg>
    </div>

    <div class="empty-content">
      <h3 class="empty-title">{{ displayTitle }}</h3>
      <p v-if="displayDescription" class="empty-description">{{ displayDescription }}</p>
    </div>

    <div v-if="showButton || $slots.action" class="empty-action">
      <slot name="action">
        <el-button v-if="showButton" :type="buttonType" :size="buttonSize" @click="handleClick">
          {{ buttonText }}
        </el-button>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 小红公寓claw - Empty 空状态组件
 * @description 用于展示 404、无数据、网络错误等空状态页面
 * @author 小红
 */

import { computed } from 'vue'
import { useRouter } from 'vue-router'

interface Props {
  /** 空状态类型 */
  type?: '404' | 'no-data' | 'network-error' | 'search-empty' | 'error' | 'unauthorized'
  /** 尺寸 */
  size?: 'small' | 'default' | 'large'
  /** 自定义标题 */
  title?: string
  /** 自定义描述 */
  description?: string
  /** 是否显示按钮 */
  showButton?: boolean
  /** 按钮文字 */
  buttonText?: string
  /** 按钮类型 */
  buttonType?: 'primary' | 'default'
}

const props = withDefaults(defineProps<Props>(), {
  type: 'no-data',
  size: 'default',
  title: '',
  description: '',
  showButton: false,
  buttonText: '',
  buttonType: 'primary'
})

const emit = defineEmits<{
  (e: 'click'): void
}>()

const router = useRouter()

const typeConfig: Record<string, { title: string; description: string; buttonText: string }> = {
  '404': {
    title: '页面找不到了',
    description: '抱歉，您访问的页面不存在或已被移除',
    buttonText: '返回首页'
  },
  'no-data': {
    title: '暂无数据',
    description: '当前没有任何内容，去看看其他的吧',
    buttonText: '刷新一下'
  },
  'network-error': {
    title: '网络开小差了',
    description: '请检查网络连接，或稍后重试',
    buttonText: '重新加载'
  },
  'search-empty': {
    title: '没有找到相关结果',
    description: '换个关键词试试看吧',
    buttonText: '清除筛选'
  },
  'error': {
    title: '出错了',
    description: '系统遇到了一点问题，请稍后重试',
    buttonText: '重新加载'
  },
  'unauthorized': {
    title: '暂无权限',
    description: '您没有访问该页面的权限，请联系管理员',
    buttonText: '返回首页'
  }
}

const displayTitle = computed(() => props.title || typeConfig[props.type].title)
const displayDescription = computed(() => props.description || typeConfig[props.type].description)
const defaultButtonText = computed(() => typeConfig[props.type].buttonText)
const resolvedButtonText = computed(() => props.buttonText || defaultButtonText.value)

const buttonSize = computed(() => {
  const sizeMap = { small: 'small', default: 'default', large: 'large' }
  return sizeMap[props.size]
})

function handleClick() {
  emit('click')
  if (props.type === '404' || props.type === 'unauthorized') {
    router.replace('/')
  } else if (props.type === 'network-error' || props.type === 'error') {
    location.reload()
  }
}
</script>

<style scoped lang="scss">
// 品牌色变量
$brand-primary: #FF6B6B;
$brand-secondary: #FF9F43;
$text-primary: #303133;
$text-secondary: #909399;

.empty-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px 20px;
}

.empty-illustration {
  margin-bottom: 24px;
}

.empty-svg {
  width: 160px;
  height: 160px;
}

.empty-content {
  max-width: 400px;
}

.empty-title {
  margin: 0 0 12px;
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  line-height: 1.4;
}

.empty-description {
  margin: 0;
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.6;
}

.empty-action {
  margin-top: 24px;
}

// 尺寸变体
.empty--small {
  padding: 24px 16px;

  .empty-svg {
    width: 100px;
    height: 100px;
  }

  .empty-title {
    font-size: 14px;
    margin-bottom: 8px;
  }

  .empty-description {
    font-size: 12px;
  }

  .empty-action {
    margin-top: 16px;
  }
}

.empty--large {
  padding: 60px 24px;

  .empty-svg {
    width: 220px;
    height: 220px;
  }

  .empty-title {
    font-size: 24px;
    margin-bottom: 16px;
  }

  .empty-description {
    font-size: 16px;
  }

  .empty-action {
    margin-top: 32px;
  }
}

// 页面级空状态（404等）
.empty--404,
.empty--unauthorized {
  min-height: calc(100vh - 200px);
}
</style>
