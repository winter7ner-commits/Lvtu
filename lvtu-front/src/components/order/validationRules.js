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

export const commonOrderRules = {
  email: [
    createOptionalPatternRule(
      /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.com$/,
      '邮箱格式应为 xxx@xxx.com'
    )
  ],
  emergencyPhone: [
    createOptionalPatternRule(/^\d{11}$/, '紧急电话必须为11位数字')
  ],
  budget: [
    createOptionalPatternRule(
      /^\d+(?:\.\d{1,2})?$/,
      '预估预算不能为负数，且最多保留两位小数'
    )
  ]
}
