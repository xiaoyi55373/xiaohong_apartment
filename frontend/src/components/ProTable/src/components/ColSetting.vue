<template>
  <!-- 列设置 -->
  <el-drawer
    v-model="drawerVisible"
    title="列设置"
    size="420px"
    :destroy-on-close="false"
    class="col-setting-drawer"
  >
    <div class="col-setting-content">
      <!-- 顶部提示 -->
      <div class="setting-tips">
        <el-icon class="tips-icon"><InfoFilled /></el-icon>
        <span>拖拽可调整列顺序，开关控制显示/隐藏</span>
      </div>

      <!-- 列设置表格 -->
      <div class="setting-table-wrapper">
        <el-table
          :data="colSetting"
          :border="true"
          row-key="prop"
          default-expand-all
          :tree-props="{ children: '_children' }"
          class="setting-table"
          size="small"
        >
          <el-table-column prop="label" label="列名" min-width="140">
            <template #default="scope">
              <div class="col-name">
                <el-icon class="drag-icon"><Rank /></el-icon>
                <span>{{ scope.row.label }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="isShow" label="显示" width="80" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.isShow"
                size="small"
                :active-color="'#FF6B6B'"
              />
            </template>
          </el-table-column>
          <el-table-column prop="sortable" label="排序" width="80" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.sortable"
                size="small"
                :active-color="'#FF6B6B'"
              />
            </template>
          </el-table-column>
          <template #empty>
            <div class="table-empty">
              <el-empty description="暂无可配置列" :image-size="80">
                <template #image>
                  <div class="empty-icon">
                    <svg viewBox="0 0 1024 1024" width="80" height="80">
                      <path
                        fill="#FFE0E0"
                        d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z"
                      />
                      <path
                        fill="#FF6B6B"
                        d="M512 832c-176.7 0-320-143.3-320-320S335.3 192 512 192s320 143.3 320 320-143.3 320-320 320z m0-576c-141.4 0-256 114.6-256 256s114.6 256 256 256 256-114.6 256-256-114.6-256-256-256z"
                        opacity="0.3"
                      />
                      <path
                        fill="#FF6B6B"
                        d="M512 640c-17.7 0-32-14.3-32-32V416c0-17.7 14.3-32 32-32s32 14.3 32 32v192c0 17.7-14.3 32-32 32z"
                      />
                      <circle fill="#FF6B6B" cx="512" cy="352" r="32" />
                    </svg>
                  </div>
                </template>
              </el-empty>
            </div>
          </template>
        </el-table>
      </div>

      <!-- 底部操作按钮 -->
      <div class="setting-footer">
        <el-button @click="handleReset" size="default">
          <el-icon><RefreshLeft /></el-icon>
          重置
        </el-button>
        <el-button type="primary" @click="handleConfirm" size="default">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts" name="ColSetting">
import { ref } from 'vue'
import { ColumnProps } from '../types'
import { InfoFilled, Rank, RefreshLeft, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps<{ colSetting: ColumnProps[] }>()

const drawerVisible = ref<boolean>(false)
const originalSetting = ref<ColumnProps[]>([])

// 打开列设置
const openColSetting = () => {
  // 保存原始设置，用于重置
  originalSetting.value = JSON.parse(JSON.stringify(props.colSetting))
  drawerVisible.value = true
}

// 重置
const handleReset = () => {
  props.colSetting.forEach((col, index) => {
    if (originalSetting.value[index]) {
      col.isShow = originalSetting.value[index].isShow
      col.sortable = originalSetting.value[index].sortable
    }
  })
  ElMessage.success('已重置为默认设置')
}

// 确定
const handleConfirm = () => {
  drawerVisible.value = false
  ElMessage.success('列设置已保存')
}

defineExpose({
  openColSetting,
})
</script>

<style scoped lang="scss">
.col-setting-content {
  display: flex;
  flex-direction: column;
  height: 100%;

  .setting-tips {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background: linear-gradient(135deg, #FFF0F0 0%, #FFE8E8 100%);
    border-radius: 10px;
    margin-bottom: 16px;
    border: 1px solid rgba(255, 107, 107, 0.15);

    .tips-icon {
      color: #FF6B6B;
      font-size: 16px;
    }

    span {
      font-size: 13px;
      color: #4A4A68;
    }
  }

  .setting-table-wrapper {
    flex: 1;
    overflow: auto;

    .setting-table {
      border-radius: 12px;
      overflow: hidden;

      :deep(.el-table__header) {
        th.el-table__cell {
          background: linear-gradient(180deg, #fafafc 0%, #f5f5fa 100%);
          color: #1A1A2E;
          font-weight: 600;
          font-size: 13px;
          padding: 12px;
          border-bottom: 2px solid #E8E8EF;
        }
      }

      :deep(.el-table__body) {
        tr.el-table__row {
          td.el-table__cell {
            padding: 10px 12px;
            border-bottom: 1px solid rgba(232, 232, 239, 0.5);
          }

          &:hover {
            td.el-table__cell {
              background: rgba(255, 107, 107, 0.04);
            }
          }
        }
      }

      .col-name {
        display: flex;
        align-items: center;
        gap: 8px;

        .drag-icon {
          color: #8A8AA3;
          font-size: 14px;
          cursor: move;
          transition: all 0.2s ease;

          &:hover {
            color: #FF6B6B;
          }
        }

        span {
          font-size: 13px;
          color: #4A4A68;
        }
      }

      :deep(.el-switch) {
        &.is-checked {
          .el-switch__core {
            background: #FF6B6B;
            border-color: #FF6B6B;
          }
        }

        .el-switch__core {
          border-radius: 10px;
        }
      }
    }
  }

  .setting-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 16px;
    border-top: 1px solid #E8E8EF;
    margin-top: 16px;

    .el-button {
      border-radius: 8px;
      padding: 10px 20px;
      font-weight: 500;
      transition: all 0.25s ease;

      .el-icon {
        margin-right: 4px;
      }

      &.el-button--primary {
        background: linear-gradient(135deg, #FF6B6B 0%, #E55A5A 100%);
        border: none;
        box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
        }
      }

      &:not(.el-button--primary):hover {
        border-color: #FF6B6B;
        color: #FF6B6B;
        background: #FFF0F0;
      }
    }
  }

  .table-empty {
    padding: 40px 20px;

    .empty-icon {
      display: flex;
      justify-content: center;
    }
  }
}
</style>
