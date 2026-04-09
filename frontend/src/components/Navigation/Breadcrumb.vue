<!--
  @Description: 面包屑导航组件 - 小红公寓claw品牌定制
  @Author: 小红
-->
<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path">
        <span
          v-if="item.redirect === 'noRedirect' || index === breadcrumbs.length - 1"
          class="no-redirect"
        >
          {{ item.meta?.title || item.name }}
        </span>
        <a v-else @click.prevent="handleLink(item)">
          {{ item.meta?.title || item.name }}
        </a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script lang="ts">
import { defineComponent, ref, watch } from 'vue'
import { useRoute, useRouter, RouteLocationMatched } from 'vue-router'
import path from 'path-browserify'

export default defineComponent({
  name: 'Breadcrumb',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const breadcrumbs = ref<RouteLocationMatched[]>([])

    const getBreadcrumb = () => {
      let matched = route.matched.filter(item => item.meta && item.meta.title)
      const first = matched[0]

      if (!isDashboard(first)) {
        matched = [{ path: '/index', meta: { title: '首页' } } as any].concat(matched)
      }

      breadcrumbs.value = matched.filter(item => {
        return item.meta && item.meta.title && item.meta.breadcrumb !== false
      })
    }

    const isDashboard = (route: RouteLocationMatched) => {
      const name = route && route.name
      if (!name) {
        return false
      }
      return name.toString().trim().toLocaleLowerCase() === 'Dashboard'.toLocaleLowerCase()
    }

    const handleLink = (item: RouteLocationMatched) => {
      const { redirect, path: itemPath } = item
      if (redirect) {
        router.push(redirect as string)
        return
      }
      router.push(itemPath)
    }

    watch(
      () => route.path,
      () => {
        getBreadcrumb()
      },
      { immediate: true }
    )

    return {
      breadcrumbs,
      handleLink
    }
  }
})
</script>

<style scoped lang="scss">
.app-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 1;

  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      color: #8A8AA3;
      font-weight: 400;
      transition: color 0.25s ease;

      a {
        color: #8A8AA3;
        transition: color 0.25s ease;

        &:hover {
          color: #FF6B6B;
        }
      }

      &.no-redirect {
        color: #1A1A2E;
        font-weight: 500;
      }
    }

    &:last-child {
      .el-breadcrumb__inner {
        color: #1A1A2E;
        font-weight: 500;
      }
    }

    .el-breadcrumb__separator {
      color: #B8B8CC;
      margin: 0 8px;
    }
  }
}

// 面包屑动画
.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.3s ease;
}

.breadcrumb-enter-from,
.breadcrumb-leave-active {
  opacity: 0;
  transform: translateX(10px);
}

.breadcrumb-move {
  transition: all 0.3s ease;
}
</style>
