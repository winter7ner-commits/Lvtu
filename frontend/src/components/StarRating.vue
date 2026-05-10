<template>
  <div class="star-rating">
    <span 
      v-for="star in max" 
      :key="star"
      @click="handleClick(star)"
      @mouseover="handleHover(star)"
      @mouseout="handleHover(0)"
      :class="['star', { 'filled': star <= (hoverStar || value) }]"
    >
      ★
    </span>
    <span class="score-text">{{ value }}/5</span>
  </div>
</template>

<script>
export default {
  props: {
    value: {
      type: Number,
      default: 0
    },
    max: {
      type: Number,
      default: 5
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      hoverStar: 0
    };
  },
  methods: {
    handleClick(star) {
      if (this.readOnly) return;
      this.$emit('update:value', star);
      this.hoverStar = 0;
    },
    handleHover(star) {
      if (this.readOnly) return;
      this.hoverStar = star;
    }
  }
}
</script>

<style scoped>
.star-rating {
  display: inline-flex;
  align-items: center;
}

.star {
  font-size: 24px;
  cursor: pointer;
  color: #ddd;
}

.star.filled {
  color: #ffc107;
}

.score-text {
  margin-left: 8px;
  font-size: 14px;
  color: #555;
}
</style>
