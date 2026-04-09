<template>
  <el-card class="chart-card" :shadow="shadow" :class="cardClass">
    <template #header v-if="showHeader">
      <div class="chart-header">
        <div class="chart-title">
          <el-icon v-if="icon" :size="20" class="title-icon">
            <component :is="icon" />
          </el-icon>
          <span class="title-text">{{ title }}</span>
          <el-tag v-if="tag" :type="tagType" size="small" class="title-tag">
            {{ tag }}
          </el-tag>
        </div>
        <div class="chart-actions">
          <slot name="actions">
            <el-radio-group v-if="timeRanges" v-model="selectedRange" size="small">
              <el-radio-button 
                v-for="range in timeRanges" 
                :key="range.value" 
                :label="range.value"
              >
                {{ range.label }}
              </el-radio-button>
            </el-radio-group>
          </slot>
        </div>
      </div>
    </template>
    <div class="chart-body" :style="{ height: bodyHeight }">
      <slot />
    </div>
    <div v-if="showFooter" class="chart-footer">
      <slot name="footer" />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface TimeRange {
  label: string
  value: string
}

interface Props {
  title?: string
  icon?: string
  tag?: string
  tagType?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  shadow?: 'always' | 'never' | 'hover'
  showHeader?: boolean
  showFooter?: boolean
  bodyHeight?: string
  timeRanges?: TimeRange[]
  modelValue?: string
  loading?: boolean
  cardClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  shadow: 'hover',
  showHeader: true,
  showFooter: false,
  bodyHeight: '320px',
  tagType: 'primary',
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const selectedRange = computed({
  get: () => props.modelValue || '',
  set: (val) => emit('update:modelValue', val),
})
</script>

<style scoped lang="scss">
.chart-card {
  border-radius: 16px;
  border: 1px solid #E8E8EF;
  box-shadow: 0 4px 12px rgba(26, 26, 46, 0.08);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 8px 24px rgba(26, 26, 46, 0.12);
  }
  
  :deep(.el-card__header) {
    padding: 20px 24px;
    border-bottom: 1px solid #F0F0F5;
  }
  
  :deep(.el-card__body) {
    padding: 24px;
  }
  
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
    
    .chart-title {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .title-icon {
        color: #FF6B6B;
      }
      
      .title-text {
        font-size: 16px;
        font-weight: 600;
        color: #1A1A2E;
      }
      
      .title-tag {
        margin-left: 8px;
        background: #FFF0F0;
        border-color: #FF6B6B;
        color: #FF6B6B;
      }
    }
    
    .chart-actions {
      :deep(.el-radio-group) {
        .el-radio-button {
          &__inner {
            border-color: #E8E8EF;
            color: #8A8AA3;
            
            &:hover {
              color: #FF6B6B;
            }
          }
          
          &.is-active {
            .el-radio-button__inner {
              background: #FF6B6B;
              border-color: #FF6B6B;
              color: #fff;
              box-shadow: -1px 0 0 0 #FF6B6B;
            }
          }
        }
      }
    }
  }
  
  .chart-body {
    width: 100%;
  }
  
  .chart-footer {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #F0F0F5;
  }
}

// 响应式适配
@media (max-width: 768px) {
  .chart-card {
    :deep(.el-card__header) {
      padding: 16px;
    }
    
    :deep(.el-card__body) {
      padding: 16px;
    }
    
    .chart-header {
      flex-direction: column;
      align-items: flex-start;
      
      .chart-actions {
        width: 100%;
        
        :deep(.el-radio-group) {
          display: flex;
          width: 100%;
          
          .el-radio-button {
            flex: 1;
            
            &__inner {
              width: 100%;
              text-align: center;
              padding: 8px 12px;
            }
          }
        }
      }
    }
  }
}
</style>
