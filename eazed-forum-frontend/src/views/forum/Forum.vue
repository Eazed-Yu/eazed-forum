<script setup>
import {get} from "@/net/index.js";
import {useStore} from "@/store/index.js";

const store = useStore()

// 获取帖子类型
get('/api/forum/types', data => {
  const array = []
  array.push({
    name: '全部',
    id: 0,
    color: 'linear-gradient(45deg, white, red, orange, gold, green, blue)'
  })
  data.forEach(item => {
    array.push(item)
  })
  store.forum.types = data
  store.forum.selectType = array
})
// 获取置顶帖子


</script>

<template>
  <div>
    <router-view v-slot="{ Component }">
      <transition mode="out-in" name="el-fade-in-linear">
        <keep-alive include="TopicList">
          <component :is="Component"/>
        </keep-alive>
      </transition>
    </router-view>
    <el-backtop :bottom="70" :right="20" target=".main-content-page .el-scrollbar__wrap"/>
  </div>
</template>

<style lang="less" scoped>

</style>