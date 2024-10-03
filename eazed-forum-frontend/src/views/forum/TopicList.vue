<script setup>

import Card from "@/components/Card.vue";
import {CollectionTag} from "@element-plus/icons-vue";
</script>

<template>
  <div class="topic-list">
    <div class="topic-left">
      <card class="editor">
        <div>

        </div>
      </card>
      <div style="margin-top: 10px">
        <card>

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

    .editor {
      background-color: var(--el-bg-color);
      height: 100px;
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