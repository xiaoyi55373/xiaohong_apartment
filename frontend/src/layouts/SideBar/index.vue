<template>
  <div class="layout-sidebar-container" :class="{ 'is-collapse': collapse, 'is-dark': themeConfig.isDark }">
    <!-- Logo区域 -->
    <Logo />

    <!-- 菜单区域 -->
    <el-scrollbar class="sidebar-scrollbar">
      <el-menu
        :default-active="activeMenu"
        :collapse="collapse"
        :collapse-transition="true"
        :unique-opened="true"
        router
        class="sidebar-menu"
      >
        <!-- 首页 -->
        <el-menu-item index="/index" class="menu-item">
          <div class="menu-icon-wrapper">
            <el-icon class="menu-icon">
              <HomeFilled />
            </el-icon>
          </div>
          <template #title>
            <span class="menu-title">首页</span>
          </template>
        </el-menu-item>

        <!-- 系统管理 -->
        <el-sub-menu index="1" class="sub-menu">
          <template #title>
            <div class="menu-icon-wrapper">
              <el-icon class="menu-icon">
                <Setting />
              </el-icon>
            </div>
            <span class="menu-title">系统管理</span>
          </template>
          <el-menu-item index="/system/user" class="sub-menu-item">
            <span class="sub-menu-dot"></span>
            <span class="sub-menu-title">用户管理</span>
          </el-menu-item>
          <el-menu-item index="/system/post" class="sub-menu-item">
            <span class="sub-menu-dot"></span>
            <span class="sub-menu-title">岗位管理</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 公寓管理 -->
        <el-sub-menu index="2" class="sub-menu">
          <template #title>
            <div class="menu-icon-wrapper">
              <el-icon class="menu-icon">
                <OfficeBuilding />
              </el-icon>
            </div>
            <span class="menu-title">公寓管理</span>
          </template>
          <el-menu-item index="/apartmentManagement/apartmentManagement/apartmentManagement" class="sub-menu-item">
            <span class="sub-menu-dot"></span>
            <span class="sub-menu-title">公寓管理</span>
          </el-menu-item>
          <el-menu-item index="/apartmentManagement/roomManagement/roomManagement" class="sub-menu-item">
            <span class="sub-menu-dot"></span>
            <span class="sub-menu-title">房间管理</span>
          </el-menu-item>
          <el-menu-item index="/apartmentManagement/attributeManagement/attributeManagement" class="sub-menu-item">
            <span class="sub-menu-dot"></span>
            <span class="sub-menu-title">属性管理</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 租赁管理 -->
        <el-sub-menu index="3" class="sub-menu">
          <template #title>
            <div class="menu-icon-wrapper">
              <el-icon class="menu-icon">
                <Management />
              </el-icon>
            </div>
            <span class="menu-title">租赁管理</span>
          </template>
          <el-menu-item index="/rentManagement/appointment/appointment" class="sub-menu-item">
            <span class="sub-menu-dot"></span>
            <span class="sub-menu-title">看房预约管理</span>
          </el-menu-item>
          <el-menu-item index="/agreementManagement/agreement/agreement" class="sub-menu-item">
            <span class="sub-menu-dot"></span>
            <span class="sub-menu-title">租约管理</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 用户管理 -->
        <el-menu-item index="/userManagement/userManagement" class="menu-item">
          <div class="menu-icon-wrapper">
            <el-icon class="menu-icon">
              <User />
            </el-icon>
          </div>
          <template #title>
            <span class="menu-title">用户管理</span>
          </template>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>

    <!-- 底部折叠按钮 -->
    <div class="sidebar-footer" @click="toggleCollapse">
      <div class="collapse-btn" :class="{ 'is-collapse': collapse }">
        <el-icon class="collapse-icon">
          <Fold v-if="!collapse" />
          <Expand v-else />
        </el-icon>
        <span v-if="!collapse" class="collapse-text">收起菜单</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { useRoute } from 'vue-router'
import Logo from '../Logo/index.vue'
import {
  HomeFilled,
  Setting,
  OfficeBuilding,
  Management,
  User,
  Fold,
  Expand
} from '@element-plus/icons-vue'

export default defineComponent({
  name: 'SideBar',
  components: {
    Logo,
    HomeFilled,
    Setting,
    OfficeBuilding,
    Management,
    User,
    Fold,
    Expand
  },
  setup() {
    const settingsStore = useSettingsStore()
    const route = useRoute()

    const collapse = computed(() => settingsStore.collapse)
    const themeConfig = computed(() => settingsStore.themeConfig)
    const activeMenu = computed(() =>
      route.meta.activeMenu ? (route.meta.activeMenu as string) : route.path
    )

    const toggleCollapse = () => {
      settingsStore.changeCollapse()
    }

    return {
      collapse,
      activeMenu,
      themeConfig,
      toggleCollapse
    }
  },
})
</script>

