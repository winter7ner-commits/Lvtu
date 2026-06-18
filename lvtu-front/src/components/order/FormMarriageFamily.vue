<template>
  <div class="form-container">
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
      
      <!-- 1. 用户实名信息（系统自动带出，只读） -->
      <el-card class="form-section" shadow="never">
        <template #header>1. 用户实名信息</template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名">
              <el-input v-model="formData.realName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="formData.phone" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input v-model="formData.idCard" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="认证状态">
              <el-tag :type="formData.verified ? 'success' : 'info'">{{ formData.verifiedText }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账户类型">
              <el-input v-model="formData.accountType" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 2. 联系补充信息 -->
      <el-card class="form-section" shadow="never">
        <template #header>2. 联系补充信息 (用于特殊情况下联系)</template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="微信号" prop="wechat">
              <el-input v-model="formData.wechat" placeholder="可选" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" type="email" placeholder="可选" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="formData.emergencyContact" placeholder="可选" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急电话" prop="emergencyPhone">
              <el-input v-model="formData.emergencyPhone" maxlength="11" inputmode="numeric" placeholder="可选" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 3. 业务基础信息 -->
      <el-card class="form-section" shadow="never">
        <template #header>3. 业务基础信息</template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="业务类型">
              <el-input v-model="formData.businessType" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属地区" prop="region">
              <el-cascader
                v-model="formData.region"
                :options="regionOptions"
                clearable
                placeholder="请选择省/市"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否紧急" prop="isUrgent">
              <el-radio-group v-model="formData.isUrgent">
                <el-radio label="普通">普通</el-radio>
                <el-radio label="加急">加急</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期望完成时间" prop="expectedDate">
              <el-date-picker v-model="formData.expectedDate" type="date" placeholder="请选择日期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预估预算" prop="budget">
              <el-input v-model="formData.budget" inputmode="decimal" placeholder="请输入预算金额（元）" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务对象" prop="serviceSubject">
              <el-select v-model="formData.serviceSubject" placeholder="请选择">
                <el-option label="本人案件" value="本人案件" />
                <el-option label="家人案件" value="家人案件" />
                <el-option label="公司案件" value="公司案件" />
                <el-option label="委托代理案件" value="委托代理案件" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 4. 案件描述模块 (婚姻家庭专属) -->
      <el-card class="form-section" shadow="never">
        <template #header>4. 婚姻家事信息</template>
        <el-form-item label="业务方向" prop="businessDirection">
          <el-select v-model="formData.businessDirection" placeholder="请选择">
            <el-option label="离婚咨询" value="离婚咨询" />
            <el-option label="财产分割" value="财产分割" />
            <el-option label="子女抚养权" value="子女抚养权" />
            <el-option label="抚养费纠纷" value="抚养费纠纷" />
            <el-option label="婚前财产协议" value="婚前财产协议" />
            <el-option label="家暴维权" value="家暴维权" />
            <el-option label="遗产继承" value="遗产继承" />
            <el-option label="赡养纠纷" value="赡养纠纷" />
          </el-select>
        </el-form-item>
        <el-form-item label="婚姻状态" prop="marriageStatus">
          <el-select v-model="formData.marriageStatus" placeholder="请选择">
            <el-option label="已婚" value="已婚" />
            <el-option label="分居" value="分居" />
            <el-option label="准备离婚" value="准备离婚" />
            <el-option label="已起诉离婚" value="已起诉离婚" />
          </el-select>
        </el-form-item>
        <el-form-item label="核心诉求" prop="coreDemand">
          <el-checkbox-group v-model="formData.coreDemand">
            <div class="checkbox-list">
              <el-checkbox label="wantDivorce">想离婚</el-checkbox>
              <el-checkbox label="childCustody">争取孩子抚养权</el-checkbox>
              <el-checkbox label="propertyDivision">财产分割</el-checkbox>
              <el-checkbox label="infidelityEvidence">对方出轨证据处理</el-checkbox>
              <el-checkbox label="domesticViolenceRights">家暴维权</el-checkbox>
            </div>
          </el-checkbox-group>
        </el-form-item>
        
        <el-divider>财产情况说明</el-divider>
        
        <el-form-item label="房产" prop="propertyHouse">
          <el-input v-model="formData.propertyHouse" placeholder="数量、位置、价值等信息" />
        </el-form-item>
        <el-form-item label="车辆" prop="propertyVehicle">
          <el-input v-model="formData.propertyVehicle" placeholder="车型、价值等信息" />
        </el-form-item>
        <el-form-item label="存款" prop="propertyDeposit">
          <el-input v-model="formData.propertyDeposit" placeholder="金额及账户信息（可保密）" />
        </el-form-item>
        <el-form-item label="债务" prop="propertyDebt">
          <el-input v-model="formData.propertyDebt" placeholder="债务类型、金额等信息" />
        </el-form-item>
        <el-form-item label="其他资产" prop="propertyOther">
          <el-input v-model="formData.propertyOther" placeholder="其他重要资产信息" />
        </el-form-item>
        
        <el-divider>子女情况</el-divider>
        
        <el-form-item label="是否有子女" prop="hasChildren">
          <el-radio-group v-model="formData.hasChildren">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="子女人数" prop="childrenCount" v-if="formData.hasChildren">
          <el-input-number v-model="formData.childrenCount" :min="1" />
        </el-form-item>
        <el-form-item label="子女年龄" prop="childrenAge" v-if="formData.hasChildren">
          <el-input v-model="formData.childrenAge" placeholder="如：6岁、12岁、18岁等" />
        </el-form-item>
      </el-card>

      <!-- 5. 证据材料上传 -->
      <el-card class="form-section" shadow="never">
        <template #header>5. 证据材料上传 (Word/PDF及其他文件)</template>
        <el-upload
          v-model:file-list="formData.evidenceFiles"
          action="#"
          list-type="text"
          :auto-upload="false"
          multiple
        >
          <el-button class="custom-action-btn">点击上传文件</el-button>
          <template #tip>
            <div>支持上传：结婚证、离婚协议、财产证明、聊天记录、转账记录、证据照片等</div>
            <div style="color: #F56C6C; margin-top: 8px;">注意：身份证不再重复上传（已实名认证）</div>
          </template>
        </el-upload>
      </el-card>

      <!-- 6. 隐私与授权确认 -->
      <el-card class="form-section required-section" shadow="never">
        <template #header>6. 隐私与授权确认</template>
        <el-form-item prop="agreeTerms" label-width="0">
          <el-checkbox-group v-model="formData.agreeTerms">
            <div class="checkbox-list">
              <el-checkbox label="privacy">我已阅读并同意《隐私保护协议》</el-checkbox>
              <el-checkbox label="service">我已阅读并同意《法律服务协议》</el-checkbox>
              <el-checkbox label="truthful">我确认提交的信息真实有效</el-checkbox>
              <el-checkbox label="authorize">我授权平台将案件信息提供给承办律师</el-checkbox>
            </div>
          </el-checkbox-group>
        </el-form-item>
      </el-card>

      <!-- 7. 风险提示 -->
      <el-alert
        title="风险提示"
        type="warning"
        description="平台提供法律服务撮合，不直接提供司法裁判结果保证。律师意见仅基于现有材料进行专业判断，最终结果以司法机关认定为准。"
        show-icon
        :closable="false"
        class="risk-alert"
      />

      <div class="form-actions">
        <el-button class="custom-action-btn" size="large" @click="submitForm">提交申请</el-button>
        <el-button size="large" @click="resetForm">重置</el-button>
      </div>

    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'

import { useRegionOptions } from './useRegionOptions'
import { submitOrderForm } from './submitOrder'
import { applyCurrentUserInfo } from './useCurrentUserInfo'
import {
  commonOrderRules,
  requiredArrayRule,
  requireAllTermsRule
} from './validationRules'

const formRef = ref(null)
const { regionOptions } = useRegionOptions()

// 自动带出的信息及表单数据
const formData = reactive({
  realName: '',
  phone: '',
  idCard: '',
  accountType: '个人用户',
  verified: false,
  verifiedText: '未认证',
  businessType: '婚姻家事',
  
  wechat: '',
  email: '',
  emergencyContact: '',
  emergencyPhone: '',
  
  region: [],
  isUrgent: '普通',
  expectedDate: '',
  budget: '',
  serviceSubject: '本人案件',
  
  businessDirection: '',
  marriageStatus: '',
  coreDemand: [],
  propertyHouse: '',
  propertyVehicle: '',
  propertyDeposit: '',
  propertyDebt: '',
  propertyOther: '',
  hasChildren: false,
  childrenCount: 1,
  childrenAge: '',
  evidenceFiles: [],
  
  agreeTerms: []
})


const rules = {
  ...commonOrderRules,
  region: [requiredArrayRule('请选择所在地区')],
  businessDirection: [{ required: true, message: '请选择业务方向', trigger: 'change' }],
  marriageStatus: [{ required: true, message: '请选择婚姻状态', trigger: 'change' }],
  coreDemand: [requiredArrayRule('请至少选择一项核心诉求')],
  agreeTerms: [requireAllTermsRule]
}

onMounted(() => applyCurrentUserInfo(formData))

const resetForm = () => {
  formRef.value?.resetFields()
  applyCurrentUserInfo(formData)
}

const submitForm = () => submitOrderForm({ formRef, formData, serviceTypeId: 105 })
</script>

<style scoped>
.form-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}
.form-section {
  margin-bottom: 20px;
}
.checkbox-list {
  display: flex;
  flex-direction: column;
}
.risk-alert {
  margin-bottom: 30px;
}
.form-actions {
  text-align: center;
  margin-top: 20px;
}
.custom-action-btn {
  background: #1a73e8 !important;
  color: white !important;
  border-radius: 30px !important;
  padding: 10px 24px !important;
  border: none !important;
  transition: all 0.2s !important;
}

.custom-action-btn:hover {
  background: #1557b0 !important;
  transform: translateY(-1px);
}

.custom-action-btn:disabled,
.custom-action-btn.is-disabled {
  opacity: 0.6 !important;
  cursor: not-allowed !important;
}
</style>
