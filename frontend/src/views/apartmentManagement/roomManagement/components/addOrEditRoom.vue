<template>
  <div class="xh-detail-container xh-detail-container--narrow">
    <!-- 页面头部 -->
    <div class="xh-detail-header">
      <div class="xh-detail-header__left">
        <div class="xh-detail-header__icon">
          <el-icon><HomeFilled /></el-icon>
        </div>
        <div class="xh-detail-header__title-section">
          <h2 class="xh-detail-header__title">
            {{ formData.id ? '修改房间' : '新增房间' }}
          </h2>
          <p class="xh-detail-header__subtitle">
            {{ formData.id ? '修改房间信息' : '添加新的房间到公寓' }}
          </p>
        </div>
      </div>
      <div class="xh-detail-header__right">
        <span 
          class="xh-detail-header__status" 
          :class="`xh-detail-header__status--${formData.isRelease === RoomReleaseStatus.RELEASED ? 'success' : 'info'}`"
        >
          {{ getReleaseStatusLabel(formData.isRelease) }}
        </span>
      </div>
    </div>

    <!-- 详情内容 -->
    <div class="xh-detail-layout">
      <!-- 基本信息卡片 -->
      <DetailCard
        title="基本信息"
        subtitle="填写房间的基本信息"
        icon="InfoFilled"
        size="large"
        theme="primary"
      >
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
          status-icon
        >
          <div class="xh-detail-grid xh-detail-grid--2">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input 
                v-model="formData.roomNumber" 
                placeholder="请输入房间号，如 101"
                class="xh-input"
                clearable
              >
                <template #prefix>
                  <el-icon><HomeFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="租金" prop="rent">
              <el-input-number 
                v-model="formData.rent" 
                :step="100" 
                :min="0"
                step-strictly 
                class="xh-input-number"
                style="width: 100%"
              >
                <template #prefix>
                  <span style="color: #FF6B6B; font-weight: 600;">¥</span>
                </template>
                <template #suffix>
                  <span style="color: #8A8AA3;">元/月</span>
                </template>
              </el-input-number>
            </el-form-item>
          </div>

          <el-form-item label="是否发布" prop="isRelease">
            <el-radio-group v-model="formData.isRelease" class="xh-radio-group">
              <el-radio :label="RoomReleaseStatus.NOT_RELEASED" class="xh-radio xh-radio--card">
                <el-tag size="small" effect="light" type="info">未发布</el-tag>
                <span class="radio-desc">暂不在前端展示</span>
              </el-radio>
              <el-radio :label="RoomReleaseStatus.RELEASED" class="xh-radio xh-radio--card">
                <el-tag size="small" effect="light" type="success">已发布</el-tag>
                <span class="radio-desc">用户可查看和预约</span>
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </DetailCard>

      <!-- 所属公寓卡片 -->
      <DetailCard
        title="所属公寓"
        subtitle="选择房间所属的公寓"
        icon="OfficeBuilding"
        size="large"
        theme="success"
      >
        <el-form
          ref="apartmentFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
        >
          <el-form-item label="公寓位置" required>
            <div class="cascading-selects">
              <el-form-item prop="provinceId" class="inline-form-item">
                <el-select
                  v-model="areaInfo.provinceId"
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
                  v-model="areaInfo.cityId"
                  placeholder="请选择城市"
                  clearable
                  :disabled="!areaInfo.provinceId"
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
                  v-model="areaInfo.districtId"
                  placeholder="请选择区域"
                  clearable
                  :disabled="!areaInfo.cityId"
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
              <el-form-item prop="apartmentId" class="inline-form-item">
                <el-select
                  v-model="areaInfo.apartmentId"
                  placeholder="请选择公寓"
                  clearable
                  filterable
                  :disabled="!areaInfo.districtId"
                  class="xh-select"
                >
                  <el-option
                    v-for="item in areaInfo.apartmentList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
          </el-form-item>
        </el-form>
      </DetailCard>

      <!-- 房间属性卡片 -->
      <DetailCard
        title="房间属性"
        subtitle="设置房间的属性和配套"
        icon="CollectionTag"
        size="large"
        theme="warning"
      >
        <el-form
          ref="attrFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
        >
          <el-form-item label="房间属性" prop="attrValueIds">
            <el-tree-select
              ref="attrTreeSelectRef"
              v-model="formData.attrValueIds"
              placeholder="请选择房间属性"
              :data="attrInfoList"
              multiple
              clearable
              node-key="value"
              :render-after-expand="false"
              @node-click="attrNodeClickHandle"
              class="xh-tree-select"
              style="width: 100%"
            />
            <div class="form-item-hint">
              <el-icon><InfoFilled /></el-icon>
              <span>选择房间的基本属性，如朝向、楼层等</span>
            </div>
          </el-form-item>

          <el-form-item label="房间配套" prop="facilityInfoIds">
            <el-select
              v-model="formData.facilityInfoIds"
              placeholder="请选择房间配套"
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
              <span>多选，可选择多个房间配套设施</span>
            </div>
          </el-form-item>

          <el-form-item label="房间标签" prop="labelInfoIds">
            <el-select
              v-model="formData.labelInfoIds"
              placeholder="请选择房间标签"
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
              <span>多选，标签将展示在房间列表中</span>
            </div>
          </el-form-item>
        </el-form>
      </DetailCard>

      <!-- 租赁配置卡片 -->
      <DetailCard
        title="租赁配置"
        subtitle="设置房间的租赁相关配置"
        icon="Wallet"
        size="large"
        theme="info"
      >
        <el-form
          ref="leaseFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
        >
          <div class="xh-detail-grid xh-detail-grid--2">
            <el-form-item label="支付方式" prop="paymentTypeIds">
              <el-select
                v-model="formData.paymentTypeIds"
                placeholder="请选择支付方式"
                multiple
                clearable
                collapse-tags
                collapse-tags-tooltip
                class="xh-select"
                style="width: 100%"
              >
                <el-option
                  v-for="item in paymentInfoList"
                  :key="item.id"
                  :label="`${item.name} (${item.additionalInfo})`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="可选租期" prop="leaseTermIds">
              <el-select
                v-model="formData.leaseTermIds"
                placeholder="请选择可选租期"
                multiple
                clearable
                collapse-tags
                collapse-tags-tooltip
                class="xh-select"
                style="width: 100%"
              >
                <el-option
                  v-for="item in leaseTermInfoList"
                  :key="item.id"
                  :label="`${item.monthCount}${item.unit}`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </div>
        </el-form>
      </DetailCard>

      <!-- 图片信息卡片 -->
      <DetailCard
        title="图片信息"
        subtitle="上传房间的展示图片，最多5张"
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
          <el-form-item label="房间图片" prop="graphVoList">
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
      </DetailCard>

      <!-- 操作按钮 -->
      <div class="xh-detail-actions xh-detail-actions--center">
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
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, FormInstance, UploadFiles } from 'element-plus'
import {
  HomeFilled,
  OfficeBuilding,
  InfoFilled,
  CollectionTag,
  Wallet,
  Picture,
  Check,
  Close,
} from '@element-plus/icons-vue'
import {
  ApartmentInterface,
  FacilityInfoInterface,
  LabelInfoInterface,
  PaymentInfoInterface,
  RegionInterface,
  SaveRoomInterface,
  TermInfoInterface,
  TreeData,
} from '@/api/apartmentManagement/types'
import {
  getApartmentListByDistrictId,
  getAttrInfoList,
  getCityList,
  getDistrictList,
  getFacilityInfoList,
  getLabelInfoList,
  getPaymentList,
  getProvinceList,
  getRoomById,
  getTermList,
  saveOrUpdateRoom,
} from '@/api/apartmentManagement'
import { UploadFile } from 'element-plus/es/components/upload/src/upload'
import UploadImg from '@/components/uploadImg/uploadImg.vue'
import { useRoute, useRouter } from 'vue-router'
import { ElTree } from 'element-plus/es/components/tree'
import {
  BuildingType,
  getLabelByValue,
  RoomReleaseStatus,
  RoomReleaseStatusMap,
} from '@/enums/constEnums'
import DetailCard from '@/components/DetailCard/index.vue'

