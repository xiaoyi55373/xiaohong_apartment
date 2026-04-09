<template>
  <div class="error-page">
    <Empty type="404" size="large" show-button @click="goHome" />
  </div>
</template>

<script setup lang="ts">
/**
 * 小红公寓claw - 404 错误页面
 * @description 页面不存在时展示的空状态页面
 * @author 小红
 */

import { useRouter } from 'vue-router'
import Empty from '@/components/Empty/index.vue'

const router = useRouter()

function goHome() {
  router.replace('/')
}
</script>

<style scoped lang="scss">
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
</style>
