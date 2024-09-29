<script setup>
defineProps({
  icon: Object,
  title: String,
  description: String
})

</script>

<template>
  <div class="card">
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

<style scoped>
.card {
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

  border-radius: 5px;
  border: solid 1px var(--el-border-color);
  min-height: 20px;
  padding: 10px;

}
</style>