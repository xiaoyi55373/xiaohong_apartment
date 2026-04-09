<!--
  @Description: 侧边栏导航组件 - 小红公寓claw品牌定制
  @Author: 小红
-->
<template>
  <div class="sidebar-container" :class="{ 'is-collapse': collapse }">
    <!-- Logo区域 -->
    <div class="sidebar-logo">
      <div class="logo-wrapper">
        <img src="@/assets/logo.svg" alt="logo" class="logo-img" v-if="!collapse" />
        <img src="@/assets/images/logo.png" alt="logo" class="logo-img-small" v-else />
      </div>
      <span class="logo-text" v-show="!collapse">小红公寓</span>
    </div>

    <!-- 菜单区域 -->
    <el-scrollbar class="sidebar-scroll">
      <el-menu
        :background-color="menuBg"
        :text-color="menuTextColor"
        :active-text-color="menuActiveTextColor"
        :default-active="activeMenu"
        :collapse="collapse"
        :collapse-transition="false"
        :unique-opened="true"
        router
        class="sidebar-menu"
      >
        <sidebar-item
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>

    <!-- 底部折叠按钮 -->
    <div class="sidebar-footer">
      <div class="collapse-btn" @click="toggleCollapse">
        <el-icon :size="18">
          <Fold v-if="!collapse" />
          <Expand v-else />
        </el-icon>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useSettingsStore } from '@/store/modules/settings'
import { staticRoutes } from '@/router/constantRoutes'
import SidebarItem from './SidebarItem.vue'

export default defineComponent({
  name: 'SideBar',
  components: {
    SidebarItem
  },
  setup() {
    const route = useRoute()
    const settingsStore = useSettingsStore()

    const collapse = computed(() => settingsStore.collapse)
    const routes = computed(() => staticRoutes.filter(r => !r.meta?.hidden))
    
    const activeMenu = computed(() => {
      const { meta, path } = route
      if (meta?.activeMenu) {
        return meta.activeMenu as string
      }
      return path
    })

    // 品牌配色
    const menuBg = ref('#1A1A2E')
    const menuTextColor = ref('#8A8AA3')
    const menuActiveTextColor = ref('#FFFFFF')

    const toggleCollapse = () => {
      settingsStore.changeCollapse()
    }

    return {
      collapse,
      routes,
      activeMenu,
      menuBg,
      menuTextColor,
      menuActiveTextColor,
      toggleCollapse
    }
  }
})
</script>

<style scoped lang="scss">
.sidebar-container {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: $base-left-menu-width;
  height: 100vh;
  background: linear-gradient(180deg, #1A1A2E 0%, #252540 100%);
  box-shadow: 4px 0 24px rgba(26, 26, 46, 0.15);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: $base-z-index;
  display: flex;
  flex-direction: column;

  &.is-collapse {
    width: $base-left-menu-width-min;

    .sidebar-logo {
      padding: 16px 8px;
      justify-content: center;
    }
  }
}

// Logo区域
.sidebar-logo {
  display: flex;
  align-items: center;
  padding: 20px 24px;
  height: 64px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  transition: all 0.3s ease;

  .logo-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #FF6B6B 0%, #FF9F43 100%);
    border-radius: 10px;
    margin-right: 12px;
    flex-shrink: 0;
    box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);

    .logo-img {
      width: 24px;
      height: 24px;
    }

    .logo-img-small {
      width: 20px;
      height: 20px;
    }
  }

  .logo-text {
    font-size: 18px;
    font-weight: 600;
    color: #FFFFFF;
    letter-spacing: 0.5px;
    white-space: nowrap;
    background: linear-gradient(135deg, #FFFFFF 0%, #FFE0E0 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

// 菜单区域
.sidebar-scroll {
  flex: 1;
  overflow: hidden;

  :deep(.el-scrollbar__wrap) {
    overflow-x: hidden;
  }
}

.sidebar-menu {
  border: none;
  background: transparent;
  padding: 12px 0;

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: 48px;
    line-height: 48px;
    margin: 4px 12px;
    border-radius: 8px;
    transition: all 0.25s ease;

    &:hover {
      background: rgba(255, 107, 107, 0.1) !important;
      color: #FF6B6B !important;
    }

    .el-icon {
      font-size: 18px;
      margin-right: 12px;
    }
  }

  :deep(.el-menu-item) {
    &.is-active {
      background: linear-gradient(135deg, #FF6B6B 0%, #FF9F43 100%) !important;
      color: #FFFFFF !important;
      box-shadow: 0 4px 12px rgba(255, 107, 107, 0.35);
      font-weight: 500;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 20px;
        background: #FFFFFF;
        border-radius: 0 2px 2px 0;
      }

      .el-icon {
        color: #FFFFFF;
      }
    }
  }

  :deep(.el-sub-menu) {
    &.is-active {
      .el-sub-menu__title {
        color: #FF6B6B !important;
      }
    }

    .el-menu {
      background: rgba(0, 0, 0, 0.15) !important;
      border-radius: 8px;
      margin: 4px 12px;
      padding: 4px 0;

      .el-menu-item {
        height: 40px;
        line-height: 40px;
        margin: 2px 8px;
        font-size: 14px;

        &.is-active {
          background: rgba(255, 107, 107, 0.9) !important;
        }
      }
    }
  }
}

// 底部折叠按钮
.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);

  .collapse-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 40px;
    border-radius: 8px;
    cursor: pointer;
    color: #8A8AA3;
    transition: all 0.25s ease;

    &:hover {
      background: rgba(255, 107, 107, 0.1);
      color: #FF6B6B;
    }
  }
}

// 折叠状态适配
.is-collapse {
  .sidebar-menu {
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      margin: 4px 8px;
      padding: 0 12px !important;

      .el-icon {
        margin-right: 0;
      }
    }
  }

  .sidebar-footer {
    .collapse-btn {
      transform: rotate(180deg);
    }
  }
}
</style>
