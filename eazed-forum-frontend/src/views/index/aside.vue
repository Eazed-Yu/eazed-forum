<script setup>
import {useRoute, useRouter} from "vue-router";
import {useStore} from "@/store/index.js";
import {
  Bell,
  ChatDotSquare,
  Collection,
  DataLine,
  Document,
  Files,
  Location,
  Lock,
  Monitor,
  Notification,
  Operation,
  Position,
  School,
  Umbrella,
  User
} from "@element-plus/icons-vue";

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
        class="aside-container unselectable"
        router
        @close="handleClose"
        @open="handleOpen"
        :default-openeds="['1', '2', '3']"
    >
      <el-sub-menu index="1">
        <template #title>
          <el-icon>
            <Location/>
          </el-icon>
          <span><b>校园论坛</b></span>
        </template>
        <el-menu-item index="/index">
          <template #title>
            <el-icon>
            <ChatDotSquare/>
          </el-icon>
            帖子广场
          </template>
        </el-menu-item>
        <el-menu-item>
          <template #title>
            <el-icon>
            <Bell/>
          </el-icon>
            失物招领
          </template>
        </el-menu-item>
        <el-menu-item>
          <template #title>
            <el-icon>
              <Notification/>
          </el-icon>
            校园活动
          </template>
        </el-menu-item>
        <el-menu-item>
          <template #title>
            <el-icon>
              <Umbrella/>
          </el-icon>
            表白墙
          </template>
        </el-menu-item>
        <el-menu-item>
          <template #title>
            <el-icon>
              <School/>
          </el-icon>
            海文考研
            <el-tag size="small" style="margin-left: 10px">合作机构</el-tag>
          </template>
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="2">
        <template #title>
          <el-icon>
            <Position/>
          </el-icon>
          <span><b>探索与发现</b></span>
        </template>
        <el-menu-item>
          <template #title>
            <el-icon>
              <Document/>
            </el-icon>
            成绩查询
          </template>
        </el-menu-item>
        <el-menu-item>
          <template #title>
            <el-icon>
              <Files/>
            </el-icon>
            班级课程表
          </template>
        </el-menu-item>
        <el-menu-item>
          <template #title>
            <el-icon>
              <Monitor/>
            </el-icon>
            教务通知
          </template>
        </el-menu-item>
        <el-menu-item>
          <template #title>
            <el-icon>
              <Collection/>
            </el-icon>
            在线图书馆
          </template>
        </el-menu-item>
        <el-menu-item>
          <template #title>
            <el-icon>
              <DataLine/>
            </el-icon>
            预约教室
          </template>
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="3">
        <template #title>
          <el-icon>
            <Operation/>
          </el-icon>
          <span><b>个人设置</b></span>
        </template>
        <el-menu-item index="/index/user-setting">
          <template #title>
            <el-icon>
              <User/>
            </el-icon>
            个人信息设置
          </template>
        </el-menu-item>
        <el-menu-item index="/index/privacy-setting">
          <template #title>
            <el-icon>
              <Lock/>
            </el-icon>
            账号安全设置
          </template>
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