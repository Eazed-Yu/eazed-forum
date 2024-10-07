<script setup>

import Card from "@/components/Card.vue";
import {Calendar, CollectionTag, EditPen, Link} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive} from "vue";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";


const weather = reactive({
  location: '',
  now: {},
  hourly: [],
  success: false
})

const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
})


navigator.geolocation.getCurrentPosition((position) => {
  const longitude = position.coords.longitude
  const latitude = position.coords.latitude
  get(`api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    weather.success = true
    Object.assign(weather, data)
  }, error => {
    console.log(error)
    ElMessage.success('获取天气信息失败')
    get('api/forum/weather?longitude=116.407526&latitude=39.90403', data => {
      weather.success = true
      Object.assign(weather, data)
    })
  }, {
    timeout: 5000,
    enableHighAccuracy: true
  })
})

</script>

<template>
  <div class="topic-list">
    <div class="topic-left">
      <card>
        <div class="create-topic unselectable">
          <el-icon style="margin-right: 10px">
            <EditPen/>
          </el-icon>
          点击发表新话题...
        </div>
      </card>
      <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <card v-for="item in 10" style="height: 100px;">

        </card>
      </div>
    </div>
    <div class="topic-right">
      <div class="topic-right-content">
        <card class="announcement">
          <div style="font-weight: bold">
            <el-icon>
              <CollectionTag/>
            </el-icon>
            论坛公告
          </div>
          <el-divider/>
          <div style="font-size: 14px;margin: 10px;color: var(--el-text-color-secondary)">
            星期二（10月1日）是中国国庆日，
            俄罗斯总统普京和朝鲜领导人金正恩当天都向中国国家主席习近平致贺电，
            祝贺中华人民共和国成立75周年，普京更称习近平为“亲爱的朋友”。
          </div>
        </card>
        <card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon>
              <Calendar/>
            </el-icon>
            天气信息
          </div>
          <el-divider/>
          <weather :data="weather"/>
        </card>

        <card style="margin-top: 20px;">
          <div style="display: flex;justify-content: space-between;">
            <div style="color: var(--el-text-color-primary)">当前日期</div>
            <div style="color: var(--el-text-color-secondary)">{{ today }}</div>
          </div>
        </card>
        <div style="margin-top: 20px">
          <el-icon>
            <Link/>
          </el-icon>
          友情连接
          <el-divider/>
        </div>
        <div style="display: grid;grid-template-columns: repeat(2, 1fr);grid-gap: 10px;margin-top: 10px">

        </div>
      </div>
    </div>
  </div>

</template>

<style lang="less" scoped>
.topic-list {

  display: flex;
  margin: 20px auto;
  gap: 20px;
  max-width: 900px;
  min-height: 100vh;

  .topic-left {
    flex: 1;

    .create-topic {
      background-color: var(--el-bg-color-page);
      height: 40px;
      border-radius: 15px;
      line-height: 40px;
      padding: 0 10px;
      color: var(--el-text-color-secondary);

      &:hover {
        cursor: pointer;
      }
    }
  }

  .topic-right {
    width: 280px;

    .topic-right-content {
      position: sticky;
      top: 20px;

    }
  }

}

</style>