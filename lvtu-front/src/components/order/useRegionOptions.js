import { onMounted, ref } from 'vue'
import { getRegionTree } from '../../api/origin'

let cachedRegionOptions = null
let pendingRequest = null

const fallbackRegionOptions = [
  { value: 1, label: '\u5317\u4eac\u5e02', children: [{ value: 35, label: '\u5317\u4eac\u5e02' }] },
  { value: 2, label: '\u5929\u6d25\u5e02', children: [{ value: 36, label: '\u5929\u6d25\u5e02' }] },
  { value: 3, label: '\u6cb3\u5317\u7701', children: [{ value: 37, label: '\u77f3\u5bb6\u5e84\u5e02' }] },
  { value: 9, label: '\u4e0a\u6d77\u5e02', children: [{ value: 102, label: '\u4e0a\u6d77\u5e02' }] },
  { value: 10, label: '\u6c5f\u82cf\u7701', children: [{ value: 103, label: '\u5357\u4eac\u5e02' }, { value: 107, label: '\u82cf\u5dde\u5e02' }] },
  { value: 11, label: '\u6d59\u6c5f\u7701', children: [{ value: 116, label: '\u676d\u5dde\u5e02' }, { value: 117, label: '\u5b81\u6ce2\u5e02' }] },
  { value: 15, label: '\u5c71\u4e1c\u7701', children: [{ value: 163, label: '\u6d4e\u5357\u5e02' }, { value: 164, label: '\u9752\u5c9b\u5e02' }] },
  { value: 17, label: '\u6e56\u5317\u7701', children: [{ value: 197, label: '\u6b66\u6c49\u5e02' }] },
  { value: 18, label: '\u6e56\u5357\u7701', children: [{ value: 209, label: '\u957f\u6c99\u5e02' }] },
  { value: 19, label: '\u5e7f\u4e1c\u7701', children: [{ value: 222, label: '\u5e7f\u5dde\u5e02' }, { value: 223, label: '\u6df1\u5733\u5e02' }] },
  { value: 22, label: '\u91cd\u5e86\u5e02', children: [{ value: 261, label: '\u91cd\u5e86\u5e02' }] },
  { value: 23, label: '\u56db\u5ddd\u7701', children: [{ value: 262, label: '\u6210\u90fd\u5e02' }] },
  { value: 27, label: '\u9655\u897f\u7701', children: [{ value: 300, label: '\u897f\u5b89\u5e02' }] }
]

function normalizeRegionOptions(regions = []) {
  return regions
    .map((region) => {
      const value = region.value ?? region.regionId
      const label = region.label ?? region.regionName
      if (value == null || !label) return null

      const children = normalizeRegionOptions(region.children || [])
      const option = { value, label }
      if (children.length > 0) option.children = children
      return option
    })
    .filter(Boolean)
}

async function loadRegionOptions() {
  if (cachedRegionOptions) return cachedRegionOptions

  if (!pendingRequest) {
    pendingRequest = getRegionTree()
      .then((response) => {
        const options = normalizeRegionOptions(response.data?.data || [])
        cachedRegionOptions = options.length > 0 ? options : fallbackRegionOptions
        return cachedRegionOptions
      })
      .catch((error) => {
        console.warn('Region API unavailable, using fallback region options.', error)
        cachedRegionOptions = fallbackRegionOptions
        return cachedRegionOptions
      })
      .finally(() => {
        pendingRequest = null
      })
  }

  return pendingRequest
}

export function useRegionOptions() {
  const regionOptions = ref(cachedRegionOptions || fallbackRegionOptions)
  const regionLoading = ref(false)

  onMounted(async () => {
    regionLoading.value = true
    try {
      regionOptions.value = await loadRegionOptions()
    } finally {
      regionLoading.value = false
    }
  })

  return { regionOptions, regionLoading }
}
