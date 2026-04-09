<template>
  <el-drawer
    v-model="drawerVisible"
    :destroy-on-close="true"
    size="480px"
    :title="`${drawerProps.title}用户`"
    class="xh-drawer xh-drawer--form"
  >
    <div class="xh-form-container">
      <el-form
        ref="ruleFormRef"
        label-width="100px"
        label-suffix=" :"
        :rules="rules"
        :model="drawerProps.rowData"
        class="xh-form"
        status-icon
      >
        <!-- 基本信息分组 -->
        <div class="xh-form-section">
          <div class="xh-form-section__title">
            <el-icon><User /></el-icon>
            基本信息
          </div>

          <el-form-item label="用户名" prop="username">
            <el-input
              :disabled="drawerProps.title === '分配角色'"
              v-model.trim="drawerProps.rowData!.username"
              placeholder="请填写用户姓名"
              clearable
              class="xh-input"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item
            :label="drawerProps.title === '编辑' ? '修改密码' : '用户密码'"
            prop="password"
            v-if="drawerProps.title === '新增' || drawerProps.title === '编辑'"
          >
            <el-input
              v-model.trim="drawerProps.rowData!.password"
              placeholder="请填写用户密码"
              clearable
              show-password
              class="xh-input"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item
            label="确认密码"
            prop="confirmPassword"
            v-if="drawerProps.title === '编辑'"
          >
            <el-input
              v-model.trim="drawerProps.rowData!.confirmPassword"
              placeholder="请再次填写用户密码"
              clearable
              show-password
              class="xh-input"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <!-- 详细信息分组 -->
        <div class="xh-form-section" v-if="drawerProps.title !== '分配角色'">
          <div class="xh-form-section__title">
            <el-icon><InfoFilled /></el-icon>
            详细信息
          </div>

          <el-form-item label="用户昵称" prop="name">
            <el-input
              v-model.trim="drawerProps.rowData!.name"
              placeholder="请填写用户昵称"
              clearable
              class="xh-input"
            >
              <template #prefix>
                <el-icon><Avatar /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="手机" prop="phone">
            <el-input
              v-model.trim="drawerProps.rowData!.phone"
              placeholder="请填写用户手机"
              clearable
              class="xh-input"
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="岗位" prop="postId">
            <el-select
              v-model="drawerProps.rowData!.postId"
              placeholder="请选择岗位"
              clearable
              class="xh-select"
              style="width: 100%"
            >
              <el-option
                v-for="item in drawerProps.postList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="用户类型" prop="type">
            <el-radio-group v-model="drawerProps.rowData!.type" class="xh-radio-group">
              <el-radio
                :label="item.value"
                v-for="item in SystemUserTypeMap"
                :key="item.value"
                class="xh-radio xh-radio--card"
              >
                <el-tag 
                  size="small" 
                  :type="item.value === '1' ? 'primary' : 'warning'"
                  effect="light"
                >
                  {{ item.label }}
                </el-tag>
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </div>

        <!-- 角色分配分组 -->
        <div class="xh-form-section" v-if="drawerProps.title === '分配角色'">
          <div class="xh-form-section__title">
            <el-icon><UserFilled /></el-icon>
            角色分配
          </div>

          <el-form-item label="角色列表" prop="rolesId">
            <div class="role-selection">
              <el-checkbox
                v-model="state.checkAll"
                :indeterminate="state.isIndeterminate"
                @change="handleCheckAllChange"
                class="role-select-all"
              >
                全选
              </el-checkbox>
              <el-divider />
              <el-checkbox-group
                v-model="state.assginRoleList"
                @change="handleCheckedChange"
                class="role-checkbox-group"
              >
                <el-checkbox
                  v-for="item in state.allRolesList"
                  :key="item.id"
                  :label="item.id"
                  class="role-checkbox"
                >
                  <el-tag size="small" effect="light" type="info">
                    {{ item.name }}
                  </el-tag>
                </el-checkbox>
              </el-checkbox-group>
            </div>
          </el-form-item>
        </div>
      </el-form>
    </div>

    <template #footer>
      <div class="xh-form-actions">
        <el-button @click="drawerVisible = false" size="large">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading" size="large">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import {
  CheckboxValueType,
  ElMessage,
  FormInstance,
  FormRules,
} from 'element-plus'
import {
  User,
  Lock,
  Avatar,
  Phone,
  InfoFilled,
  UserFilled,
  Check,
  Close,
} from '@element-plus/icons-vue'
import { DeptInterfacesRes, PostInterfacesRes, Role } from '@/api/system/types'
import { SystemUserTypeMap } from '@/enums/constEnums'
import { checkUserNameAvailable } from '@/api/system'

const origiinRowData = ref<any>({})

interface DrawerProps {
  title: string
  rowData?: any
  list?: any
  api?: (params: any) => Promise<any>
  getTableList?: () => Promise<any>
  postList?: PostInterfacesRes[]
  deptList?: DeptInterfacesRes[]
}

interface RolesState {
  allRolesList: Role[]
  assginRoleList: string[] | number[]
  checkAll: boolean
  isIndeterminate: boolean
}

const drawerVisible = ref(false)
const drawerProps = ref<DrawerProps>({
  title: '',
})

const loading = ref<boolean>(false)

const checkUserName = async (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入用户名'))
  }
  if (value.length < 4) {
    return callback(new Error('用户名不能小于4位'))
  }
  if (value.length > 20) {
    return callback(new Error('用户名不能大于20位'))
  }
  if (value === origiinRowData.value.username) {
    return callback()
  }
  try {
    const { data } = await checkUserNameAvailable(value)
    if (!data) {
      return callback(new Error('用户名已存在'))
    }
    callback()
  } catch (error) {
    console.error(error)
    callback(new Error('用户名校验失败'))
  }
}

