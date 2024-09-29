<script setup>


import {useDark, useToggle} from "@vueuse/core";

useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light',
})

useDark({
  onChanged(dark) {
    useToggle(dark)
  }
})
</script>

<template>
  <router-view v-slot="{ Component }">
    <transition mode="out-in"  name="el-fade-in-linear">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style scoped>


</style>
