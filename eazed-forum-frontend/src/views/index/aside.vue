<script setup>
import {useRoute, useRouter} from "vue-router";
import {useStore} from "@/store/index.js";
import {Bell, ChatDotSquare, Location} from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();
const store = useStore();

const handleOpen = () => {

}
const handleClose = () => {

}

</script>

<template>
  <el-scrollbar>
    <el-menu
        :collapse="store.menu.isCollapse"
        :default-active="route.path"
        class="aside-container"
        router
        @close="handleClose"
        @open="handleOpen"
    >
      <div class="logo" style="display: flex; align-items: center; justify-content: center;">
        <el-icon size="20">
          <Location/>
        </el-icon>
        <p v-if="!store.menu.isCollapse" class="logo-lg unselectable">xx论坛</p>
      </div>
      <el-sub-menu class="unselectable" index="1">
        <template #title>
          <el-icon size="20">
            <Location/>
          </el-icon>
          <span><b>校园论坛</b></span>
        </template>
        <el-menu-item index="/index/">
          <el-icon size="20">
            <ChatDotSquare/>
          </el-icon>
          <span>帖子广场</span>
        </el-menu-item>
        <el-menu-item index="1-2">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>失物招领</span>
        </el-menu-item>
        <el-menu-item index="1-3">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>校园活动</span>
        </el-menu-item>
        <el-menu-item index="1-4">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>表白墙</span>
        </el-menu-item>
        <el-menu-item index="1-5">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>海文考研</span>
          <el-tag size="small" style="margin-left: 10px">合作机构</el-tag>
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu class="unselectable" index="2">
        <template #title>
          <el-icon size="20">
            <Location/>
          </el-icon>
          <span><b>探索与发现</b></span>
        </template>
        <el-menu-item index="2-1">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>成绩查询</span>
        </el-menu-item>
        <el-menu-item index="2-2">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>班级课表</span>
        </el-menu-item>
        <el-menu-item index="2-3">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>教务通知</span>
        </el-menu-item>
        <el-menu-item index="2-4">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>在线图书馆</span>
        </el-menu-item>
        <el-menu-item index="2-5">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>预约自习室</span>
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu class="unselectable" index="3">
        <template #title>
          <el-icon size="20">
            <Location/>
          </el-icon>
          <span><b>个人设置</b></span>
        </template>
        <el-menu-item index="/index/user-setting">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="/index/privacy-setting">
          <el-icon size="20">
            <Bell/>
          </el-icon>
          <span>账号安全</span>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </el-scrollbar>
</template>

<style lang="less" scoped>
.aside-container {
  background-color: var(--el-bg-color);
  min-height: 100vh;

  .logo {
    height: 50px;
    transition: all 0.5s ease;


    .logo-lg {
      color: var(--el-text-color-primary);
      font-size: 20px;

      white-space: nowrap;
      overflow: hidden;
    }
  }
}

.aside-container:not(.el-menu--collapse) {
  width: 220px;
}


</style>