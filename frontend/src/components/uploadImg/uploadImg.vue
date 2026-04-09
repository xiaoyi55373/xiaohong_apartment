<template>
  <div class="xh-upload">
    <!-- 拖拽上传区域 -->
    <el-upload
      v-bind="$attrs"
      ref="uploadRef"
      :action="UPLOAD_IMG_URL"
      :headers="{ ACCESS_TOKEN: useUserStore().token }"
      :on-preview="handlePictureCardPreview"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-progress="handleProgress"
      :on-remove="handleRemove"
      :on-change="handleChange"
      :before-upload="beforeUpload"
      :drag="drag"
      :list-type="listType"
      :file-list="fileList"
      :class="['xh-upload__container', { 'is-drag': drag, 'is-disabled': disabled }]"
      :disabled="disabled || isUploading"
    >
      <!-- 拖拽上传模式 -->
      <template v-if="drag && fileList.length === 0">
        <div class="xh-upload__drag-area">
          <div class="drag-icon">
            <el-icon :size="48" color="#FF6B6B"><UploadFilled /></el-icon>
          </div>
          <div class="drag-text">
            <span class="primary-text">点击或拖拽文件到此处上传</span>
            <span class="secondary-text">支持 {{ acceptText }} 格式，单张不超过 {{ formatFileSize(maxSize) }}</span>
          </div>
        </div>
      </template>
      
      <!-- 普通上传模式 -->
      <template v-else-if="!drag">
        <el-icon :size="28" color="#FF6B6B"><Plus /></el-icon>
        <div v-if="tip" class="upload-tip">{{ tip }}</div>
      </template>
      
      <!-- 触发按钮（拖拽模式下已有文件时显示） -->
      <template v-if="drag && fileList.length > 0" #trigger>
        <div class="xh-upload__trigger">
          <el-icon :size="24" color="#FF6B6B"><Plus /></el-icon>
          <span>继续添加</span>
        </div>
      </template>
    </el-upload>

    <!-- 上传提示信息 -->
    <div v-if="showTips && !drag" class="xh-upload__tips">
      <el-icon><InfoFilled /></el-icon>
      <span>支持 {{ acceptText }} 格式，单张不超过 {{ formatFileSize(maxSize) }}</span>
    </div>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="800px"
      class="xh-upload__preview-dialog"
      destroy-on-close
    >
      <div class="preview-container">
        <el-image
          :src="previewUrl"
          fit="contain"
          :preview-src-list="previewList"
          :initial-index="previewIndex"
          class="preview-image"
        />
      </div>
      <template #footer>
        <div class="preview-footer">
          <span class="preview-counter">{{ previewIndex + 1 }} / {{ previewList.length }}</span>
          <el-button @click="previewVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 裁剪对话框 -->
    <el-dialog
      v-model="cropVisible"
      title="图片裁剪"
      width="600px"
      class="xh-upload__crop-dialog"
      :close-on-click-modal="false"
    >
      <div class="crop-container">
        <img ref="cropperImageRef" :src="cropImageUrl" class="cropper-image" />
      </div>
      <template #footer>
        <el-button @click="cropVisible = false">取消</el-button>
        <el-button type="primary" :loading="cropLoading" @click="confirmCrop">确认裁剪</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage, UploadFile, UploadFiles, UploadRawFile } from 'element-plus'
