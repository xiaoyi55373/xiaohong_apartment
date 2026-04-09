<template>
  <!-- 分页组件 -->
  <div class="pagination-wrapper">
    <div class="pagination-info" v-if="showTotalInfo">
      <span class="info-text">
        共 <strong>{{ pageable.total }}</strong> 条记录
      </span>
      <span class="info-text" v-if="pageable.total > 0">
        第 <strong>{{ pageable.pageNum }}</strong> / {{ totalPages }} 页
      </span>
    </div>
    <el-pagination
      class="pagination"
      :current-page="pageable.pageNum"
      :page-size="pageable.pageSize"
      :page-sizes="pageSizes"
      :background="true"
      :layout="layout"
      :total="pageable.total"
      :pager-count="pagerCount"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts" name="Pagination">
interface Pageable {
  pageNum: number
  pageSize: number
  total: number
}

interface PaginationProps {
  pageable: Pageable
  handleSizeChange: (size: number) => void
  handleCurrentChange: (currentPage: number) => void
  pageSizes?: number[]
  layout?: string
  pagerCount?: number
  showTotalInfo?: boolean
}

const props = withDefaults(defineProps<PaginationProps>(), {
  pageSizes: () => [10, 25, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  pagerCount: 7,
  showTotalInfo: true,
})

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(props.pageable.total / props.pageable.pageSize)
})
</script>

<script lang="ts">
import { computed } from 'vue'
export default {
  name: 'Pagination',
}
</script>

<style lang="scss" scoped>
.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(232, 232, 239, 0.6);

  .pagination-info {
    display: flex;
    align-items: center;
    gap: 16px;

    .info-text {
      font-size: 13px;
      color: #8A8AA3;

      strong {
        color: #FF6B6B;
        font-weight: 600;
      }
    }
  }

  .pagination {
    :deep(.el-pagination) {
      display: flex;
      align-items: center;
      gap: 8px;

      .el-pagination__total,
      .el-pagination__sizes {
        color: #8A8AA3;
        font-size: 13px;
        font-weight: 400;
      }

      .el-pagination__sizes {
        .el-select {
          .el-input {
            .el-input__inner {
              border-radius: 8px;
              border-color: #E8E8EF;
              height: 32px;
              font-size: 13px;
              transition: all 0.25s ease;

              &:hover {
                border-color: #FF6B6B;
              }

              &:focus {
                border-color: #FF6B6B;
                box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
              }
            }
          }
        }
      }

      .el-pager {
        display: flex;
        align-items: center;
        gap: 6px;

        li {
          border-radius: 8px;
          min-width: 32px;
          height: 32px;
          line-height: 32px;
          font-size: 13px;
          font-weight: 500;
          color: #4A4A68;
          background: #fff;
          border: 1px solid #E8E8EF;
          transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

          &:hover {
            color: #FF6B6B;
            border-color: #FF6B6B;
            background: #FFF0F0;
            transform: translateY(-1px);
          }

          &.is-active {
            background: linear-gradient(135deg, #FF6B6B 0%, #E55A5A 100%);
            border-color: #FF6B6B;
            color: #fff;
            box-shadow: 0 4px 12px rgba(255, 107, 107, 0.35);
            transform: translateY(-1px);
          }

          &.more {
            border-color: transparent;
            background: transparent;

            &:hover {
              color: #FF6B6B;
              background: #FFF0F0;
              border-color: #FF6B6B;
            }
          }
        }
      }

      .btn-prev,
      .btn-next {
        border-radius: 8px;
        border: 1px solid #E8E8EF;
        background: #fff;
        min-width: 32px;
        height: 32px;
        padding: 0 8px;
        transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

        .el-icon {
          font-size: 14px;
          color: #4A4A68;
        }

        &:hover:not(:disabled) {
          color: #FF6B6B;
          border-color: #FF6B6B;
          background: #FFF0F0;
          transform: translateY(-1px);

          .el-icon {
            color: #FF6B6B;
          }
        }

        &:disabled {
          opacity: 0.4;
          cursor: not-allowed;
        }
      }

      .el-pagination__jump {
        color: #8A8AA3;
        font-size: 13px;
        font-weight: 400;
        margin-left: 8px;

        .el-input {
          width: 48px;
          margin: 0 4px;

          .el-input__inner {
            border-radius: 8px;
            border-color: #E8E8EF;
            height: 32px;
            text-align: center;
            font-size: 13px;
            font-weight: 500;
            transition: all 0.25s ease;

            &:hover {
              border-color: #FF6B6B;
            }

            &:focus {
              border-color: #FF6B6B;
              box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
            }
          }
        }
      }
    }
  }
}

// 响应式适配
@media screen and (max-width: 768px) {
  .pagination-wrapper {
    flex-direction: column;
    align-items: center;
    gap: 12px;

    .pagination-info {
      order: 2;
    }

    .pagination {
      order: 1;

      :deep(.el-pagination) {
        .el-pagination__sizes,
        .el-pagination__jump {
          display: none;
        }

        .el-pager {
          li {
            min-width: 28px;
            height: 28px;
            line-height: 28px;
            font-size: 12px;
          }
        }

        .btn-prev,
        .btn-next {
          min-width: 28px;
          height: 28px;
        }
      }
    }
  }
}
</style>
