<!--
  @Description: 顶部导航栏组件 - 小红公寓claw品牌定制
  @Author: 小红
-->
<template>
  <div class="navbar-container">
    <div class="navbar-left">
      <div class="hamburger-wrapper" @click="toggleSidebar">
        <el-icon :size="20" class="hamburger-icon">
          <Fold v-if="!sidebar.opened" />
          <Expand v-else />
        </el-icon>
      </div>
      <breadcrumb class="breadcrumb-container" />
    </div>

    <div class="navbar-right">
      <!-- 搜索 -->
      <div class="navbar-item search-box" v-if="showSearch">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索菜单..."
          :prefix-icon="Search"
          size="small"
          clearable
          @keyup.enter="handleSearch"
        />
      </div>

      <!-- 全屏 -->
      <div class="navbar-item" @click="toggleFullScreen">
        <el-tooltip content="全屏" placement="bottom">
          <el-icon :size="18">
            <FullScreen v-if="!isFullscreen" />
            <Aim v-else />
          </el-icon>
        </el-tooltip>
      </div>

      <!-- 主题切换 -->
      <div class="navbar-item" @click="toggleTheme">
        <el-tooltip :content="isDark ? '切换亮色' : '切换暗色'" placement="bottom">
          <el-icon :size="18">
            <Sunny v-if="isDark" />
            <Moon v-else />
          </el-icon>
        </el-tooltip>
      </div>

      <!-- 通知 -->
      <div class="navbar-item notification-wrapper">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
          <el-tooltip content="通知消息" placement="bottom">
            <el-icon :size="18" @click="showNotification">
              <Bell />
            </el-icon>
          </el-tooltip>
        </el-badge>
      </div>

      <!-- 用户菜单 -->
      <el-dropdown class="navbar-item user-dropdown" trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" :src="avatar" class="user-avatar">
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
          <span class="user-name">{{ username }}</span>
          <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="user-dropdown-menu">
            <div class="dropdown-header">
              <span class="dropdown-title">小红公寓管理系统</span>
              <span class="dropdown-subtitle">{{ username }}</span>
            </div>
            <el-dropdown-item command="profile" divided>
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon>
              <span class="logout-text">退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 通知抽屉 -->
    <el-drawer
      v-model="notificationVisible"
      title="通知消息"
      size="360px"
      :with-header="true"
      class="notification-drawer"
    >
      <div class="notification-list">
        <div v-for="(item, index) in notifications" :key="index" class="notification-item" :class="{ unread: !item.read }">
          <div class="notification-icon" :class="item.type">
            <el-icon :size="20">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="notification-content">
            <div class="notification-title">{{ item.title }}</div>
            <div class="notification-desc">{{ item.desc }}</div>
            <div class="notification-time">{{ item.time }}</div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSettingsStore } from '@/store/modules/settings'
import { useUserStore } from '@/store/modules/user'
import Breadcrumb from './Breadcrumb.vue'
import screenfull from 'screenfull'

export default defineComponent({
  name: 'NavBar',
  components: {
    Breadcrumb
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const settingsStore = useSettingsStore()
    const userStore = useUserStore()

    const sidebar = computed(() => ({
      opened: !settingsStore.collapse
    }))

    const username = computed(() => userStore.username || '管理员')
    const avatar = computed(() => userStore.avatar || '')
    const isDark = computed(() => settingsStore.theme === 'dark')

    const showSearch = ref(true)
    const searchKeyword = ref('')
    const isFullscreen = ref(false)
    const unreadCount = ref(3)
    const notificationVisible = ref(false)

    const notifications = ref([
      { title: '系统通知', desc: '小红公寓管理系统已更新至v2.0', time: '10分钟前', read: false, type: 'info', icon: 'InfoFilled' },
      { title: '预约提醒', desc: '您有一条新的看房预约待处理', time: '1小时前', read: false, type: 'warning', icon: 'WarningFilled' },
      { title: '租约到期', desc: 'A栋301室租约即将到期', time: '2小时前', read: false, type: 'success', icon: 'SuccessFilled' },
      { title: '欢迎', desc: '欢迎使用小红公寓管理系统', time: '1天前', read: true, type: 'primary', icon: 'StarFilled' }
    ])

    const toggleSidebar = () => {
      settingsStore.changeCollapse()
    }

    const handleSearch = () => {
      console.log('搜索:', searchKeyword.value)
    }

    const toggleFullScreen = () => {
      if (screenfull.isEnabled) {
        screenfull.toggle()
        isFullscreen.value = !isFullscreen.value
      }
    }

    const toggleTheme = () => {
      settingsStore.changeTheme(isDark.value ? 'light' : 'dark')
    }

    const showNotification = () => {
      notificationVisible.value = true
    }

    const handleCommand = (command: string) => {
      switch (command) {
        case 'profile':
          router.push('/profile')
          break
        case 'settings':
          router.push('/settings')
          break
        case 'logout':
          userStore.logout().then(() => {
            router.push('/login')
          })
          break
      }
    }

    return {
      sidebar,
      username,
      avatar,
      isDark,
      showSearch,
      searchKeyword,
      isFullscreen,
      unreadCount,
      notificationVisible,
      notifications,
      toggleSidebar,
      handleSearch,
      toggleFullScreen,
      toggleTheme,
      showNotification,
      handleCommand
    }
  }
})
</script>