import { Plus, UploadFilled, InfoFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { UPLOAD_IMG_URL } from '@/api/upload'
import { formatFileSize, compressImage } from './utils'

// 定义Props
interface Props {
  fileList?: UploadFile[]
  drag?: boolean
  disabled?: boolean
  limit?: number
  maxSize?: number
  accept?: string[]
  tip?: string
  showTips?: boolean
  listType?: 'text' | 'picture' | 'picture-card'
  enableCompress?: boolean
  compressQuality?: number
  enableCrop?: boolean
  cropAspectRatio?: number
}

const props = withDefaults(defineProps<Props>(), {
  fileList: () => [],
  drag: true,
  disabled: false,
  limit: 5,
  maxSize: 10 * 1024 * 1024, // 10MB
  accept: () => ['jpg', 'jpeg', 'png', 'gif', 'webp'],
  tip: '',
  showTips: true,
  listType: 'picture-card',
  enableCompress: true,
  compressQuality: 0.8,
  enableCrop: false,
  cropAspectRatio: 16 / 9,
})

// 定义Emits
const emit = defineEmits<{
  'update:fileList': [files: UploadFile[]]
  success: [response: any, file: UploadFile, files: UploadFiles]
  error: [error: any, file: UploadFile, files: UploadFiles]
  remove: [file: UploadFile, files: UploadFiles]
  exceed: [files: File[]]
}>()

// 组件引用
const uploadRef = ref()
const cropperImageRef = ref<HTMLImageElement>()

// 状态
const isUploading = ref(false)
const uploadProgress = ref(0)
const previewVisible = ref(false)
const previewUrl = ref('')
const previewIndex = ref(0)
const cropVisible = ref(false)
const cropImageUrl = ref('')
const cropLoading = ref(false)
const currentFile = ref<UploadRawFile | null>(null)

// 计算属性
const acceptText = computed(() => props.accept.map(ext => ext.toUpperCase()).join('、'))

const previewList = computed(() => {
  return props.fileList.map(file => file.url || (file.response as any)?.data).filter(Boolean)
})

// 监听文件列表变化
watch(() => props.fileList, (newVal) => {
  // 同步到el-upload
  if (uploadRef.value) {
    uploadRef.value.fileList = newVal
  }
}, { deep: true })

// 上传前处理
async function beforeUpload(rawFile: UploadRawFile) {
  // 检查文件类型
  const ext = rawFile.name.split('.').pop()?.toLowerCase()
  if (!ext || !props.accept.includes(ext)) {
    ElMessage.error(`不支持的文件格式，请上传 ${acceptText.value} 格式的图片`)
    return false
  }

  // 检查文件大小
  if (rawFile.size > props.maxSize) {
    ElMessage.error(`文件大小超过限制，最大支持 ${formatFileSize(props.maxSize)}`)
    return false
  }

  // 图片压缩
  if (props.enableCompress && rawFile.size > 1024 * 1024) {
    try {
      const compressedFile = await compressImage(rawFile, props.compressQuality)
      if (compressedFile.size < rawFile.size) {
        ElMessage.success(`图片已压缩，从 ${formatFileSize(rawFile.size)} 降至 ${formatFileSize(compressedFile.size)}`)
        return compressedFile
      }
    } catch (error) {
      console.error('图片压缩失败:', error)
    }
  }

  isUploading.value = true
  return true
}

// 上传成功
function handleSuccess(response: any, file: UploadFile, files: UploadFiles) {
  isUploading.value = false
  uploadProgress.value = 100
  
  if (response.code !== 200) {
    ElMessage.error(response.message || '上传失败')
    return
  }
  
  // 更新文件URL
  const updatedFile = {
    ...file,
    url: response.data,
  }
  
  const newFileList = files.map(f => f.uid === file.uid ? updatedFile : f)
  emit('update:fileList', newFileList)
  emit('success', response, updatedFile, newFileList)
  
  ElMessage.success('上传成功')
}

// 上传失败
function handleError(error: any, file: UploadFile, files: UploadFiles) {
  isUploading.value = false
  uploadProgress.value = 0
  ElMessage.error('上传失败，请重试')
  emit('error', error, file, files)
}

// 上传进度
function handleProgress(event: any, file: UploadFile, files: UploadFiles) {
  uploadProgress.value = Math.round(event.percent)
}

// 文件变化
function handleChange(file: UploadFile, files: UploadFiles) {
  emit('update:fileList', files)
}

// 删除文件
function handleRemove(file: UploadFile, files: UploadFiles) {
  emit('remove', file, files)
  emit('update:fileList', files)
  ElMessage.success('已删除')
}

// 预览图片
function handlePictureCardPreview(file: UploadFile) {
  const url = file.url || (file.response as any)?.data
  if (!url) return
  
  previewUrl.value = url
  previewIndex.value = previewList.value.indexOf(url)
  previewVisible.value = true
}

// 确认裁剪
async function confirmCrop() {
  cropLoading.value = true
  // 这里可以实现裁剪逻辑
  cropVisible.value = false
  cropLoading.value = false
}

// 暴露方法
defineExpose({
  clearFiles: () => uploadRef.value?.clearFiles(),
  abort: (file: UploadFile) => uploadRef.value?.abort(file),
  submit: () => uploadRef.value?.submit(),
})
</script>

<style scoped lang="scss">
.xh-upload {
  &__container {
    :deep(.el-upload) {
      .el-upload-dragger {
        width: 100%;
        height: 100%;
        border: 2px dashed #E0E0E8;
        border-radius: 12px;
        background: #FAFAFC;
        transition: all 0.3s ease;
        
        &:hover {
          border-color: #FF6B6B;
          background: #FFF5F5;
        }
        
        &.is-dragover {
          border-color: #FF6B6B;
          background: #FFF0F0;
          transform: scale(1.02);
        }
      }
    }
    
    &.is-drag {
      :deep(.el-upload) {
        width: 100%;
      }
    }
    
    &.is-disabled {
      :deep(.el-upload) {
        cursor: not-allowed;
        opacity: 0.6;
      }
    }
  }
  
  &__drag-area {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    text-align: center;
    
    .drag-icon {
      margin-bottom: 16px;
      animation: float 3s ease-in-out infinite;
    }
    
    .drag-text {
      display: flex;
      flex-direction: column;
      gap: 8px;
      
      .primary-text {
        font-size: 16px;
        font-weight: 500;
        color: #2D2D3A;
      }
      
      .secondary-text {
        font-size: 13px;
        color: #8A8AA3;
      }
    }
  }
  
  &__trigger {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 120px;
    height: 120px;
    border: 2px dashed #E0E0E8;
    border-radius: 10px;
    background: #FAFAFC;
    cursor: pointer;
    transition: all 0.3s ease;
    gap: 8px;
    
    span {
      font-size: 12px;
      color: #8A8AA3;
    }
    
    &:hover {
      border-color: #FF6B6B;
      background: #FFF5F5;
    }
  }
  
  &__tips {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-top: 12px;
    font-size: 12px;
    color: #8A8AA3;
    
    .el-icon {
      font-size: 14px;
      color: #FF6B6B;
    }
  }
  
  // 图片列表样式优化
  :deep(.el-upload-list) {
    &.el-upload-list--picture-card {
      .el-upload-list__item {
        width: 120px;
        height: 120px;
        border-radius: 10px;
        border: 1px solid #E8E8EF;
        transition: all 0.3s ease;
        overflow: hidden;
        
        &:hover {
          border-color: #FF8E8E;
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
        }
        
        .el-upload-list__item-actions {
          background: rgba(0, 0, 0, 0.5);
          
          .el-upload-list__item-preview,
          .el-upload-list__item-delete {
            color: #fff;
            
            &:hover {
              color: #FF6B6B;
            }
          }
        }
      }
    }
  }
  
  // 普通上传按钮样式
  :deep(.el-upload--picture-card) {
    width: 120px;
    height: 120px;
    border-radius: 10px;
    border: 2px dashed #E0E0E8;
    background: #FAFAFC;
    transition: all 0.3s ease;
    
    &:hover {
      border-color: #FF6B6B;
      background: #FFF5F5;
      transform: translateY(-2px);
    }
    
    .upload-tip {
      margin-top: 8px;
      font-size: 12px;
      color: #8A8AA3;
    }
  }
  
  // 预览对话框样式
  &__preview-dialog {
    :deep(.el-dialog__body) {
      padding: 20px;
    }
    
    .preview-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 400px;
      background: #F5F5F7;
      border-radius: 8px;
      overflow: hidden;
      
      .preview-image {
        max-width: 100%;
        max-height: 500px;
      }
    }
    
    .preview-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .preview-counter {
        font-size: 14px;
        color: #8A8AA3;
      }
    }
  }
  
  // 裁剪对话框样式
  &__crop-dialog {
    .crop-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 400px;
      background: #F5F5F7;
      border-radius: 8px;
      overflow: hidden;
      
      .cropper-image {
        max-width: 100%;
        max-height: 400px;
      }
    }
  }
}

// 浮动动画
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

// 响应式适配
@media (max-width: 768px) {
  .xh-upload {
    &__drag-area {
      padding: 24px 16px;
      
      .drag-icon {
        .el-icon {
          font-size: 36px;
        }
      }
      
      .drag-text {
        .primary-text {
          font-size: 14px;
        }
        
        .secondary-text {
          font-size: 12px;
        }
      }
    }
    
    :deep(.el-upload-list) {
      &.el-upload-list--picture-card {
        .el-upload-list__item {
          width: 100px;
          height: 100px;
        }
      }
    }
    
    :deep(.el-upload--picture-card) {
      width: 100px;
      height: 100px;
    }
  }
}
</style>
