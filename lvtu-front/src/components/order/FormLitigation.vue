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
              <el-input v-model="formData.email" placeholder="可选" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="formData.emergencyContact" placeholder="可选" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急电话" prop="emergencyPhone">
              <el-input v-model="formData.emergencyPhone" placeholder="可选" />
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
              <el-input v-model="formData.budget" placeholder="请输入预算金额（元）" />
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

      <!-- 4. 案件描述模块 (诉讼专属) -->
      <el-card class="form-section" shadow="never">
        <template #header>4. 诉讼案件信息</template>
        <el-form-item label="案件类型" prop="caseType">
          <el-select v-model="formData.caseType" placeholder="请选择">
            <el-option label="民事诉讼" value="民事诉讼" />
            <el-option label="刑事案件" value="刑事案件" />
            <el-option label="行政诉讼" value="行政诉讼" />
            <el-option label="劳动仲裁" value="劳动仲裁" />
            <el-option label="商事纠纷" value="商事纠纷" />
            <el-option label="债务追偿" value="债务追偿" />
            <el-option label="交通事故" value="交通事故" />
            <el-option label="房产纠纷" value="房产纠纷" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前阶段" prop="currentStage">
          <el-select v-model="formData.currentStage" placeholder="请选择">
            <el-option label="准备起诉" value="准备起诉" />
            <el-option label="已经立案" value="已经立案" />
            <el-option label="一审中" value="一审中" />
            <el-option label="二审中" value="二审中" />
            <el-option label="执行阶段" value="执行阶段" />
          </el-select>
        </el-form-item>
        <el-form-item label="涉案金额" prop="caseAmount">
          <el-input v-model="formData.caseAmount" placeholder="请输入金额（元）" />
        </el-form-item>
        
        <el-divider>对方信息</el-divider>
        
        <el-form-item label="对方姓名/公司名称" prop="oppositePartyName">
          <el-input v-model="formData.oppositePartyName" placeholder="请输入对方的真实名称" />
        </el-form-item>
        <el-form-item label="是否已有律师" prop="hasLawyer">
          <el-radio-group v-model="formData.hasLawyer">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-divider>已有材料</el-divider>
        
        <el-form-item label="已有材料" prop="existingMaterials">
          <el-checkbox-group v-model="formData.existingMaterials">
            <div class="checkbox-list">
              <el-checkbox label="indictment">起诉状</el-checkbox>
              <el-checkbox label="judgment">判决书</el-checkbox>
              <el-checkbox label="evidence">证据材料</el-checkbox>
              <el-checkbox label="arbitrationAward">仲裁裁决书</el-checkbox>
              <el-checkbox label="other">其他</el-checkbox>
            </div>
          </el-checkbox-group>
        </el-form-item>
        
        <el-divider>期望律师服务</el-divider>
        
        <el-form-item label="期望律师服务" prop="expectedService">
          <el-checkbox-group v-model="formData.expectedService">
            <div class="checkbox-list">
              <el-checkbox label="fullRepresentation">全程代理</el-checkbox>
              <el-checkbox label="singleAppearance">单次出庭</el-checkbox>
              <el-checkbox label="legalAdvice">法律意见</el-checkbox>
              <el-checkbox label="executionRepresentation">执行代理</el-checkbox>
            </div>
          </el-checkbox-group>
        </el-form-item>
      </el-card>

      <!-- 5. 证据材料上传 -->
      <el-card class="form-section" shadow="never">
        <template #header>5. 证据材料上传 (Word/PDF及其他文件)</template>
        <el-upload
          action="#"
          list-type="text"
          :auto-upload="false"
          multiple
        >
          <el-button class="custom-action-btn">点击上传文件</el-button>
          <template #tip>
            <div>支持上传：起诉状、判决书、仲裁裁决书、聊天记录、转账记录、证据照片、律师函等</div>
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
  businessType: '诉讼',
  
  wechat: '',
  email: '',
  emergencyContact: '',
  emergencyPhone: '',
  
  region: [],
  isUrgent: '普通',
  expectedDate: '',
  budget: '',
  serviceSubject: '本人案件',
  
  caseType: '',
  currentStage: '',
  caseAmount: '',
  oppositePartyName: '',
  hasLawyer: false,
  existingMaterials: [],
  expectedService: [],
  
  agreeTerms: []
})


const rules = {
  region: [{ required: true, message: '请选择所在地区', trigger: 'change' }],
  caseType: [{ required: true, message: '请选择案件类型', trigger: 'change' }],
  currentStage: [{ required: true, message: '请选择当前阶段', trigger: 'change' }],
  oppositePartyName: [{ required: true, message: '请输入对方姓名/公司名称', trigger: 'blur' }],
  existingMaterials: [{ 
    type: 'array', 
    required: true, 
    message: '请至少选择一项已有材料',
    trigger: 'change' 
  }],
  expectedService: [{ 
    type: 'array', 
    required: true, 
    message: '请至少选择一项期望的律师服务',
    trigger: 'change' 
  }],
  agreeTerms: [{ 
    type: 'array', 
    required: true, 
    validator: (rule, value, callback) => {
      if (value.length < 4) {
        callback(new Error('请阅读并勾选所有的授权与确认项'))
      } else {
        callback()
      }
    },
    trigger: 'change' 
  }]
}

onMounted(() => applyCurrentUserInfo(formData))

const resetForm = () => {
  formRef.value?.resetFields()
  applyCurrentUserInfo(formData)
}

const submitForm = () => submitOrderForm({ formRef, formData, serviceTypeId: 106 })
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
