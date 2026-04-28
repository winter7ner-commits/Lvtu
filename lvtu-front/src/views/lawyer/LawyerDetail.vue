<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getLawyerDetail } from '@/api/lawyer'

const route = useRoute()
const detail = ref(null)

onMounted(() => {
  const id = route.params.id
  getLawyerDetail(id).then(res => {
    detail.value = res.data
  })
})
</script>

<template>
  <div v-if="detail">
    <h2>{{ detail.name }}</h2>

    <p>ID：{{ detail.lawyerId }}</p>
    <p>事务所：{{ detail.lawFirm }}</p>
    <p>执业年限：{{ detail.practiceYears }} 年</p>
    <p>评分：⭐{{ detail.rating }}</p>

    <p>擅长领域：
      <span v-for="specialty in detail.specialties" :key="specialty.id || specialty.specialtyId">
        {{ specialty.name }}
      </span>
    </p>
  </div>
</template>