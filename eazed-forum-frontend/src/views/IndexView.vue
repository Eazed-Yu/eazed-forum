<script setup>
import Aside from "@/views/index/aside.vue";
import Header from "@/views/index/header.vue"

import {useStore} from "@/store/index.js";
import {ref} from "vue"
import {get} from "@/net/index.js"

const store = useStore()
const loading = ref(true)

get('api/user/info', (data) => {
  store.user = data
  loading.value = false
})




</script>

<template>
  <div v-loading="loading" class="main-container" element-loading-text="正在进入，请稍后...">
    <el-container v-if="!loading" style="height: 100vh;width: 100vw">
      <Aside/>
      <el-container>
        <el-header class="main-content-header">
          <Header/>
        </el-header>
        <el-main class="main-content">
          <el-scrollbar>
            <RouterView v-slot="{ Component }">
              <transition mode="out-in" name="el-fade-in-linear">
                <component :is="Component"/>
              </transition>
            </RouterView>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<style lang="less" scoped>
.main-container{
  height: 100%;
}
.main-content {
  .main-content-header {
    border-bottom: solid 1px var(--el-border-color);
  }
}

</style>