<style scoped lang="scss">
// 侧边栏容器
.layout-sidebar-container {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: $base-z-index;
  width: $base-left-menu-width;
  height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f172a 100%);
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.15);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;

  &.is-collapse {
    width: $base-left-menu-width-min;
  }
}

// 滚动区域
.sidebar-scrollbar {
  flex: 1;
  overflow: hidden;

  :deep(.el-scrollbar__wrap) {
    overflow-x: hidden;
  }

  :deep(.el-scrollbar__bar) {
    &.is-vertical {
      width: 4px;
      right: 2px;
    }

    .el-scrollbar__thumb {
      background: rgba(255, 255, 255, 0.2);
      border-radius: 2px;

      &:hover {
        background: rgba(255, 255, 255, 0.3);
      }
    }
  }
}

// 菜单样式
.sidebar-menu {
  background: transparent !important;
  border-right: none !important;
  padding: 8px 0;

  // 菜单项通用样式
  .menu-item,
  :deep(.el-sub-menu__title),
  :deep(.el-menu-item) {
    height: $base-menu-item-height;
    line-height: $base-menu-item-height;
    margin: 4px 12px;
    padding: 0 16px !important;
    border-radius: 10px;
    color: rgba(255, 255, 255, 0.75);
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      color: #ffffff;
      background: rgba(255, 255, 255, 0.08) !important;
    }

    &.is-active {
      color: #ffffff;
      background: linear-gradient(135deg, rgba(255, 107, 107, 0.9) 0%, rgba(255, 159, 67, 0.9) 100%) !important;
      box-shadow: 0 4px 12px rgba(255, 107, 107, 0.35);

      .menu-icon {
        color: #ffffff;
      }
    }
  }

  // 一级菜单项
  .menu-item {
    display: flex;
    align-items: center;
  }

  // 子菜单
  :deep(.el-sub-menu) {
    &.is-active {
      .el-sub-menu__title {
        color: #ffffff;
        background: rgba(255, 255, 255, 0.05) !important;
      }

      .menu-icon {
        color: #FF6B6B;
      }
    }

    .el-sub-menu__icon-arrow {
      color: rgba(255, 255, 255, 0.5);
      font-size: 12px;
      transition: transform 0.3s ease;
    }

    &.is-opened {
      .el-sub-menu__icon-arrow {
        transform: rotateZ(180deg);
      }
    }
  }

  // 子菜单项
  :deep(.el-sub-menu .el-menu) {
    background: transparent !important;
    padding: 4px 0;

    .el-menu-item {
      height: 44px;
      line-height: 44px;
      margin: 2px 12px 2px 24px;
      padding: 0 16px !important;
      font-size: 14px;

      &.is-active {
        background: linear-gradient(90deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 107, 107, 0.05) 100%) !important;
        box-shadow: none;
        color: #FF6B6B;

        .sub-menu-dot {
          background: #FF6B6B;
          box-shadow: 0 0 6px rgba(255, 107, 107, 0.6);
        }
      }
    }
  }

  // 折叠状态样式
  &.el-menu--collapse {
    .menu-item,
    :deep(.el-sub-menu__title) {
      margin: 4px 10px;
      padding: 0 !important;
      justify-content: center;
    }

    .menu-icon-wrapper {
      margin-right: 0;
    }
  }
}

// 图标包装器
.menu-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  margin-right: 12px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  transition: all 0.25s ease;

  .menu-icon {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.7);
    transition: all 0.25s ease;
  }
}

// 菜单标题
.menu-title {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

// 子菜单项样式
.sub-menu-item {
  display: flex !important;
  align-items: center;

  .sub-menu-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    margin-right: 10px;
    transition: all 0.25s ease;
  }

  .sub-menu-title {
    font-size: 13px;
  }

  &:hover {
    .sub-menu-dot {
      background: rgba(255, 255, 255, 0.6);
    }
  }
}

// 底部折叠按钮
.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  cursor: pointer;
  transition: all 0.25s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.03);
  }

  .collapse-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 40px;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.05);
    transition: all 0.25s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }

    &.is-collapse {
      .collapse-icon {
        transform: rotate(180deg);
      }
    }

    .collapse-icon {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.7);
      transition: all 0.3s ease;
    }

    .collapse-text {
      margin-left: 8px;
      font-size: 13px;
      color: rgba(255, 255, 255, 0.7);
    }
  }
}

// 菜单折叠动画
:deep(.el-menu--collapse) {
  .el-sub-menu__title {
    .menu-title,
    .el-sub-menu__icon-arrow {
      display: none;
    }
  }
}

// 菜单弹出层样式（折叠后hover显示）
:deep(.el-menu--popup) {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%) !important;
  border-radius: 10px;
  padding: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);

  .el-menu-item {
    height: 44px;
    line-height: 44px;
    border-radius: 8px;
    color: rgba(255, 255, 255, 0.75);

    &:hover {
      color: #ffffff;
      background: rgba(255, 255, 255, 0.08) !important;
    }

    &.is-active {
      color: #FF6B6B;
      background: rgba(255, 107, 107, 0.1) !important;
    }
  }
}
</style>
