<template>
  <div class="mobile-nav-bar" v-if="isMobile">
    <div
      v-for="item in navItems"
      :key="item.path"
      class="nav-item"
      :class="{ 'is-active': activePath === item.path, 'nav-item-center': item.isCenter }"
      @click="handleNavClick(item)"
    >
      <div v-if="item.isCenter" class="center-btn">
        <el-icon class="nav-icon">
          <component :is="item.icon" />
        </el-icon>
      </div>
      <template v-else>
        <el-icon class="nav-icon">
          <component :is="item.icon" />
        </el-icon>
        <span class="nav-text">{{ item.title }}</span>
      </template>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useResponsive } from '@/hooks/useResponsive'
import {
  HomeFilled,
  OfficeBuilding,
  Plus,
  Calendar,
  UserFilled,
} from '@element-plus/icons-vue'

interface NavItem {
  title: string
  icon: string
  path: string
  isCenter?: boolean
}

export default defineComponent({
  name: 'MobileNavBar',
  components: {
    HomeFilled,
    OfficeBuilding,
    Plus,
    Calendar,
    UserFilled,
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const { isMobile } = useResponsive()

    const navItems: NavItem[] = [
      {
        title: '首页',
        icon: 'HomeFilled',
        path: '/index',
      },
      {
        title: '公寓',
        icon: 'OfficeBuilding',
        path: '/apartmentManagement/apartmentManagement/apartmentManagement',
      },
      {
        title: '新增',
        icon: 'Plus',
        path: '/apartmentManagement/apartmentManagement/apartmentManagement',
        isCenter: true,
      },
      {
        title: '预约',
        icon: 'Calendar',
        path: '/rentManagement/appointment/appointment',
      },
      {
        title: '我的',
        icon: 'UserFilled',
        path: '/userManagement/userManagement',
      },
    ]

    const activePath = computed(() => {
      const path = route.path
      // 匹配最相近的路径
      const matched = navItems.find(item => path.startsWith(item.path) && item.path !== '/index')
      if (matched) return matched.path
      if (path === '/index') return '/index'
      return ''
    })

    const handleNavClick = (item: NavItem) => {
      if (item.path) {
        router.push(item.path)
      }
    }

    return {
      isMobile,
      navItems,
      activePath,
      handleNavClick,
    }
  },
})
</script>

<style scoped lang="scss">
.mobile-nav-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
  height: 60px;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding-bottom: env(safe-area-inset-bottom, 0);

  .nav-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #999;
    transition: all 0.2s ease;
    cursor: pointer;

    .nav-icon {
      font-size: 22px;
      margin-bottom: 2px;
      transition: all 0.2s ease;
    }

    .nav-text {
      font-size: 11px;
    }

    &.is-active {
      color: #FF6B6B;

      .nav-icon {
        transform: scale(1.1);
      }
    }

    &:active {
      opacity: 0.7;
    }

    &.nav-item-center {
      position: relative;
      margin-top: -20px;

      .center-btn {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);

        .nav-icon {
          font-size: 24px;
          color: #fff;
          margin-bottom: 0;
        }
      }
    }
  }
}
</style>
