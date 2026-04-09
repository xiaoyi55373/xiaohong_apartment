<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    :destroy-on-close="true"
    width="520px"
    class="xh-dialog xh-dialog--form"
    align-center
  >
    <div class="xh-form-container">
      <el-form
        ref="ruleFormRef"
        label-width="100px"
        label-suffix=" :"
        :rules="rules"
        :model="postData"
        class="xh-form"
        status-icon
      >
        <el-form-item label="岗位名称" prop="name">
          <el-input
            v-model.trim="postData.name"
            placeholder="请填写岗位名称"
            clearable
            class="xh-input"
          >
            <template #prefix>
              <el-icon><Postcard /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="岗位编码" prop="postCode">
          <el-input
            v-model.trim="postData.postCode"
            placeholder="请填写岗位编码"
            clearable
            class="xh-input"
          >
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="岗位描述" prop="description">
          <el-input
            v-model.trim="postData.description"
            placeholder="请填写岗位描述"
            clearable
            type="textarea"
            class="xh-textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="postData.status" class="xh-radio-group">
            <el-radio :label="1" class="xh-radio xh-radio--card">
              <el-tag size="small" effect="light" type="success">正常</el-tag>
              <span class="radio-desc">该岗位可用</span>
            </el-radio>
            <el-radio :label="0" class="xh-radio xh-radio--card">
              <el-tag size="small" effect="light" type="danger">停用</el-tag>
              <span class="radio-desc">该岗位不可用</span>
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="xh-form-actions xh-form-actions--center">
        <el-button @click="dialogVisible = false" size="large">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
        <el-button
          type="primary"
          @click="handleSubmit(ruleFormRef)"
          :loading="loading"
          size="large"
        >
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, FormInstance } from 'element-plus'
import { Postcard, Key, Check, Close } from '@element-plus/icons-vue'
import { PostInterfacesRes } from '@/api/system/types'

interface DialogProps {
  rowData?: PostInterfacesRes
  api?: (params: any) => Promise<any>
  getTableList?: () => Promise<any>
}

let postData = ref<PostInterfacesRes>({} as PostInterfacesRes)
const rules = reactive({
  name: [{ required: true, message: '请填写岗位名称' }],
  postCode: [{ required: true, message: '请填写岗位编码' }],
  description: [{ required: true, message: '请填写岗位描述' }],
})

const dialogVisible = ref(false)
const title = ref<string>()
const loading = ref<boolean>(false)

const dialogProps = ref<DialogProps>()

const acceptParams = (params: DialogProps): void => {
  const row: any = params.rowData
  postData.value = row
  title.value = getDialogTitle(params)
  dialogProps.value = params
  dialogVisible.value = true
}

const getDialogTitle = (params: DialogProps): string => {
  return params.rowData?.id ? '编辑岗位' : '新增岗位'
}

const ruleFormRef = ref<FormInstance>()

const handleSubmit = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      loading.value = true
      await dialogProps.value?.api!(postData.value)
      ElMessage.success({ message: `${title.value}成功！` })
      dialogProps.value?.getTableList!()
      dialogVisible.value = false
      loading.value = false
      resetForm(formEl)
    } catch (error) {
      loading.value = false
      console.log(error)
    }
  })
}

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}

defineExpose({
  acceptParams,
})
</script>

<style scoped lang="scss">
// 对话框样式
:deep(.xh-dialog) {
  .el-dialog {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 24px 48px rgba(0, 0, 0, 0.15);

    &__header {
      padding: 24px 24px 16px;
      margin-right: 0;
      border-bottom: 1px solid #F0F0F5;
    }

    &__title {
      font-size: 18px;
      font-weight: 600;
      color: #1A1A2E;
    }

    &__headerbtn {
      top: 24px;
      right: 24px;

      .el-dialog__close {
        font-size: 18px;
        color: #8A8AA3;

        &:hover {
          color: #FF6B6B;
        }
      }
    }

    &__body {
      padding: 24px;
    }

    &__footer {
      padding: 16px 24px 24px;
      border-top: 1px solid #F0F0F5;
    }
  }
}

// 单选框卡片样式
.xh-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.xh-radio {
  margin-right: 0;

  &--card {
    display: flex;
    align-items: center;
    gap: 8px;
    height: auto;
    padding: 12px 20px;
    border-radius: 10px;
    background: #FAFAFC;
    border: 1px solid #E8E8EF;
    transition: all 0.3s ease;
    flex: 1;
    min-width: 140px;

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

.radio-desc {
  font-size: 13px;
  color: #8A8AA3;
}

// 响应式适配
@media (max-width: 768px) {
  :deep(.xh-dialog) {
    width: 90% !important;

    .el-dialog {
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
