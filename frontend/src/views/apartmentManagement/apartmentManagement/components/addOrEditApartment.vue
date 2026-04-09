<template>
  <div class="xh-form-container xh-form-container--narrow">
    <div class="xh-form-layout">
      <!-- 基本信息卡片 -->
      <FormCard
        title="基本信息"
        subtitle="填写公寓的基本信息"
        icon="OfficeBuilding"
        size="large"
      >
        <el-form
          ref="apartmentFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
          status-icon
        >
          <div class="form-grid form-grid--2-col">
            <el-form-item label="公寓名称" prop="name">
              <el-input 
                v-model="formData.name" 
                placeholder="请输入公寓名称"
                class="xh-input"
                clearable
              />
            </el-form-item>
            
            <el-form-item label="公寓前台电话" prop="phone">
              <el-input 
                v-model="formData.phone" 
                placeholder="请输入前台电话"
                class="xh-input"
                clearable
              >
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <el-form-item label="公寓介绍" prop="introduction">
            <el-input 
              type="textarea" 
              v-model="formData.introduction" 
              placeholder="请输入公寓介绍，建议包含公寓特色、服务等信息"
              class="xh-textarea"
              :rows="4"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="是否发布" prop="isRelease">
            <el-radio-group v-model="formData.isRelease" class="xh-radio-group">
              <el-radio :label="ApartmentReleaseStatus.NOT_RELEASED" class="xh-radio">
                <el-tag size="small" effect="light" type="info">未发布</el-tag>
                <span class="radio-desc">暂不在前端展示</span>
              </el-radio>
              <el-radio :label="ApartmentReleaseStatus.RELEASED" class="xh-radio">
                <el-tag size="small" effect="light" type="success">已发布</el-tag>
                <span class="radio-desc">用户可查看和预约</span>
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </FormCard>

      <!-- 位置信息卡片 -->
      <FormCard
        title="位置信息"
        subtitle="设置公寓的地理位置"
        icon="MapLocation"
        size="large"
      >
        <el-form
          ref="locationFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
        >
          <el-form-item label="所处区域" required>
            <div class="cascading-selects">
              <el-form-item prop="provinceId" class="inline-form-item">
                <el-select
                  v-model="formData.provinceId"
                  placeholder="请选择省份"
                  clearable
                  @change="provinceChangeCallback"
                  @clear="provinceClearCallback"
                  class="xh-select"
                >
                  <el-option
                    v-for="item in areaInfo.provinceList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item prop="cityId" class="inline-form-item">
                <el-select
                  v-model="formData.cityId"
                  placeholder="请选择城市"
                  clearable
                  :disabled="!formData.provinceId"
                  @change="cityChangeCallback"
                  @clear="cityClearCallback"
                  class="xh-select"
                >
                  <el-option
                    v-for="item in areaInfo.cityList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item prop="districtId" class="inline-form-item">
                <el-select
                  v-model="formData.districtId"
                  placeholder="请选择区域"
                  clearable
                  :disabled="!formData.cityId"
                  @change="districtChangeCallback"
                  @clear="districtClearCallback"
                  class="xh-select"
                >
                  <el-option
                    v-for="item in areaInfo.districtList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
          </el-form-item>

          <el-form-item label="详细地址" prop="addressDetail">
            <el-input
              v-model="formData.addressDetail"
              placeholder="请输入详细地址"
              class="xh-input"
              clearable
            >
              <template #prefix>
                <el-icon><Location /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </FormCard>

      <!-- 配套信息卡片 -->
      <FormCard
        title="配套与标签"
        subtitle="选择公寓的配套设施和标签"
        icon="CollectionTag"
        size="large"
      >
        <el-form
          ref="facilityFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
        >
          <el-form-item label="公寓配套" prop="facilityInfoIds">
            <el-select
              v-model="formData.facilityInfoIds"
              placeholder="请选择公寓配套"
              multiple
              clearable
              collapse-tags
              collapse-tags-tooltip
              class="xh-select"
              style="width: 100%"
            >
              <el-option
                v-for="item in facilityInfoList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
            <div class="form-item-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>多选，可选择多个配套设施</span>
            </div>
          </el-form-item>

          <el-form-item label="公寓标签" prop="labelIds">
            <el-select
              v-model="formData.labelIds"
              placeholder="请选择公寓标签"
              multiple
              clearable
              collapse-tags
              collapse-tags-tooltip
              class="xh-select"
              style="width: 100%"
            >
              <el-option
                v-for="item in labelInfoList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
            <div class="form-item-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>多选，标签将展示在公寓列表中</span>
            </div>
          </el-form-item>

          <el-form-item label="公寓杂费" prop="feeValueIds">
            <el-tree-select
              ref="feeTreeSelectRef"
              v-model="formData.feeValueIds"
              placeholder="请选择公寓杂费"
              :data="feeInfoList"
              multiple
              clearable
              node-key="value"
              :render-after-expand="false"
              @node-click="feeNodeClickHandle"
              class="xh-tree-select"
              style="width: 100%"
            />
            <div class="form-item-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>选择各项杂费的收费标准</span>
            </div>
          </el-form-item>
        </el-form>
      </FormCard>

      <!-- 图片信息卡片 -->
      <FormCard
        title="图片信息"
        subtitle="上传公寓的图片，最多5张"
        icon="Picture"
        size="large"
      >
        <el-form
          ref="imageFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
        >
          <el-form-item label="公寓图片" prop="graphVoList">
            <div class="upload-wrapper">
              <upload-img
                v-model:file-list="formData.graphVoList"
                :on-success="uploadSuccessHandle"
                list-type="picture-card"
                :limit="5"
                drag
                :max-size="10 * 1024 * 1024"
                :enable-compress="true"
                :compress-quality="0.8"
                accept="jpg,jpeg,png,gif,webp"
                tip="点击或拖拽上传"
              />
            </div>
            <div class="form-item-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>支持拖拽上传，自动压缩，第一张将作为封面展示</span>
            </div>
          </el-form-item>
        </el-form>
      </FormCard>

      <!-- 操作按钮 -->
      <div class="xh-form-actions xh-form-actions--center">
        <el-button size="large" @click="router.back()">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
        <el-button
          size="large"
          type="primary"
          :loading="submitLoading"
          @click="submitHandle"
        >
          <el-icon><Check /></el-icon>
          {{ formData.id ? '保存修改' : '确认新增' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, FormInstance, UploadFiles } from 'element-plus'
import {
  OfficeBuilding,
  MapLocation,
  CollectionTag,
  Picture,
  Phone,
  Location,
  InfoFilled,
  Check,
  Close,
} from '@element-plus/icons-vue'
import {
  AddressOptionsInterface,
  ApartmentInterface,
  FacilityInfoInterface,
  LabelInfoInterface,
  RegionInterface,
} from '@/api/apartmentManagement/types'
import {
  ApartmentReleaseStatus,
  getLabelByValue,
  ApartmentReleaseStatusMap,
  BuildingType,
} from '@/enums/constEnums'
import {
  getApartmentById,
  getCityList,
  getDistrictList,
  getFacilityInfoList,
  getFeeInfoList,
  getLabelInfoList,
  getProvinceList,
  saveOrUpdateApartment,
} from '@/api/apartmentManagement'
import { TreeData } from '@/api/apartmentManagement/types'
import { ElTree } from 'element-plus/es/components/tree'
import { UploadFile } from 'element-plus/es/components/upload/src/upload'
import UploadImg from '@/components/uploadImg/uploadImg.vue'
import FormCard from '@/components/FormCard/index.vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 表单引用
const apartmentFormRef = ref<FormInstance>()
const locationFormRef = ref<FormInstance>()
const facilityFormRef = ref<FormInstance>()
const imageFormRef = ref<FormInstance>()

// 提交加载状态
const submitLoading = ref(false)

// 表单数据
type FormDataInstance = Required<
  Omit<
    ApartmentInterface,
    | 'totalRoomCount'
    | 'freeRoomCount'
    | 'facilityInfoList'
    | 'labelInfoList'
    | 'feeValueVoList'
  >
>

const formData = ref<FormDataInstance>({
  id: '',
  name: '',
  introduction: '',
  districtId: '',
  districtName: '',
  cityId: '',
  cityName: '',
  provinceId: '',
  provinceName: '',
  addressDetail: '',
  latitude: '',
  longitude: '',
  phone: '',
  isRelease: ApartmentReleaseStatus.NOT_RELEASED,
  facilityInfoIds: [],
  labelIds: [],
  feeValueIds: [],
  graphVoList: [] as UploadFile[],
})

// 表单验证规则
const rules = reactive({
  name: [{ required: true, message: '请输入公寓名称', trigger: 'blur' }],
  introduction: [
    { required: true, message: '请输入公寓介绍', trigger: 'blur' },
  ],
  provinceId: [{ required: true, message: '请选择省份', trigger: 'change' }],
  cityId: [{ required: true, message: '请选择城市', trigger: 'change' }],
  districtId: [{ required: true, message: '请选择区域', trigger: 'change' }],
  addressDetail: [
    { required: true, message: '请输入详细地址', trigger: 'change' },
  ],
  phone: [{ required: true, message: '请输入公寓前台电话', trigger: 'blur' }],
  graphVoList: [{ required: true, message: '请上传图片', trigger: 'change' }],
})

// 地区数据
const areaInfo = reactive({
  provinceList: [] as RegionInterface[],
  cityList: [] as RegionInterface[],
  districtList: [] as RegionInterface[],
})

// 配套信息
const facilityInfoList = ref<FacilityInfoInterface[]>([])
const labelInfoList = ref<LabelInfoInterface[]>([])
const feeInfoList = ref<TreeData[]>([])
const feeTreeSelectRef = ref<InstanceType<typeof ElTree>>()

// 获取省份
async function getProvinceListHandle() {
  try {
    const { data } = await getProvinceList()
    areaInfo.provinceList = data
  } catch (error) {
    console.log(error)
  }
}

// 获取城市
async function getCityListHandle(
  provinceId: number | string = formData.value.provinceId,
) {
  if (!provinceId) return
  try {
    const { data } = await getCityList(provinceId)
    areaInfo.cityList = data
  } catch (error) {
    console.log(error)
  }
}

// 获取区域
async function getDistrictListHandle(
  cityId: number | string = formData.value.cityId,
) {
  if (!cityId) return
  try {
    const { data } = await getDistrictList(cityId)
    areaInfo.districtList = data
  } catch (error) {
    console.log(error)
  }
}

// 重置市数据
function resetCity() {
  formData.value.cityId = ''
  areaInfo.cityList = []
}

// 重置区数据
function resetDistrict() {
  formData.value.districtId = ''
  areaInfo.districtList = []
}

// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = formData.value.provinceId
  if (provinceId) {
    resetCity()
    resetDistrict()
    await getCityListHandle(provinceId)
  }
}

