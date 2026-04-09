<template>
  <div class="login-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="circle circle-4"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo-wrapper">
            <img src="@/assets/logo/logo-white.svg" alt="logo" class="brand-logo" />
          </div>
          <h1 class="brand-title">小红公寓 claw</h1>
          <p class="brand-slogan">让租房更简单</p>
          <div class="brand-features">
            <div class="feature-item">
              <el-icon class="feature-icon"><House /></el-icon>
              <span>优质房源</span>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Lock /></el-icon>
              <span>安全可靠</span>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Service /></el-icon>
              <span>贴心服务</span>
            </div>
          </div>
        </div>
        <!-- 装饰图形 -->
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="form-section">
        <div class="form-header">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请登录您的管理后台账号</p>
        </div>

        <el-form
          ref="ruleFormRef"
          :model="ruleForm"
          status-icon
          :rules="rules"
          class="login-form"
          @keyup.enter="submitForm(ruleFormRef)"
        >
          <el-form-item prop="username">
            <div class="input-wrapper">
              <el-icon class="input-icon"><User /></el-icon>
              <el-input
                v-model.trim="ruleForm.username"
                autocomplete="off"
                placeholder="请输入用户名"
                class="custom-input"
              />
            </div>
          </el-form-item>

          <el-form-item prop="password">
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <el-input
                v-model.trim="ruleForm.password"
                type="password"
                show-password
                autocomplete="off"
                placeholder="请输入密码"
                class="custom-input"
              />
            </div>
          </el-form-item>

          <el-form-item prop="captchaCode">
            <div class="captcha-wrapper">
              <div class="input-wrapper captcha-input">
                <el-icon class="input-icon"><CircleCheck /></el-icon>
                <el-input
                  v-model.trim="ruleForm.captchaCode"
                  autocomplete="off"
                  placeholder="请输入验证码"
                  class="custom-input"
                />
              </div>
              <div class="captcha-image" @click="getCaptcha">
                <el-image
                  fit="contain"
                  :src="captcha.image"
                  class="pointer"
                >
                  <template #error>
                    <div class="captcha-placeholder">点击刷新</div>
                  </template>
                </el-image>
              </div>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              class="login-btn"
              type="primary"
              size="large"
              :loading="loading"
              @click="submitForm(ruleFormRef)"
            >
              <span class="btn-text">登 录</span>
              <el-icon class="btn-icon"><ArrowRight /></el-icon>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <p>© {{ currentYear }} 小红公寓claw 版权所有</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import type { FormInstance } from 'element-plus'
import { User, Lock, CircleCheck, ArrowRight, House, Service } from '@element-plus/icons-vue'
import { HOME_URL } from '@/config/config'
import { timeFix } from '@/utils/index'
import { getCode, getUserInfo, login } from '@/api/user'
import { ElNotification, ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const ruleFormRef = ref<FormInstance>()
const userStore = useUserStore()
const loading = ref(false)
const currentYear = new Date().getFullYear()

const ruleForm = reactive({
  username: 'user',
  password: '123456',
  captchaKey: '',
  captchaCode: '',
})

const validateUsername = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('用户名不能为空'))
  } else if (value.length < 4) {
    callback(new Error('用户名长度不能小于4位'))
  } else {
    callback()
  }
}

const validatePassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('密码不能为空'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能小于6位'))
  } else {
    callback()
  }
}

const validateCaptchaCode = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('验证码不能为空'))
  } else {
    callback()
  }
}

const rules = reactive({
  username: [{ required: true, validator: validateUsername }],
  password: [{ required: true, validator: validatePassword }],
  captchaCode: [{ required: true, validator: validateCaptchaCode }],
})

const captcha = ref({
  image: '',
  key: '',
})

const getCaptcha = async () => {
  try {
    const { data } = await getCode()
    captcha.value = data
    ruleForm.captchaKey = data.key
  } catch (error) {
    console.log(error)
  }
}

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      loading.value = true
      const { data } = await login(ruleForm)
      userStore.setToken(data)
      router.replace((route.query.redirect as string) || HOME_URL)

      const userInfo = await getUserInfo()
      userStore.setUserInfo(userInfo.data)

      ElNotification({
        title: `hi,${timeFix()}!`,
        message: `欢迎回来`,
        type: 'success',
      })
    } catch (error: any) {
      console.error('登录失败详情：', error)
      ElMessage.error(error?.response?.data?.message || '登录失败，请重试')
      // 登录失败后刷新验证码
      getCaptcha()
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  getCaptcha()
})
</script>

<style scoped lang="scss">
@import './index';
</style>
