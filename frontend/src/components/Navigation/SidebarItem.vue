<!--
  @Description: 侧边栏菜单项组件
  @Author: 小红
-->
<template>
  <div v-if="!item.meta || !item.meta.hidden" class="menu-item-wrapper">
    <!-- 单层菜单 -->
    <template v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren)">
      <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{ 'submenu-title-noDropdown': !isNest }">
        <el-icon v-if="onlyOneChild.meta && onlyOneChild.meta.icon" :size="18">
          <component :is="onlyOneChild.meta.icon" />
        </el-icon>
        <template #title>
          <span class="menu-title">{{ onlyOneChild.meta?.title || onlyOneChild.name }}</span>
        </template>
      </el-menu-item>
    </template>

    <!-- 多层菜单 -->
    <el-sub-menu v-else :index="resolvePath(item.path)" popper-class="sidebar-popper">
      <template #title>
        <el-icon v-if="item.meta && item.meta.icon" :size="18">
          <component :is="item.meta.icon" />
        </el-icon>
        <span class="menu-title">{{ item.meta?.title || item.name }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-sub-menu>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import path from 'path-browserify'
import { isExternal } from '@/utils/validate'
import type { RouteRecordRaw } from 'vue-router'

export default defineComponent({
  name: 'SidebarItem',
  props: {
    item: {
      type: Object as () => RouteRecordRaw,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    const onlyOneChild = ref<RouteRecordRaw | null>(null)

    const hasOneShowingChild = (children: RouteRecordRaw[] = [], parent: RouteRecordRaw) => {
      const showingChildren = children.filter(item => {
        if (item.meta?.hidden) {
          return false
        } else {
          onlyOneChild.value = item
          return true
        }
      })

      if (showingChildren.length === 1) {
        return true
      }

      if (showingChildren.length === 0) {
        onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
        return true
      }

      return false
    }

    const resolvePath = (routePath: string) => {
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(props.basePath)) {
        return props.basePath
      }
      return path.resolve(props.basePath, routePath)
    }

    return {
      onlyOneChild,
      hasOneShowingChild,
      resolvePath
    }
  }
})
</script>

<style scoped lang="scss">
.menu-item-wrapper {
  :deep(.el-menu-item) {
    .menu-title {
      font-size: 14px;
      font-weight: 400;
    }

    &.is-active {
      .menu-title {
        font-weight: 500;
      }
    }
  }

  :deep(.el-sub-menu__title) {
    .menu-title {
      font-size: 14px;
      font-weight: 500;
    }
  }
}

.nest-menu {
  :deep(.el-menu-item) {
    padding-left: 40px !important;
  }
}
</style>
