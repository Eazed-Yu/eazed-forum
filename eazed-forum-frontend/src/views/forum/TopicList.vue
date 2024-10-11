<script setup>

import Card from "@/components/Card.vue";
import {
  Calendar,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen,
  Link,
  Microphone,
  Picture
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store/index.js";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";

const store = useStore()
const weather = reactive({
  location: '',
  now: {},
  hourly: [],
  success: false
})
const editor = ref(false);
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  end: false
})


const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
})


// 获取天气信息
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
  })
})


function updateList() {
  if (topics.end) return
  get(`api/forum/list-topic?page=${topics.page}&type=${topics.type}`, data => {
    if (data && data.length > 0) {
      data.forEach(d => topics.list.push(d))
      topics.page++
    } else {
      topics.end = true
      ElMessage.info("没有更多帖子了")
    }
  })
}

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

function initList() {
  topics.page = 0;
  topics.list = []
  topics.end = false
  updateList()
}

watch(() => topics.type, () => {
  initList()
}, {immediate: true})

</script>

<template>
  <div class="topic-list">
    <div class="topic-left">
      <card>
        <div class="create-topic unselectable" @click="editor = true">
          <el-icon style="margin-right: 10px">
            <EditPen/>
          </el-icon>
          点击发表新话题...
        </div>
        <div style="margin-top: 10px;display: flex;gap:13px;font-size: 18px;color: grey">
          <el-icon>
            <Edit/>
          </el-icon>
          <el-icon>
            <Document/>
          </el-icon>
          <el-icon>
            <Compass/>
          </el-icon>
          <el-icon>
            <Picture/>
          </el-icon>
          <el-icon>
            <Microphone/>
          </el-icon>
        </div>
      </card>
      <card style="margin-top: 10px;display: flex;gap: 7px;justify-content: center">
        <div v-for="item in store.forum.selectType"
             :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
             @click="topics.type = item.id"
        >
          <color-dot :color="item.color"/>
          <span style="margin-left: 5px">{{ item.name }}</span>
        </div>
      </card>
      <transition mode="out-in" name="el-fade-in">
        <div v-if="topics.list?.length">
          <div v-if="store.forum.types" v-infinite-scroll="updateList"
               style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
            <card v-for="item in topics.list" class="topic-card">
              <div style="display: flex">
                <div>
                  <el-avatar :size="30" :src="`${axios.defaults.baseURL}/images${item.avatar}`"/>
                </div>
                <div style="margin-left: 7px;transform: translateY(-2px)">
                  <div style="font-size: 14px">{{ item.username }}</div>
                  <div style="font-size: 14px">
                    <el-icon>
                      <Clock/>
                    </el-icon>
                    <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                      {{ new Date(item.time).toLocaleString() }}
                    </div>
                  </div>
                </div>
              </div>
              <div style="margin-top: 5px">
                <div :style="{
              color: store.findTypeById(item.type)?.color + 'EE',
              'border-color': store.findTypeById(item.type)?.color + '77',
              'background-color': store.findTypeById(item.type)?.color + '33'
                 }"
                     class="topic-type">
                  {{ store.findTypeById(item.type)?.name }}
                </div>
                <span style="font-weight: bold;margin-left: 5px">{{ item.title }}</span>
              </div>
              <div class="topic-content">
                {{ item.text }}
              </div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image v-for="image in item.images" :src="image" class="topic-image" fit="cover"/>
              </div>
            </card>
          </div>
        </div>
      </transition>

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
    <topic-editor :show="editor" @close="editor = false" @success="editor = false;initList()"/>
  </div>

</template>

<style lang="less" scoped>
.dark {
  .topic-list {
    .topic-left {
      .type-select-card {
        background-color: #282828;

        &.active {
          border: solid 1px #64594b;
        }

        &:hover {
          background-color: #5e5e5e;
        }
      }
    }
  }

}
.topic-list {

  display: flex;
  margin: 20px auto;
  gap: 20px;
  max-width: 900px;
  min-height: 100vh;

  .topic-left {
    flex: 1;

    .type-select-card {
      transition: all 0.3s;
      background-color: #f5f6f5;
      padding: 2px 7px;
      font-size: 14px;
      border-radius: 3px;
      box-sizing: border-box;

      &.active {
        border: solid 1px #ead4c4;
      }

      &:hover {
        cursor: pointer;
        scale: 1.02;
        background-color: #dadada;
      }
    }

    .dark .type-select-card {
      background-color: #282828;

      &.active {
        border: solid 1px #64594b;
      }

      &:hover {
        background-color: #5e5e5e;
      }
    }


    .topic-card {
      transition: scale 0.2s;
      padding: 10px;

      &:hover {
        cursor: pointer;
        scale: 1.02;
      }

      .topic-content {
        margin: 5px 0;
        font-size: 14px;
        color: var(--el-text-color-secondary);
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 3;
        line-clamp: 3;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .topic-type {
        display: inline-block;
        border: solid 0.5px grey;
        border-radius: 10px;
        font-size: 16px;
        padding: 0 5px;
      }

      .topic-image {
        width: 100%;
        height: 100%;
        max-height: 110px;
        transition: scale 0.2s;

        &:hover {
          scale: 1.2;
          z-index: auto;
        }
      }

    }

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