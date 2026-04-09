<template>
  <div class="logo-container" :class="{ 'is-collapse': collapse }">
    <a href="/" class="logo-link">
      <div class="logo-wrapper">
        <img class="logo" alt="logo" :src="logoPng" />
      </div>
      <transition name="logo-fade">
        <h1 v-if="!collapse" class="title">
          {{ getEnvByName('VITE_APP_TITLE') }}
        </h1>
      </transition>
    </a>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, ref } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { getEnvByName } from '@/utils/getEnv'
import logoPngUrl from '@/assets/images/logo.png'

export default defineComponent({
  name: 'Logo',
  methods: { getEnvByName },
  setup() {
    const settingsStore = useSettingsStore()
    const collapse = computed(() => settingsStore.collapse)
    const logoPng = ref(logoPngUrl)
    return { logoPng, collapse }
  },
})
</script>

<style scoped lang="scss">
.logo-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 70px;
  overflow: hidden;
  background: transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);

  &.is-collapse {
    .logo-link {
      justify-content: center;
      padding: 0;
    }
    
    .logo-wrapper {
      width: 40px;
      height: 40px;
    }
  }
}

.logo-link {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  height: 100%;
  padding: 0 20px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF9F43 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 16px rgba(255, 107, 107, 0.4);
  }
}

.logo {
  width: 28px;
  height: 28px;
  object-fit: contain;
}

.title {
  margin: 0 0 0 14px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  white-space: nowrap;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Logo文字淡入淡出动画 */
.logo-fade-enter-active,
.logo-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo-fade-enter-from,
.logo-fade-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}
</style>