<style scoped lang="scss">
.navbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $base-nav-bar-height;
  padding: 0 20px;
  background: #FFFFFF;
  box-shadow: 0 1px 4px rgba(26, 26, 46, 0.06);
  position: relative;
  z-index: 100;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 16px;

  .hamburger-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.25s ease;

    &:hover {
      background: rgba(255, 107, 107, 0.1);

      .hamburger-icon {
        color: #FF6B6B;
      }
    }

    .hamburger-icon {
      color: #4A4A68;
      transition: color 0.25s ease;
    }
  }

  .breadcrumb-container {
    font-size: 14px;
  }
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.navbar-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  cursor: pointer;
  color: #4A4A68;
  transition: all 0.25s ease;

  &:hover {
    background: rgba(255, 107, 107, 0.1);
    color: #FF6B6B;
  }

  &.search-box {
    width: 200px;
    padding: 0 8px;

    :deep(.el-input__wrapper) {
      border-radius: 18px;
      box-shadow: 0 0 0 1px #E8E8EF inset;

      &:hover, &.is-focus {
        box-shadow: 0 0 0 1px #FF6B6B inset;
      }
    }
  }

  &.notification-wrapper {
    position: relative;

    :deep(.notification-badge) {
      .el-badge__content {
        background: #FF6B6B;
        border: none;
        font-size: 10px;
        height: 16px;
        line-height: 16px;
        padding: 0 5px;
      }
    }
  }

  &.user-dropdown {
    width: auto;
    padding: 0 8px;
    margin-left: 8px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .user-avatar {
        border: 2px solid rgba(255, 107, 107, 0.2);
        transition: all 0.25s ease;

        &:hover {
          border-color: #FF6B6B;
        }
      }

      .user-name {
        font-size: 14px;
        font-weight: 500;
        color: #1A1A2E;
        max-width: 100px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .dropdown-icon {
        font-size: 12px;
        color: #8A8AA3;
        transition: transform 0.25s ease;
      }
    }

    &:hover {
      background: rgba(255, 107, 107, 0.05);

      .dropdown-icon {
        transform: rotate(180deg);
      }
    }
  }
}

// 下拉菜单样式
:deep(.user-dropdown-menu) {
  padding: 8px;
  min-width: 180px;

  .dropdown-header {
    padding: 12px 16px;
    margin-bottom: 8px;
    border-bottom: 1px solid #E8E8EF;

    .dropdown-title {
      display: block;
      font-size: 14px;
      font-weight: 600;
      color: #1A1A2E;
      margin-bottom: 4px;
    }

    .dropdown-subtitle {
      display: block;
      font-size: 12px;
      color: #8A8AA3;
    }
  }

  .el-dropdown-menu__item {
    padding: 10px 16px;
    border-radius: 6px;
    margin: 2px 0;
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 14px;
    color: #4A4A68;
    transition: all 0.2s ease;

    &:hover {
      background: rgba(255, 107, 107, 0.1);
      color: #FF6B6B;
    }

    .el-icon {
      font-size: 16px;
    }

    .logout-text {
      color: #FF6B6B;
    }
  }
}

// 通知抽屉样式
:deep(.notification-drawer) {
  .el-drawer__header {
    padding: 20px;
    margin-bottom: 0;
    border-bottom: 1px solid #E8E8EF;

    .el-drawer__title {
      font-size: 18px;
      font-weight: 600;
      color: #1A1A2E;
    }
  }

  .notification-list {
    padding: 12px;
  }

  .notification-item {
    display: flex;
    gap: 12px;
    padding: 16px;
    border-radius: 12px;
    margin-bottom: 8px;
    transition: all 0.25s ease;
    cursor: pointer;

    &:hover {
      background: #F5F5FA;
    }

    &.unread {
      background: rgba(255, 107, 107, 0.05);

      .notification-title::after {
        content: '';
        display: inline-block;
        width: 6px;
        height: 6px;
        background: #FF6B6B;
        border-radius: 50%;
        margin-left: 8px;
        vertical-align: middle;
      }
    }

    .notification-icon {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      &.primary {
        background: rgba(255, 107, 107, 0.1);
        color: #FF6B6B;
      }

      &.success {
        background: rgba(29, 209, 161, 0.1);
        color: #1DD1A1;
      }

      &.warning {
        background: rgba(255, 159, 67, 0.1);
        color: #FF9F43;
      }

      &.info {
        background: rgba(84, 160, 255, 0.1);
        color: #54A0FF;
      }
    }

    .notification-content {
      flex: 1;
      min-width: 0;

      .notification-title {
        font-size: 14px;
        font-weight: 500;
        color: #1A1A2E;
        margin-bottom: 4px;
      }

      .notification-desc {
        font-size: 13px;
        color: #8A8AA3;
        margin-bottom: 6px;
        line-height: 1.5;
      }

      .notification-time {
        font-size: 12px;
        color: #B8B8CC;
      }
    }
  }
}
</style>