// 省份清除回调
const provinceClearCallback = () => {
  formData.value.provinceId = ''
  resetCity()
  resetDistrict()
}

// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = formData.value.cityId
  if (cityId) {
    resetDistrict()
    await getDistrictListHandle(cityId)
  }
}

// 城市清除回调
const cityClearCallback = () => {
  formData.value.cityId = ''
  resetDistrict()
}

// 区域改变回调
const districtChangeCallback = async () => {
  console.log('区域改变')
}

// 区域清除回调
const districtClearCallback = () => {
  formData.value.districtId = ''
}

// 获取配套信息
async function getFacilityInfoListHandle() {
  try {
    const { data } = await getFacilityInfoList(BuildingType.APARTMENT)
    facilityInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}

// 获取标签信息
async function getLabelInfoListHandle() {
  try {
    const { data } = await getLabelInfoList(BuildingType.APARTMENT)
    labelInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}

// 获取杂费信息
async function getFeeInfoListHandle() {
  try {
    const { data } = await getFeeInfoList()
    feeInfoList.value = data.map((item) => {
      return {
        value: item.id + item.name,
        label: item.name,
        children: item.feeValueList.map((child) => {
          return {
            value: child.id,
            label: `${child.name} ${child.unit}(${item.name})`,
            parentId: item.id + item.name,
          }
        }),
      }
    })
  } catch (error) {
    console.log(error)
  }
}

// 配套信息节点点击回调
function feeNodeClickHandle(data: TreeData) {
  if (data.parentId) {
    const childrenList = feeInfoList.value
      .find((item) => item.value === data.parentId)
      ?.children?.map((item) => item.value)
    nextTick(() => {
      formData.value.feeValueIds = formData.value.feeValueIds?.filter(
        (item) => !childrenList?.includes(item),
      )
      formData.value.feeValueIds?.push(data.value as number)
    })
  }
}

// 图片上传成功
function uploadSuccessHandle(
  response: any,
  uploadFile: UploadFile,
  uploadFiles: UploadFiles,
) {
  formData.value.graphVoList = uploadFiles?.map((item) => {
    return {
      ...item,
      url: (item?.response as any)?.data || item.url,
    }
  })
}

// 根据id获取公寓信息
async function getApartmentInfoByIdHandle(id: number | string) {
  try {
    const { data } = await getApartmentById(id)
    data.facilityInfoIds = data.facilityInfoList?.map(
      (item) => item.id,
    ) as number[]
    delete data.facilityInfoList
    data.labelIds = data.labelInfoList?.map((item) => item.id) as number[]
    delete data.labelInfoList
    data.feeValueIds = data.feeValueVoList?.map((item) => item.id) as number[]
    delete data.feeValueVoList
    formData.value = data as FormDataInstance
    // 获取城市
    formData.value.provinceId && getCityListHandle(formData.value.provinceId)
    // 获取区域
    formData.value.cityId && getDistrictListHandle(formData.value.cityId)
  } catch (error) {
    console.log(error)
  }
}

// 新增或更新公寓信息
async function addOrUpdateApartmentInfoHandle() {
  try {
    submitLoading.value = true
    await saveOrUpdateApartment(formData.value)
    ElMessage.success('操作成功')
    router.back()
  } catch (error) {
    console.log(error)
    submitLoading.value = false
  }
}

// 提交
function submitHandle() {
  apartmentFormRef.value?.validate(async (valid) => {
    if (valid) {
      await addOrUpdateApartmentInfoHandle()
    } else {
      ElMessage.error('表单填写有误，请检查')
      return false
    }
  })
}

onMounted(async () => {
  // 获取省份
  getProvinceListHandle()
  // 获取配套信息
  getFacilityInfoListHandle()
  // 获取标签信息
  getLabelInfoListHandle()
  // 获取杂费信息
  getFeeInfoListHandle()
  if (route.query?.id) {
    getApartmentInfoByIdHandle(route.query.id as string)
  }
})
</script>

<style scoped lang="scss">
// 表单网格布局
.form-grid {
  display: grid;
  gap: 0 24px;

  &--2-col {
    grid-template-columns: repeat(2, 1fr);
  }
}

// 级联选择器
.cascading-selects {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;

  .inline-form-item {
    margin-bottom: 0;
    flex: 1;
    min-width: 150px;
  }
}

// 单选框组
.xh-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}

.xh-radio {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 0;
  height: auto;
  padding: 8px 16px;
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

.radio-desc {
  font-size: 13px;
  color: #8A8AA3;
}

// 表单项提示
.form-item-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 12px;
  color: #8A8AA3;

  .el-icon {
    font-size: 14px;
    color: #FF6B6B;
  }
}

// 上传区域
.upload-wrapper {
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
    }

    .el-icon {
      font-size: 28px;
      color: #FF6B6B;
    }
  }

  :deep(.el-upload-list__item) {
    width: 120px;
    height: 120px;
    border-radius: 10px;
    border: 1px solid #E8E8EF;
    transition: all 0.3s ease;

    &:hover {
      border-color: #FF8E8E;
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .form-grid {
    &--2-col {
      grid-template-columns: 1fr;
    }
  }

  .cascading-selects {
    flex-direction: column;

    .inline-form-item {
      width: 100%;
    }
  }

  .xh-radio-group {
    flex-direction: column;
    gap: 12px;
  }

  .xh-radio {
    width: 100%;
  }
}
</style>
