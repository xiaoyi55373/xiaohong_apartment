<!--
 * @Description: 导航栏组件 - 小红公寓claw (支持响应式)
-->
<template>
  <div class="nav-bar-container" :class="{ 'is-mobile': isMobile }">
    <el-row :gutter="15">
      <el-col :span="12">
        <div class="left-panel">
          <!-- 移动端菜单按钮 -->
          <el-icon v-if="isMobile" class="mobile-menu-btn" @click="handleToggleMobileMenu">
            <Fold />
          </el-icon>
          <!-- 桌面端折叠按钮 -->
          <el-icon v-else class="fold-unfold" @click="handleCollapse">
            <component :is="collapse ? 'Expand' : 'Fold'"></component>
          </el-icon>
          <!-- 面包屑 -->
          <Breadcrumb v-if="!isMobile" />
          <!-- 移动端页面标题 -->
          <span v-else class="mobile-page-title">{{ pageTitle }}</span>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="right-panel">
          <!-- 刷新按钮 -->
          <Refresh v-if="!isMobile" />
          <!-- 全屏按钮 -->
          <ScreenFull v-if="!isMobile" />
          <!-- 设置按钮 -->
          <Settings />
          <!-- 用户菜单 -->
          <User />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useSettingsStore } from '@/store/modules/settings'
import { useResponsive } from '@/hooks/useResponsive'
import User from './components/User/index.vue'
import Breadcrumb from './components/Breadcrumb/index.vue'
import Refresh from './components/Refresh/index.vue'
import ScreenFull from './components/ScreeFull/index.vue'
import Settings from './components/Settings/index.vue'
import { Fold, Expand } from '@element-plus/icons-vue'

export default defineComponent({
  components: {
    Refresh,
    User,
    ScreenFull,
    Breadcrumb,
    Settings,
    Fold,
    Expand,
  },
  emits: ['toggle-mobile-menu'],
  setup(props, { emit }) {
    const settingsStore = useSettingsStore()
    const route = useRoute()
    const { isMobile } = useResponsive()

    const collapse = computed(() => settingsStore.collapse)
    
    // 移动端页面标题
    const pageTitle = computed(() => {
      const title = route.meta.title as string
      return title || '小红公寓'
    })

    function handleCollapse() {
      settingsStore.changeCollapse()
    }
    
    function handleToggleMobileMenu() {
      emit('toggle-mobile-menu')
    }

    return {
      collapse,
      isMobile,
      pageTitle,
      handleCollapse,
      handleToggleMobileMenu,
    }
  },
})
</script>

<style scoped lang="scss">
.nav-bar-container {
  position: relative;
  height: $base-nav-bar-height;
  padding-right: $base-padding;
  padding-left: $base-padding;
  overflow: hidden;
  user-select: none;
  background: $base-color-white;
  box-shadow: $base-box-shadow;

  .left-panel {
    display: flex;
    align-items: center;
    justify-items: center;
    height: 60px;

    .fold-unfold {
      font-size: 18px;
      color: $base-color-gray;
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover {
        color: $base-color-default;
      }
    }
    
    .mobile-menu-btn {
      font-size: 20px;
      color: $base-color-gray;
      cursor: pointer;
      padding: 8px;
      margin-left: -8px;
      border-radius: 4px;
      transition: all 0.2s ease;
      
      &:hover {
        background: rgba(0, 0, 0, 0.05);
        color: $base-color-default;
      }
    }
    
    .mobile-page-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-left: 8px;
    }
  }

  .right-panel {
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: flex-end;
    height: $base-nav-bar-height;
    gap: 8px;
  }
  
  &.is-mobile {
    padding: 0 12px;
    
    .left-panel {
      .breadcrumb-container {
        display: none;
      }
    }
  }
}
</style>