const route = useRoute()
const router = useRouter()

// 表单引用
const formRef = ref<FormInstance>()
const apartmentFormRef = ref<FormInstance>()
const attrFormRef = ref<FormInstance>()
const leaseFormRef = ref<FormInstance>()
const imageFormRef = ref<FormInstance>()

// 提交加载状态
const submitLoading = ref(false)

// 表单数据
const formData = ref<SaveRoomInterface>({
  id: '',
  roomNumber: '',
  rent: 0,
  apartmentId: '',
  isRelease: RoomReleaseStatus.NOT_RELEASED,
  attrValueIds: [],
  facilityInfoIds: [],
  labelInfoIds: [],
  paymentTypeIds: [],
  leaseTermIds: [],
  graphVoList: [] as UploadFile[],
})

// 表单验证规则
const rules = reactive({
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  rent: [{ required: true, message: '请输入租金', trigger: 'blur' }],
  apartmentId: [{ required: true, message: '请选择公寓', trigger: 'change' }],
  attrValueIds: [{ required: true, message: '请选择属性', trigger: 'change' }],
  facilityInfoIds: [
    { required: true, message: '请选择配套', trigger: 'change' },
  ],
  labelInfoIds: [{ required: true, message: '请选择标签', trigger: 'change' }],
  paymentTypeIds: [
    { required: true, message: '请选择支付方式', trigger: 'change' },
  ],
  leaseTermIds: [
    { required: true, message: '请选择可选租期', trigger: 'change' },
  ],
  graphVoList: [{ required: true, message: '请上传图片', trigger: 'change' }],
})