const checkConfirmPassword = async (rule: any, value: any, callback: any) => {
  if (value === drawerProps.value.rowData?.password) {
    return callback()
  } else {
    return callback(new Error('两次输入密码不一致'))
  }
}

const rules = reactive<FormRules>({
  username: [
    {
      required: true,
      validator: checkUserName as unknown as () => void,
      trigger: 'blur',
    },
  ],
  password: [
    {
      required: true,
      message: '请填写用户密码',
      trigger: 'blur',
    },
    { min: 6, message: '密码不能小于6位' },
  ],
  confirmPassword: [
    {
      validator: checkConfirmPassword as unknown as () => void,
      trigger: 'blur',
    },
  ],
  name: [{ required: true, message: '请填写用户昵称', trigger: 'blur' }],
  phone: [{ required: true, message: '请填写用户手机', trigger: 'blur' }],
  postId: [{ required: true, message: '请选择所属岗位', trigger: 'change' }],
  type: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
})

watch(
  drawerProps,
  (newVal) => {
    if (newVal) {
      ;(rules as any).password[0].required = newVal.title === '新增'
    }
  },
  { immediate: true, deep: true },
)

const state: RolesState = reactive({
  allRolesList: [],
  assginRoleList: [],
  checkAll: false,
  isIndeterminate: false,
})

const handleCheckAllChange = (val: CheckboxValueType) => {
  state.assginRoleList = val
    ? state.allRolesList.map((item: Role) => item.id)
    : []
  state.isIndeterminate = false
}

const handleCheckedChange = (value: CheckboxValueType[]) => {
  const checkedCount = value.length
  state.checkAll = checkedCount === state.allRolesList.length
  state.isIndeterminate =
    checkedCount > 0 && checkedCount < state.allRolesList.length
}

const acceptParams = (params: DrawerProps): void => {
  origiinRowData.value = JSON.parse(JSON.stringify(params.rowData))
  if (params.title === '编辑') {
    params.rowData.password = ''
    params.rowData.confirmPassword = ''
  }
  if (params.title === '分配角色') {
    const { list } = params
    state.allRolesList = list.data
    state.assginRoleList = list?.data
      .filter((item: Role) => item.selected)
      .map((item: Role) => item.id)
    state.isIndeterminate = state.assginRoleList.length > 0 ? true : false
  }
  drawerProps.value = params
  drawerVisible.value = true
}

const ruleFormRef = ref<FormInstance>()
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    if (!valid) return
    try {
      loading.value = true
      if (drawerProps.value.title === '分配角色') {
        const params = {
          userId: drawerProps.value.rowData.id,
          roleIdList: state.assginRoleList,
        }
        await drawerProps.value.api!(params)
      } else {
        await drawerProps.value.api!(drawerProps.value.rowData)
      }
      ElMessage.success({ message: `${drawerProps.value.title}用户成功！` })
      drawerProps.value.getTableList!()
      drawerVisible.value = false
      loading.value = false
    } catch (error) {
      loading.value = false
      console.log(error)
    }
  })
}

defineExpose({
  acceptParams,
})
</script>

<style scoped lang="scss">
// 抽屉样式
:deep(.xh-drawer) {
  .el-drawer {
    &__header {
      padding: 24px 24px 16px;
      margin-bottom: 0;
      border-bottom: 1px solid #F0F0F5;

      span {
        font-size: 18px;
        font-weight: 600;
        color: #1A1A2E;
      }
    }

    &__body {
      padding: 24px;
      overflow-y: auto;
    }

    &__footer {
      padding: 16px 24px 24px;
      border-top: 1px solid #F0F0F5;
    }
  }
}

// 表单分组
.xh-form-section {
  margin-bottom: 32px;

  &:last-child {
    margin-bottom: 0;
  }

  &__title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 600;
    color: #1A1A2E;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #F0F0F5;

    .el-icon {
      font-size: 18px;
      color: #FF6B6B;
    }
  }
}

// 单选框卡片样式
.xh-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.xh-radio {
  margin-right: 0;

  &--card {
    display: flex;
    align-items: center;
    gap: 8px;
    height: auto;
    padding: 10px 16px;
    border-radius: 8px;
    background: #FAFAFC;
    border: 1px solid #E8E8EF;
    transition: all 0.3s ease;

    :deep(.el-radio__input) {
      margin-right: 4px;
    }

    :deep(.el-radio__label) {
      display: flex;
      align-items: center;
      gap: 8px;
      padding-left: 0;
    }

    &:hover {
      border-color: #FF8E8E;
      background: #FFF5F5;
    }

    &.is-checked {
      border-color: #FF6B6B;
      background: #FFF0F0;
    }
  }
}

// 角色选择区域
.role-selection {
  background: #FAFAFC;
  border-radius: 10px;
  padding: 16px;
  border: 1px solid #E8E8EF;
}

.role-select-all {
  margin-bottom: 8px;

  :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
    color: #FF6B6B;
    font-weight: 500;
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
    border-color: #FF6B6B;
  }
}

.role-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.role-checkbox {
  margin-right: 0;

  :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
    color: #FF6B6B;
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
    border-color: #FF6B6B;
  }
}

// 响应式适配
@media (max-width: 768px) {
  :deep(.xh-drawer) {
    .el-drawer {
      width: 100% !important;

      &__header {
        padding: 20px 20px 12px;
      }

      &__body {
        padding: 20px;
      }

      &__footer {
        padding: 12px 20px 20px;
      }
    }
  }

  .xh-radio-group {
    flex-direction: column;
  }

  .xh-radio--card {
    width: 100%;
  }
}
</style>
