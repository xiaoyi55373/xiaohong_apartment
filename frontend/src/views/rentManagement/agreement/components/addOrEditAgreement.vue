<template>
  <div class="xh-detail-container xh-detail-container--narrow">
    <!-- 页面头部 -->
    <div class="xh-detail-header">
      <div class="xh-detail-header__left">
        <div class="xh-detail-header__icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="xh-detail-header__title-section">
          <h2 class="xh-detail-header__title">
            {{ formData.id ? '修改租约' : '新增租约' }}
          </h2>
          <p class="xh-detail-header__subtitle">
            {{ formData.id ? '修改现有租约信息' : '创建新的房屋租赁合同' }}
          </p>
        </div>
      </div>
      <div class="xh-detail-header__right">
        <span 
          class="xh-detail-header__status" 
          :class="`xh-detail-header__status--${getStatusType(formData.status)}`"
        >
          {{ getStatusLabel(formData.status) }}
        </span>
      </div>
    </div>

    <!-- 详情内容 -->
    <div class="xh-detail-layout">
      <!-- 租客信息卡片 -->
      <DetailCard
        title="租客信息"
        subtitle="填写租客的基本信息"
        icon="User"
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
            <el-form-item label="租客姓名" prop="name">
              <el-input 
                v-model.trim="formData.name" 
                placeholder="请输入租客姓名"
                class="xh-input"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="手机号码" prop="phone">
              <el-input 
                v-model.trim="formData.phone" 
                placeholder="请输入手机号码"
                class="xh-input"
                clearable
              >
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <el-form-item label="身份证号" prop="identificationNumber">
            <el-input
              v-model.trim="formData.identificationNumber"
              placeholder="请输入身份证号码"
              class="xh-input"
              clearable
            >
              <template #prefix>
                <el-icon><Postcard /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </DetailCard>

      <!-- 房源信息卡片 -->
      <DetailCard
        title="房源信息"
        subtitle="选择租赁的房源位置"
        icon="OfficeBuilding"
        size="large"
        theme="success"
      >
        <el-form
          ref="roomFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
        >
          <el-form-item label="房源位置" required>
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
                  @change="apartmentChangeCallback"
                  @clear="apartmentClearCallback"
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
              <el-form-item prop="roomId" class="inline-form-item">
                <el-select
                  v-model="areaInfo.roomId"
                  placeholder="请选择房间"
                  clearable
                  filterable
                  :disabled="!areaInfo.apartmentId"
                  class="xh-select"
                >
                  <el-option
                    v-for="item in areaInfo.roomList"
                    :key="item.id"
                    :label="item.roomNumber"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </div>
          </el-form-item>
        </el-form>
      </DetailCard>

      <!-- 租约信息卡片 -->
      <DetailCard
        title="租约信息"
        subtitle="设置租约期限和费用"
        icon="Calendar"
        size="large"
        theme="warning"
      >
        <el-form
          ref="leaseFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
        >
          <div class="xh-detail-grid xh-detail-grid--2">
            <el-form-item label="开始时间" prop="leaseStartDate">
              <el-date-picker
                v-model="formData.leaseStartDate"
                type="date"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                placeholder="选择开始时间"
                clearable
                class="xh-date-picker"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="结束时间" prop="leaseEndDate">
              <el-date-picker
                v-model="formData.leaseEndDate"
                type="date"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                placeholder="选择结束时间"
                clearable
                class="xh-date-picker"
                style="width: 100%"
              />
            </el-form-item>
          </div>

          <div class="xh-detail-grid xh-detail-grid--2">
            <el-form-item label="支付方式" prop="paymentTypeId">
              <el-select
                v-model="formData.paymentTypeId"
                placeholder="请选择支付方式"
                clearable
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

            <el-form-item label="可选租期" prop="leaseTermId">
              <el-select
                v-model="formData.leaseTermId"
                placeholder="请选择租期"
                clearable
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

          <div class="xh-detail-grid xh-detail-grid--2">
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
              </el-input-number>
            </el-form-item>

            <el-form-item label="押金" prop="deposit">
              <el-input-number 
                v-model="formData.deposit" 
                :step="100" 
                :min="0"
                step-strictly 
                class="xh-input-number"
                style="width: 100%"
              >
                <template #prefix>
                  <span style="color: #FF6B6B; font-weight: 600;">¥</span>
                </template>
              </el-input-number>
            </el-form-item>
          </div>
        </el-form>
      </DetailCard>

      <!-- 备注信息卡片 -->
      <DetailCard
        title="备注信息"
        subtitle="添加租约相关备注"
        icon="EditPen"
        size="large"
        theme="info"
      >
        <el-form
          ref="remarkFormRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="xh-form"
        >
          <el-form-item label="备注" prop="additionalInfo">
            <el-input
              v-model.trim="formData.additionalInfo"
              type="textarea"
              :rows="4"
              placeholder="请输入备注信息，如特殊约定、注意事项等"
              class="xh-textarea"
              maxlength="500"
              show-word-limit
            />
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
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, FormInstance } from 'element-plus'
import {
  Document,
  User,
  Phone,
  Postcard,
  OfficeBuilding,
  Calendar,
  EditPen,
  Check,
  Close,
} from '@element-plus/icons-vue'
import {
  ApartmentInterface,
  PaymentInfoInterface,
  RegionInterface,
  TermInfoInterface,
} from '@/api/apartmentManagement/types'
import {
  getApartmentListByDistrictId,
  getCityList,
  getDistrictList,
  getPaymentList,
  getProvinceList,
  getTermList,
} from '@/api/apartmentManagement'
import { useRoute, useRouter } from 'vue-router'
import { AgreementSource, AgreementStatus, AgreementStatusMap } from '@/enums/constEnums'
import {
  AgreementInterface,
  RoomInfoInterface,
} from '@/api/rentManagement/types'
import {
  getAgreementInfoById,
  getRoomListByApartmentId,
  saveOrUpdateAgreementInfo,
} from '@/api/rentManagement'
import DetailCard from '@/components/DetailCard/index.vue'

