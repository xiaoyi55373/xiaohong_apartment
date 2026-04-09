<!--
  @Description: 标签页导航组件 - 小红公寓claw品牌定制
  @Author: 小红
-->
<template>
  <div class="tabsbar-container">
    <div class="tabs-wrapper">
      <el-tabs
        v-model="activeTab"
        type="card"
        class="tabs-nav"
        @tab-click="handleTabClick"
        @tab-remove="handleTabRemove"
      >
        <el-tab-pane
          v-for="item in visitedViews"
          :key="item.path"
          :label="item.title"
          :name="item.path"
          :closable="!isAffix(item)"
        >
          <template #label>
            <span class="tab-label">
              <el-icon v-if="item.icon" :size="14" class="tab-icon">
                <component :is="item.icon" />
              </el-icon>
              {{ item.title }}
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>
    </div>

    <div class="tabs-actions">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="action-btn">
          <el-icon :size="16"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="tabs-dropdown-menu">
            <el-dropdown-item command="refresh">
              <el-icon><Refresh /></el-icon>
              <span>刷新当前</span>
            </el-dropdown-item>
            <el-dropdown-item command="closeCurrent" divided>
              <el-icon><Close /></el-icon>
              <span>关闭当前</span>
            </el-dropdown-item>
            <el-dropdown-item command="closeOthers">
              <el-icon><CircleClose /></el-icon>
              <span>关闭其他</span>
            </el-dropdown-item>
            <el-dropdown-item command="closeAll">
              <el-icon><Delete /></el-icon>
              <span>关闭全部</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTabsBarStore } from '@/store/modules/tabsBar'
import type { TabPaneName } from 'element-plus'

interface VisitedView {
  path: string
  title: string
  icon?: string
  affix?: boolean
}

export default defineComponent({
  name: 'TabsBar',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const tabsBarStore = useTabsBarStore()

    const activeTab = ref('')

    const visitedViews = computed<VisitedView[]>(() => {
      return tabsBarStore.visitedViews.map(v => ({
        path: v.path,
        title: v.title || v.meta?.title || '未命名',
        icon: v.meta?.icon as string,
        affix: v.meta?.affix as boolean
      }))
    })

    const isAffix = (view: VisitedView) => {
      return view.affix || view.path === '/index' || view.path === '/dashboard'
    }

    const addView = () => {
      const { name, path, meta } = route
      if (name === 'Login') return
      if (path) {
        tabsBarStore.addView(route as any)
      }
    }

    const handleTabClick = (tab: any) => {
      const path = tab.props.name as string
      if (path === route.path) return
      router.push(path)
    }

    const handleTabRemove = (name: TabPaneName) => {
      const path = name as string
      tabsBarStore.delView(path)
      if (path === route.path) {
        toLastView()
      }
    }

    const toLastView = () => {
      const views = visitedViews.value
      const currentIndex = views.findIndex(v => v.path === route.path)
      const nextView = views[currentIndex - 1] || views[currentIndex + 1]
      if (nextView) {
        router.push(nextView.path)
      } else {
        router.push('/index')
      }
    }

    const handleCommand = (command: string) => {
      switch (command) {
        case 'refresh':
          // 刷新当前页面
          router.replace({
            path: '/redirect' + route.fullPath
          })
          break
        case 'closeCurrent':
          if (!isAffix({ path: route.path, title: '' })) {
            tabsBarStore.delView(route.path)
            toLastView()
          }
          break
        case 'closeOthers':
          tabsBarStore.delOtherViews(route.path)
          break
        case 'closeAll':
          tabsBarStore.delAllViews()
          router.push('/index')
          break
      }
    }

    watch(
      () => route.path,
      () => {
        addView()
        activeTab.value = route.path
      },
      { immediate: true }
    )

    onMounted(() => {
      addView()
    })

    return {
      activeTab,
      visitedViews,
      isAffix,
      handleTabClick,
      handleTabRemove,
      handleCommand
    }
  }
})
</script>

<style scoped lang="scss">
.tabsbar-container {
  display: flex;
  align-items: center;
  height: 44px;
  background: #FFFFFF;
  border-bottom: 1px solid #E8E8EF;
  padding: 0 16px;
}

.tabs-wrapper {
  flex: 1;
  overflow: hidden;

  :deep(.tabs-nav) {
    .el-tabs__header {
      margin: 0;
      border: none;
    }

    .el-tabs__nav {
      border: none;
      border-radius: 0;
    }

    .el-tabs__item {
      height: 44px;
      line-height: 44px;
      padding: 0 16px !important;
      border: none;
      border-right: 1px solid #E8E8EF;
      color: #8A8AA3;
      font-size: 13px;
      font-weight: 400;
      transition: all 0.25s ease;

      &:first-child {
        border-left: 1px solid #E8E8EF;
      }

      &:hover {
        color: #FF6B6B;
        background: rgba(255, 107, 107, 0.05);
      }

      &.is-active {
        color: #FF6B6B;
        background: rgba(255, 107, 107, 0.08);
        font-weight: 500;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 2px;
          background: linear-gradient(90deg, #FF6B6B 0%, #FF9F43 100%);
        }
      }

      .tab-label {
        display: flex;
        align-items: center;
        gap: 6px;

        .tab-icon {
          color: inherit;
        }
      }

      .is-icon-close {
        margin-left: 8px;
        width: 14px;
        height: 14px;
        line-height: 14px;
        border-radius: 50%;
        transition: all 0.2s ease;

        &:hover {
          background: rgba(255, 107, 107, 0.2);
          color: #FF6B6B;
        }
      }
    }
  }
}

.tabs-actions {
  display: flex;
  align-items: center;
  padding-left: 12px;
  border-left: 1px solid #E8E8EF;

  .action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border-radius: 6px;
    cursor: pointer;
    color: #8A8AA3;
    transition: all 0.25s ease;

    &:hover {
      background: rgba(255, 107, 107, 0.1);
      color: #FF6B6B;
    }
  }
}

// 下拉菜单样式
:deep(.tabs-dropdown-menu) {
  padding: 8px;
  min-width: 140px;

  .el-dropdown-menu__item {
    padding: 10px 16px;
    border-radius: 6px;
    margin: 2px 0;
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 13px;
    color: #4A4A68;
    transition: all 0.2s ease;

    &:hover {
      background: rgba(255, 107, 107, 0.1);
      color: #FF6B6B;
    }

    .el-icon {
      font-size: 14px;
    }
  }
}
</style>