// 地区数据
const areaInfo = reactive({
  provinceList: [] as RegionInterface[],
  provinceId: '',
  cityList: [] as RegionInterface[],
  cityId: '',
  districtList: [] as RegionInterface[],
  districtId: '',
  apartmentList: [] as ApartmentInterface[],
  apartmentId: '',
})

// 配套信息
const facilityInfoList = ref<FacilityInfoInterface[]>([])
const labelInfoList = ref<LabelInfoInterface[]>([])
const attrInfoList = ref<TreeData[]>([])
const paymentInfoList = ref<PaymentInfoInterface[]>([])
const leaseTermInfoList = ref<TermInfoInterface[]>([])
const attrTreeSelectRef = ref<InstanceType<typeof ElTree>>()

// 监视地区信息变化,更新公寓信息
watch(
  () => areaInfo,
  (newVal) => {
    formData.value.apartmentId = newVal.apartmentId
  },
  { immediate: true, deep: true },
)

// 获取发布状态标签
const getReleaseStatusLabel = (status: RoomReleaseStatus) => {
  return getLabelByValue(RoomReleaseStatusMap, status) || '未知'
}

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
  provinceId: number | string = areaInfo.provinceId,
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
  cityId: number | string = areaInfo.cityId,
) {
  if (!cityId) return
  try {
    const { data } = await getDistrictList(cityId)
    areaInfo.districtList = data
  } catch (error) {
    console.log(error)
  }
}

// 获取公寓
async function getApartmentListHandle(
  districtId: number | string = areaInfo.districtId,
) {
  if (!districtId) return
  try {
    const { data } = await getApartmentListByDistrictId(districtId)
    areaInfo.apartmentList = data
  } catch (error) {
    console.log(error)
  }
}

// 重置市数据
function resetCity() {
  areaInfo.cityId = ''
  areaInfo.cityList = []
}

// 重置区数据
function resetDistrict() {
  areaInfo.districtId = ''
  areaInfo.districtList = []
}

// 重置公寓数据
function resetApartment() {
  areaInfo.apartmentId = ''
  areaInfo.apartmentList = []
}

// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = areaInfo.provinceId
  if (provinceId) {
    resetCity()
    resetDistrict()
    resetApartment()
    await getCityListHandle(provinceId)
  }
}

// 省份清除回调
const provinceClearCallback = () => {
  areaInfo.provinceId = ''
  resetCity()
  resetDistrict()
  resetApartment()
}

// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = areaInfo.cityId
  if (cityId) {
    resetDistrict()
    resetApartment()
    await getDistrictListHandle(cityId)
  }
}

// 城市清除回调
const cityClearCallback = () => {
  areaInfo.cityId = ''
  resetDistrict()
  resetApartment()
}

// 区域改变回调
const districtChangeCallback = async () => {
  let districtId = areaInfo.districtId
  if (districtId) {
    resetApartment()
    await getApartmentListHandle(districtId)
  }
}

// 区域清除回调
const districtClearCallback = () => {
  areaInfo.districtId = ''
  resetApartment()
}

