<template>
  <div class="layout-admin-wrapper">
    <div class="layout-container-vertical fixed" :class="{ 'is-mobile': isMobile }">
      <!-- sideBar - 桌面端显示，移动端隐藏 -->
      <LayoutSideBar v-if="!isMobile" />
      
      <!-- 移动端侧边栏抽屉 -->
      <el-drawer
        v-model="mobileDrawerVisible"
        size="80%"
        :with-header="false"
        :append-to-body="true"
        class="mobile-sidebar-drawer"
        destroy-on-close
      >
        <LayoutSideBarMobile @close="mobileDrawerVisible = false" />
      </el-drawer>
      
      <!-- 遮罩层 -->
      <div 
        class="mobile-overlay" 
        :class="{ 'is-show': mobileDrawerVisible }"
        @click="mobileDrawerVisible = false"
      ></div>

      <div class="layout-main" :class="{ 'is-collapse': collapse, 'is-mobile': isMobile }">
        <!-- Header -->
        <div
          class="layout-header fixed-header"
          :class="{ 'is-collapse': collapse, 'is-mobile': isMobile }"
        >
          <LayoutNavBar @toggle-mobile-menu="mobileDrawerVisible = true" />
          <LayoutTabsBar v-if="!isMobile" />
        </div>
        <div class="app-main-container" :class="{ 'is-mobile': isMobile }">
          <!-- Main -->
          <LayoutMain />
          <!-- Footer -->
          <LayoutFooter v-if="!isMobile" />
        </div>
      </div>
    </div>
    
    <!-- 移动端底部导航 -->
    <MobileNavBar />
    
    <!-- 主题切换 -->
    <ThemeDrawer />
  </div>
</template>

<script lang="ts">
import { defineComponent, onBeforeUnmount, computed, ref, watch } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { useDebounceFn } from '@vueuse/core'
import { useResponsive } from '@/hooks/useResponsive'
import LayoutFooter from './Footer/index.vue'
import LayoutMain from './Main/index.vue'
import LayoutSideBar from './SideBar/index.vue'
import LayoutSideBarMobile from './SideBar/mobile.vue'
import LayoutNavBar from './NavBar/index.vue'
import LayoutTabsBar from './TabsBar/index.vue'
import MobileNavBar from './MobileNavBar/index.vue'
import ThemeDrawer from './NavBar/components/ThemeDrawer/index.vue'

export default defineComponent({
  components: {
    LayoutFooter,
    LayoutMain,
    LayoutSideBar,
    LayoutSideBarMobile,
    LayoutNavBar,
    LayoutTabsBar,
    MobileNavBar,
    ThemeDrawer,
  },
  setup() {
    const settingsStore = useSettingsStore()
    const collapse = computed(() => settingsStore.collapse)
    const { isMobile, isTablet } = useResponsive()
    
    // 移动端侧边栏抽屉显示状态
    const mobileDrawerVisible = ref(false)

    // 监听窗口大小变化，自动折叠侧边栏
    const listeningWindow = useDebounceFn(() => {
      const screenWidth = document.body.clientWidth
      if (!collapse.value && screenWidth < 1200)
        settingsStore.changeCollapse()
      if (collapse.value && screenWidth > 1200)
        settingsStore.changeCollapse()
    }, 100)

    window.addEventListener('resize', listeningWindow, false)
    
    // 监听移动端状态变化，自动关闭抽屉
    watch(isMobile, (val) => {
      if (!val) {
        mobileDrawerVisible.value = false
      }
    })
    
    // 监听平板状态，自动折叠侧边栏
    watch(isTablet, (val) => {
      if (val && !collapse.value) {
        settingsStore.changeCollapse()
      }
    })

    onBeforeUnmount(() => {
      window.removeEventListener('resize', listeningWindow)
    })
    
    return {
      collapse,
      isMobile,
      mobileDrawerVisible,
    }
  },
})
</script>

<style scoped lang="scss">
@mixin fix-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: $base-z-index - 2;
  width: calc(100% - $base-left-menu-width);
}

.layout-admin-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: auto;

  .layout-container-vertical {
    &.fixed {
      padding-top: calc(#{$base-top-bar-height} + #{$base-tabs-bar-height});
      
      &.is-mobile {
        padding-top: $base-nav-bar-height;
      }
    }

    .layout-main {
      min-height: 100%;
      margin-left: $base-left-menu-width;

      &.is-collapse {
        margin-left: $base-left-menu-width-min;
        border-right: 0;
      }
      
      &.is-mobile {
        margin-left: 0 !important;
        
        &.is-collapse {
          margin-left: 0 !important;
        }
      }

      .layout-header {
        box-shadow: 0 1px 4px rgb(0 21 41 / 8%);

        &.fixed-header {
          @include fix-header;
        }

        &.is-collapse {
          width: calc(100% - $base-left-menu-width-min);
        }
        
        &.is-mobile {
          width: 100% !important;
          left: 0 !important;
          
          &.is-collapse {
            width: 100% !important;
          }
        }
      }

      .app-main-container {
        padding: 20px;
        
        &.is-mobile {
          padding: 12px;
          padding-bottom: calc(60px + 12px + env(safe-area-inset-bottom, 0));
        }
      }
    }
  }
}

// 移动端侧边栏抽屉
.mobile-sidebar-drawer {
  :deep(.el-drawer) {
    background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%) !important;
  }
}

// 遮罩层
.mobile-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: $base-z-index - 3;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;

  &.is-show {
    opacity: 1;
    visibility: visible;
  }
}
</style>
