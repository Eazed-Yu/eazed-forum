<script setup>

</script>

<template>
  <div style = "width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style = "flex: 1;background-color: black">
      <el-image fit = "cover" src = "https://pbs.twimg.com/media/EtSfHpjVkAEWlG6.jpg"
          style="width: 100%;height: 100%"/>
      <div class="welcome-title">
        <div style="font-size: 30px;font-weight: bold">欢迎来到我们的网站</div>
        <div style="margin-top: 10px">这是一个基于Vue3.0的前端项目</div>
        <div style="margin-top: 5px">用于展示如何使用Vue3.0进行前端开发</div>
      </div>
    </div>

    <div class = "right-card">
      <router-view v-slot="{ Component }">
        <transition mode="out-in"  name="el-fade-in-linear">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </div>

</template>

<style scoped>
.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}

.right-card {
  width: 600px;
  z-index: 1;
  background-color: white;

}

</style>