const route = useRoute()
const router = useRouter()

// 表单引用
const formRef = ref<FormInstance>()
const roomFormRef = ref<FormInstance>()
const leaseFormRef = ref<FormInstance>()
const remarkFormRef = ref<FormInstance>()

// 提交加载状态
const submitLoading = ref(false)

// 表单数据
const formData = ref<AgreementInterface>({
  id: '',
  name: '',
  phone: '',
  identificationNumber: '',
  apartmentId: '',
  roomId: '',
  leaseStartDate: '',
  leaseEndDate: '',
  leaseTermId: '',
  rent: 0,
  deposit: 0,
  paymentTypeId: '',
  status: AgreementStatus.WAITING,
  sourceType: AgreementSource.NEW,
  additionalInfo: '',
})

// 表单验证规则
const rules = reactive({
  name: [{ required: true, message: '请输入租客姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  identificationNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^\d{17}[\dXx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  leaseStartDate: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  leaseEndDate: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  paymentTypeId: [{ required: true, message: '请选择支付方式', trigger: 'change' }],
  leaseTermId: [{ required: true, message: '请选择租期', trigger: 'change' }],
  rent: [{ required: true, message: '请输入租金', trigger: 'blur' }],
  deposit: [{ required: true, message: '请输入押金', trigger: 'blur' }],
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
  roomList: [] as RoomInfoInterface[],
  roomId: '',
})

// 支付方式信息
const paymentInfoList = ref<PaymentInfoInterface[]>([])
// 可选租期信息
const leaseTermInfoList = ref<TermInfoInterface[]>([])

// 监视地区信息变化,更新公寓信息
watch(
  () => areaInfo,
  (newVal) => {
    formData.value.apartmentId = newVal.apartmentId
    formData.value.roomId = newVal.roomId
  },
  { immediate: true, deep: true },
)

// 获取状态标签
const getStatusLabel = (status: AgreementStatus) => {
  return AgreementStatusMap.find(item => item.value === status)?.label || '未知'
}

// 获取状态类型
const getStatusType = (status: AgreementStatus) => {
  const typeMap: Record<number, string> = {
    [AgreementStatus.WAITING]: 'warning',
    [AgreementStatus.SIGNED]: 'success',
    [AgreementStatus.EXPIRED]: 'info',
    [AgreementStatus.CANCELLED]: 'danger',
  }
  return typeMap[status] || 'info'
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

// 获取房间
async function getRoomListHandle(
  apartmentId: number | string = areaInfo.apartmentId,
) {
  if (!apartmentId) return
  try {
    const { data } = await getRoomListByApartmentId(apartmentId)
    areaInfo.roomList = data
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

// 重置房间的数据
function resetRoom() {
  areaInfo.roomId = ''
  areaInfo.roomList = []
}

// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = areaInfo.provinceId
  if (provinceId) {
    resetCity()
    resetDistrict()
    resetApartment()
    resetRoom()
    await getCityListHandle(provinceId)
  }
}

// 省份清除回调
const provinceClearCallback = () => {
  areaInfo.provinceId = ''
  resetCity()
  resetDistrict()
  resetApartment()
  resetRoom()
}

// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = areaInfo.cityId
  if (cityId) {
    resetDistrict()
    resetApartment()
    resetRoom()
    await getDistrictListHandle(cityId)
  }
}

// 城市清除回调
const cityClearCallback = () => {
  areaInfo.cityId = ''
  resetDistrict()
  resetApartment()
  resetRoom()
}

// 区域改变回调
const districtChangeCallback = async () => {
  let districtId = areaInfo.districtId
  if (districtId) {
    resetApartment()
    resetRoom()
    await getApartmentListHandle(districtId)
  }
}

// 区域清除回调
const districtClearCallback = () => {
  areaInfo.districtId = ''
  resetApartment()
  resetRoom()
}

// 公寓改变回调
const apartmentChangeCallback = async () => {
  let apartmentId = areaInfo.apartmentId
  if (apartmentId) {
    resetRoom()
    await getRoomListHandle(apartmentId)
  }
}

// 公寓清除回调
const apartmentClearCallback = () => {
  areaInfo.apartmentId = ''
  resetRoom()
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

// 根据id获取租约信息
async function getAgreementInfoByIdHandle(id: number | string) {
  try {
    const { data } = await getAgreementInfoById(id)
    formData.value = data
    // 重置省市区
    areaInfo.provinceId = data.apartmentInfo.provinceId as string
    areaInfo.cityId = data.apartmentInfo.cityId as string
    areaInfo.districtId = data.apartmentInfo.districtId as string
    areaInfo.apartmentId = data.apartmentId as string
    areaInfo.roomId = data.roomId as string
    data.apartmentInfo.provinceId &&
      getCityListHandle(data.apartmentInfo.provinceId)
    data.apartmentInfo.cityId &&
      getDistrictListHandle(data.apartmentInfo.cityId)
    data.apartmentInfo.districtId &&
      getApartmentListHandle(data.apartmentInfo.districtId)
    data.apartmentId && getRoomListHandle(data.apartmentId)
  } catch (error) {
    console.log(error)
  }
}

// 新增或更新租约信息
async function addOrUpdateAgreementInfoHandle() {
  try {
    submitLoading.value = true
    await saveOrUpdateAgreementInfo(formData.value)
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
      await addOrUpdateAgreementInfoHandle()
    } else {
      ElMessage.error('表单填写有误，请检查')
      return false
    }
  })
}

onMounted(() => {
  // 获取省份信息列表
  getProvinceListHandle()
  // 获取支付方式信息列表
  getPaymentInfoListHandle()
  // 获取可选租期信息列表
  getLeaseTermInfoListHandle()
  if (route.query?.id) {
    getAgreementInfoByIdHandle(route.query.id as string)
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

// 响应式适配
@media (max-width: 768px) {
  .cascading-selects {
    flex-direction: column;

    .inline-form-item {
      width: 100%;
    }
  }
}
</style>
