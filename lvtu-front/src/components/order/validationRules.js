const normalizeValue = (value) => String(value ?? '').trim()

const createOptionalPatternRule = (pattern, message) => ({
  validator: (_rule, value, callback) => {
    const normalized = normalizeValue(value)
    if (!normalized || pattern.test(normalized)) {
      callback()
      return
    }

    callback(new Error(message))
  },
  trigger: 'blur'
})

const createRequiredPositiveMoneyRule = () => ({
  validator: (_rule, value, callback) => {
    const normalized = normalizeValue(value)
    if (!normalized) {
      callback(new Error('请输入预估预算'))
      return
    }

    if (!/^\d+(?:\.\d{1,2})?$/.test(normalized) || Number(normalized) <= 0) {
      callback(new Error('预估预算必须大于0，且最多保留两位小数'))
      return
    }

    callback()
  },
  trigger: 'blur'
})

export const requiredTrimmedRule = (message) => ({
  validator: (_rule, value, callback) => {
    if (normalizeValue(value)) {
      callback()
      return
    }

    callback(new Error(message))
  },
  trigger: 'blur'
})

export const requiredArrayRule = (message) => ({
  type: 'array',
  required: true,
  validator: (_rule, value, callback) => {
    if (Array.isArray(value) && value.length > 0) {
      callback()
      return
    }

    callback(new Error(message))
  },
  trigger: 'change'
})

export const requireAllTermsRule = {
  type: 'array',
  required: true,
  validator: (_rule, value, callback) => {
    if (Array.isArray(value) && value.length >= 4) {
      callback()
      return
    }

    callback(new Error('请阅读并勾选所有的授权与确认项'))
  },
  trigger: 'change'
}

export const commonOrderRules = {
  wechat: [
    createOptionalPatternRule(
      /^(?!\d)[A-Za-z0-9_-]{6,20}$/,
      '微信号不能以数字开头，需为6-20位字母、数字、下划线或减号'
    )
  ],
  email: [
    createOptionalPatternRule(
      /^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.com$/,
      '邮箱格式应为 xxx@xxx.com'
    )
  ],
  emergencyContact: [
    createOptionalPatternRule(/^[\u4e00-\u9fa5]{2,6}$/, '紧急联系人需为2-6个汉字')
  ],
  emergencyPhone: [
    createOptionalPatternRule(/^1[3-9]\d{9}$/, '紧急电话需为11位手机号，且第二位为3-9')
  ],
  budget: [createRequiredPositiveMoneyRule()]
}
