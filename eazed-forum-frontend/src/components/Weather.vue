<script setup>
defineProps({
  data: Object
})
</script>

<template>
  <div v-loading="!data.success" element-loading-text="正在加载天气信息" style="height: 200px">
    <div v-if="data.success"
         style="display: flex;justify-content: space-between;margin: 10px 20px">
      <div style="font-size: 45px">
        <i :class="`qi-${data.now.icon}-fill`"/>
      </div>
      <div style="font-weight: bold;text-align: center">
        <div style="font-size: 25px">{{ data.now.temp }}°</div>
        <div>{{ data.now.text }}</div>
      </div>
      <div style="margin-top: 13px">
        <div style="font-size: 15px;color: var(--el-text-color-primary)">
          {{ `${data.location.country} ${data.location.adm1}` }}
        </div>
        <div style="margin-top: 5px;font-size: 14px;color: var(--el-text-color-secondary)">
          {{ `${data.location.adm2}市 ${data.location.name}区` }}
        </div>
      </div>
    </div>
    <el-divider/>
    <div style="display: grid;grid-template-columns: repeat(5, 1fr);text-align: center;height: 500px">
      <div v-for="item in data.hourly">
        <div style="font-size: 13px">{{ new Date(item.fxTime).getHours() }}时</div>
        <div style="font-size: 25px">
          <i :class="`qi-${item.icon}-fill`"/>
        </div>
        <div style="font-size: 12px">{{ item.temp }}°</div>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>

</style>