<script setup>

defineProps({
  color: String
})
</script>

<template>
  <div :style="{background: color}" class="dot">

  </div>
</template>

<style lang="less" scoped>
.dot {
  width: 10px;
  height: 10px;
  border-radius: 5px;
  display: inline-block;
}

</style>