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
              <el-tag type="success">已认证</el-tag>
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

      <!-- 4. 案件描述模块 (在线咨询专属) -->
      <el-card class="form-section" shadow="never">
        <template #header>4. 图文咨询信息</template>
        <el-form-item label="咨询类型" prop="consultationType">
          <el-select v-model="formData.consultationType" placeholder="请选择">
            <el-option label="劳动纠纷" value="劳动纠纷" />
            <el-option label="婚姻家事" value="婚姻家事" />
            <el-option label="债权债务" value="债权债务" />
            <el-option label="合同纠纷" value="合同纠纷" />
            <el-option label="刑事风险" value="刑事风险" />
            <el-option label="房产纠纷" value="房产纠纷" />
            <el-option label="交通事故" value="交通事故" />
            <el-option label="公司法务" value="公司法务" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题标题" prop="problemTitle">
          <el-input v-model="formData.problemTitle" placeholder="简要概括咨询问题" />
        </el-form-item>
        <el-form-item label="问题详细描述" prop="problemDescription">
          <el-input 
            v-model="formData.problemDescription" 
            type="textarea" 
            rows="6"
            placeholder="请详细描述问题情况" 
          />
        </el-form-item>
        <el-form-item label="希望获得的帮助" prop="helpExpected">
          <el-checkbox-group v-model="formData.helpExpected">
            <div class="checkbox-list">
              <el-checkbox label="legalAdvice">法律意见</el-checkbox>
              <el-checkbox label="riskAnalysis">风险分析</el-checkbox>
              <el-checkbox label="solutionSuggestion">处理方案建议</el-checkbox>
              <el-checkbox label="worthSuing">是否值得起诉</el-checkbox>
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
            <div>支持上传：合同文件、起诉状、判决书、聊天记录、转账记录、证据照片等</div>
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
        <el-button size="large">重置</el-button>
      </div>

    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

import { useRegionOptions } from './useRegionOptions'

const formRef = ref(null)
const { regionOptions } = useRegionOptions()

// 自动带出的信息及表单数据
const formData = reactive({
  realName: '张三',
  phone: '138****0000',
  idCard: '370************1234',
  accountType: '个人用户',
  businessType: '在线咨询',
  
  wechat: '',
  email: '',
  emergencyContact: '',
  emergencyPhone: '',
  
  region: [],
  isUrgent: '普通',
  expectedDate: '',
  budget: '',
  serviceSubject: '本人案件',
  
  consultationType: '',
  problemTitle: '',
  problemDescription: '',
  helpExpected: [],
  
  agreeTerms: []
})


const rules = {
  region: [{ required: true, message: '请选择所在地区', trigger: 'change' }],
  consultationType: [{ required: true, message: '请选择咨询类型', trigger: 'change' }],
  problemTitle: [{ required: true, message: '请输入问题标题', trigger: 'blur' }],
  problemDescription: [{ required: true, message: '请输入问题详细描述', trigger: 'blur' }],
  helpExpected: [{ 
    type: 'array', 
    required: true, 
    message: '请至少选择一项希望获得的帮助',
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

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      console.log('提交的表单数据:', formData)
      // 处理提交逻辑
    }
  })
}
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
