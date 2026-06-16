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

      <!-- 4. 案件描述模块 (文件撰写专属) -->
      <el-card class="form-section" shadow="never">
        <template #header>4. 文件撰写需求信息</template>
        <el-form-item label="文书类型" prop="documentType">
          <el-select v-model="formData.documentType" placeholder="请选择">
            <el-option label="起诉状" value="起诉状" />
            <el-option label="答辩状" value="答辩状" />
            <el-option label="离婚协议书" value="离婚协议书" />
            <el-option label="律师函" value="律师函" />
            <el-option label="催款函" value="催款函" />
            <el-option label="借条" value="借条" />
            <el-option label="欠条" value="欠条" />
            <el-option label="遗嘱" value="遗嘱" />
            <el-option label="劳动仲裁申请书" value="劳动仲裁申请书" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="文书用途" prop="documentPurpose">
          <el-select v-model="formData.documentPurpose" placeholder="请选择">
            <el-option label="用于法院诉讼" value="用于法院诉讼" />
            <el-option label="用于协商谈判" value="用于协商谈判" />
            <el-option label="用于正式通知" value="用于正式通知" />
            <el-option label="用于留存证据" value="用于留存证据" />
          </el-select>
        </el-form-item>
        
        <el-divider>相关事实说明</el-divider>
        
        <el-form-item label="事件经过详细描述" prop="eventDescription">
          <el-input 
            v-model="formData.eventDescription" 
            type="textarea" 
            rows="6"
            placeholder="请详细描述事件的经过，包括时间、地点、人物、过程等" 
          />
        </el-form-item>
        <el-form-item label="对方姓名/公司名称" prop="otherPartyName">
          <el-input v-model="formData.otherPartyName" placeholder="请输入对方的真实名称" />
        </el-form-item>
        <el-form-item label="对方联系方式（可选）" prop="otherPartyContact">
          <el-input v-model="formData.otherPartyContact" placeholder="邮箱、电话等联系方式" />
        </el-form-item>
        
        <el-divider>特殊要求</el-divider>
        
        <el-form-item label="是否需要加急" prop="needUrgent">
          <el-radio-group v-model="formData.needUrgent">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否需要律师署名版" prop="needLawyerSignature">
          <el-radio-group v-model="formData.needLawyerSignature">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
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
            <div>支持上传：参考文件、案件相关材料、合同文件、证据照片等</div>
            <div style="color: #F56C6C; margin-top: 8px;">注意：身份证不再重复上传（已实名认证）</div>
          </template>
        </el-upload>
      </el-card>

      <!-- 6. 隐私与授权确认 -->
      <el-card class="form-section" shadow="never">
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
  requiredTrimmedRule,
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
  businessType: '文件撰写',
  
  wechat: '',
  email: '',
  emergencyContact: '',
  emergencyPhone: '',
  
  region: [],
  isUrgent: '普通',
  expectedDate: '',
  budget: '',
  serviceSubject: '本人案件',
  
  documentType: '',
  documentPurpose: '',
  eventDescription: '',
  otherPartyName: '',
  otherPartyContact: '',
  needUrgent: false,
  needLawyerSignature: false,
  evidenceFiles: [],
  
  agreeTerms: []
})


const rules = {
  ...commonOrderRules,
  region: [requiredArrayRule('请选择所在地区')],
  documentType: [{ required: true, message: '请选择文书类型', trigger: 'change' }],
  documentPurpose: [{ required: true, message: '请选择文书用途', trigger: 'change' }],
  eventDescription: [requiredTrimmedRule('请输入事件经过详细描述')],
  otherPartyName: [requiredTrimmedRule('请输入对方姓名/公司名称')],
  agreeTerms: [requireAllTermsRule]
}

onMounted(() => applyCurrentUserInfo(formData))

const resetForm = () => {
  formRef.value?.resetFields()
  applyCurrentUserInfo(formData)
}

const submitForm = () => submitOrderForm({ formRef, formData, serviceTypeId: 103 })
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