// 获取配套信息
async function getFacilityInfoListHandle() {
  try {
    const { data } = await getFacilityInfoList(BuildingType.ROOM)
    facilityInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}

// 获取标签信息
async function getLabelInfoListHandle() {
  try {
    const { data } = await getLabelInfoList(BuildingType.ROOM)
    labelInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}

// 获取属性信息
async function getAttrInfoListHandle() {
  try {
    const { data } = await getAttrInfoList()
    attrInfoList.value = data.map((item) => {
      return {
        value: item.id + item.name,
        label: item.name,
        children: item.attrValueList.map((child) => {
          return {
            value: child.id,
            label: `${child.name}(${item.name})`,
            parentId: item.id + item.name,
          }
        }),
      }
    })
  } catch (error) {
    console.log(error)
  }
}

// 获取支付方式信息列表
async function getPaymentInfoListHandle() {
  try {
    const { data } = await getPaymentList()
    paymentInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}

// 获取可选租期信息列表
async function getLeaseTermInfoListHandle() {
  try {
    const { data } = await getTermList()
    leaseTermInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}

// 属性信息节点点击回调
function attrNodeClickHandle(data: TreeData) {
  if (data.parentId) {
    const childrenList = attrInfoList.value
      .find((item) => item.value === data.parentId)
      ?.children?.map((item) => item.value)
    nextTick(() => {
      formData.value.attrValueIds = formData.value.attrValueIds?.filter(
        (item) => !childrenList?.includes(item),
      )
      formData.value.attrValueIds?.push(data.value as number)
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

// 根据id获取房间信息
async function getRoomInfoByIdHandle(id: number | string) {
  try {
    const { data } = await getRoomById(id)
    formData.value = data as unknown as SaveRoomInterface
    formData.value.attrValueIds =
      data.attrValueVoList?.map((item) => item.id) || []
    delete data.attrValueVoList
    formData.value.facilityInfoIds =
      (data.facilityInfoList?.map((item) => item.id) as number[]) || []
    delete data.facilityInfoList
    formData.value.labelInfoIds =
      (data.labelInfoList?.map((item) => item.id) as number[]) || []
    delete data.labelInfoList
    formData.value.paymentTypeIds =
      (data.paymentTypeList?.map((item) => item.id) as number[]) || []
    delete data.paymentTypeList
    formData.value.leaseTermIds =
      (data.leaseTermList?.map((item) => item.id) as number[]) || []
    delete data.leaseTermList
    // 重置省市区
    areaInfo.provinceId = data.apartmentInfo.provinceId as string
    areaInfo.cityId = data.apartmentInfo.cityId as string
    areaInfo.districtId = data.apartmentInfo.districtId as string
    areaInfo.apartmentId = data.apartmentId as string
    data.apartmentInfo.provinceId &&
      getCityListHandle(data.apartmentInfo.provinceId)
    data.apartmentInfo.cityId &&
      getDistrictListHandle(data.apartmentInfo.cityId)
    data.apartmentInfo.districtId &&
      getApartmentListHandle(data.apartmentInfo.districtId)
  } catch (error) {
    console.log(error)
  }
}

// 新增或更新房间信息
async function addOrUpdateRoomInfoHandle() {
  try {
    submitLoading.value = true
    await saveOrUpdateRoom(formData.value)
    ElMessage.success('操作成功')
    router.back()
  } catch (error) {
    console.log(error)
    submitLoading.value = false
  }
}

// 提交
function submitHandle() {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      await addOrUpdateRoomInfoHandle()
    } else {
      ElMessage.error('表单填写有误，请检查')
      return false
    }
  })
}

onMounted(() => {
  // 获取省份
  getProvinceListHandle()
  // 获取配套信息
  getFacilityInfoListHandle()
  // 获取标签信息
  getLabelInfoListHandle()
  // 获取属性信息
  getAttrInfoListHandle()
  // 获取支付方式信息列表
  getPaymentInfoListHandle()
  // 获取可选租期信息列表
  getLeaseTermInfoListHandle()
  if (route.query?.id) {
    getRoomInfoByIdHandle(route.query.id as string)
  }
})
</script>

<style scoped lang="scss">
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
  padding: 12px 20px;
  border-radius: 10px;
  background: #FAFAFC;
  border: 1px solid #E8E8EF;
  transition: all 0.3s ease;

  :deep(.el-radio__input) {
    margin-right: 4px;
  }

  :deep(.el-radio__label) {
    display: flex;
    align-items: center;
    gap: 10px;
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
