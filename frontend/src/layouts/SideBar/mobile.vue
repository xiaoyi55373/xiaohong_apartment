<template>
  <div class="mobile-sidebar">
    <!-- 头部 Logo -->
    <div class="drawer-header">
      <div class="logo-area">
        <img src="/logo.png" alt="logo" />
        <span class="brand-text">小红公寓</span>
      </div>
      <el-icon class="close-btn" @click="handleClose">
        <Close />
      </el-icon>
    </div>

    <!-- 用户信息 -->
    <div class="user-info">
      <el-avatar :size="48" :src="userInfo.avatar || defaultAvatar" />
      <div class="user-detail">
        <div class="user-name">{{ userInfo.name || '管理员' }}</div>
        <div class="user-role">{{ userInfo.role || '超级管理员' }}</div>
      </div>
    </div>

    <!-- 菜单区域 -->
    <el-scrollbar class="menu-scrollbar">
      <el-menu
        :default-active="activeMenu"
        class="drawer-menu"
        @select="handleSelect"
      >
        <!-- 首页 -->
        <el-menu-item index="/index" class="menu-item">
          <el-icon class="menu-icon"><HomeFilled /></el-icon>
          <span class="menu-title">首页</span>
        </el-menu-item>

        <!-- 系统管理 -->
        <el-sub-menu index="1" class="sub-menu">
          <template #title>
            <el-icon class="menu-icon"><Setting /></el-icon>
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
            <el-icon class="menu-icon"><OfficeBuilding /></el-icon>
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
            <el-icon class="menu-icon"><Management /></el-icon>
            <span class="menu-title">租赁管理</span>
          </template>
          <el-menu-item index="/rentManagement/appointment/appointment" class="sub-menu-item">
            <span class="sub-menu-dot"></span>
            <span class="sub-menu-title">看房预约</span>
          </el-menu-item>
          <el-menu-item index="/agreementManagement/agreement/agreement" class="sub-menu-item">
            <span class="sub-menu-dot"></span>
            <span class="sub-menu-title">租约管理</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 用户管理 -->
        <el-menu-item index="/userManagement/userManagement" class="menu-item">
          <el-icon class="menu-icon"><User /></el-icon>
          <span class="menu-title">用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>

    <!-- 底部操作 -->
    <div class="drawer-footer">
      <div class="footer-item" @click="handleSetting">
        <el-icon><Setting /></el-icon>
        <span>设置</span>
      </div>
      <div class="footer-item" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        <span>退出</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { ElMessageBox } from 'element-plus'
import {
  HomeFilled,
  Setting,
  OfficeBuilding,
  Management,
  User,
  Close,
  SwitchButton,
} from '@element-plus/icons-vue'

export default defineComponent({
  name: 'SideBarMobile',
  components: {
    HomeFilled,
    Setting,
    OfficeBuilding,
    Management,
    User,
    Close,
    SwitchButton,
  },
  emits: ['close'],
  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()
    const userStore = useUserStore()

    const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    
    const userInfo = computed(() => userStore.userInfo || {})
    
    const activeMenu = computed(() =>
      route.meta.activeMenu ? (route.meta.activeMenu as string) : route.path
    )

    const handleSelect = (index: string) => {
      router.push(index)
      emit('close')
    }

    const handleClose = () => {
      emit('close')
    }

    const handleSetting = () => {
      // 打开设置
      emit('close')
    }

    const handleLogout = () => {
      ElMessageBox.confirm('确定退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        userStore.logout()
        router.push('/login')
        emit('close')
      })
    }

    return {
      userInfo,
      defaultAvatar,
      activeMenu,
      handleSelect,
      handleClose,
      handleSetting,
      handleLogout,
    }
  },
})
</script>

<style scoped lang="scss">
.mobile-sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);

  .drawer-header {
    padding: 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);

    .logo-area {
      display: flex;
      align-items: center;
      gap: 10px;

      img {
        width: 36px;
        height: 36px;
        border-radius: 8px;
      }

      .brand-text {
        color: #fff;
        font-size: 18px;
        font-weight: 600;
      }
    }

    .close-btn {
      color: rgba(255, 255, 255, 0.7);
      font-size: 20px;
      cursor: pointer;
      padding: 4px;
      border-radius: 4px;
      transition: all 0.2s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        color: #fff;
      }
    }
  }

  .user-info {
    padding: 20px 16px;
    display: flex;
    align-items: center;
    gap: 12px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);

    .user-detail {
      flex: 1;
      min-width: 0;

      .user-name {
        color: #fff;
        font-size: 16px;
        font-weight: 500;
        margin-bottom: 4px;
      }

      .user-role {
        color: rgba(255, 255, 255, 0.5);
        font-size: 12px;
      }
    }
  }

  .menu-scrollbar {
    flex: 1;
    overflow: hidden;

    :deep(.el-scrollbar__wrap) {
      overflow-x: hidden;
    }
  }

  .drawer-menu {
    background: transparent !important;
    border-right: none !important;
    padding: 8px 0;

    .menu-item,
    :deep(.el-sub-menu__title),
    :deep(.el-menu-item) {
      height: 50px;
      line-height: 50px;
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

    .menu-icon {
      font-size: 18px;
      margin-right: 12px;
      color: rgba(255, 255, 255, 0.7);
    }

    .menu-title {
      font-size: 14px;
      font-weight: 500;
    }

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
      }
    }

    :deep(.el-sub-menu .el-menu) {
      background: transparent !important;
      padding: 4px 0;

      .el-menu-item {
        height: 42px;
        line-height: 42px;
        margin: 2px 12px 2px 24px;
        padding: 0 16px !important;
        font-size: 13px;

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
  }

  .drawer-footer {
    padding: 12px 16px;
    display: flex;
    gap: 12px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);

    .footer-item {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      height: 40px;
      border-radius: 8px;
      background: rgba(255, 255, 255, 0.05);
      color: rgba(255, 255, 255, 0.7);
      font-size: 13px;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        color: #fff;
      }

      .el-icon {
        font-size: 16px;
      }
    }
  }
}
</style>
