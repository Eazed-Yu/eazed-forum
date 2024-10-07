<script setup>
defineProps({
  icon: Object,
  title: String,
  description: String
})

</script>

<template>
  <div class="card" style="background-color: var(--el-bg-color)">
    <div v-if="title" class="card-header">
      <div class="title">
        <el-icon size="15" style="translate: 0 2px">
          <component :is="icon"/>
        </el-icon>
        {{ title }}
      </div>
      <div class="description">{{ description }}
      </div>
    </div>
    <slot/>
  </div>

</template>

<style lang="less" scoped>
.card {

  min-height: 20px;
  padding: 10px;
  .card-header {

    border-bottom: solid 1px var(--el-border-color);
    padding-bottom: 5px;
    margin-bottom: 10px;

    .title {
      font-size: 18px;
      font-weight: bold;
      color: var(--el-text-color-primary);
    }

    .description {
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }

  }
}

</style>