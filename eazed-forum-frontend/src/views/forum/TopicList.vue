<script setup>

import Card from "@/components/Card.vue";
import {
  ArrowRightBold,
  Calendar,
  CircleCheck,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen,
  FolderOpened,
  Link,
  Microphone,
  Picture,
  Plus,
  Star
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store/index.js";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import {useRouter} from "vue-router";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";

const collects = ref(false)
const router = useRouter()
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
  end: false,
  top: []
})

get('/api/forum/top-topic', data => topics.top = data)
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
      <card style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <div v-for="item in topics.top" class="top-topic" @click="router.push(`/index/topic-detail/${item.id}`)">
          <el-tag size="small" type="info">置顶</el-tag>
          <div>{{ item.title }}</div>
          <div>{{ new Date(item.time).toLocaleDateString() }}</div>
        </div>
      </card>
      <card style="margin-top: 10px;display: flex;gap: 7px;justify-content: center">
        <div v-for="item in store.forum.selectType"
             :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
             style="display: flex; text-align: center; align-items: center; justify-content: center"
             @click="topics.type = item.id"
        >
          <color-dot :color="item.color" style="align-self: center; justify-self: center; text-align: center"/>
          <div>{{ item.name }}</div>
        </div>
      </card>
      <transition mode="out-in" name="el-fade-in">
        <div v-if="topics.list?.length">
          <div v-if="store.forum.types" v-infinite-scroll="updateList"
               style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
            <card v-for="item in topics.list" class="topic-card"
                  @click="router.push(`/index/topic-detail/` + item.id)"
            >
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
                <topic-tag :type="item.type"/>
                <span style="font-weight: bold;margin-left: 5px">{{ item.title }}</span>
              </div>
              <div class="topic-content">
                {{ item.text }}
              </div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image v-for="image in item.images" :src="image" class="topic-image" fit="cover"/>
              </div>
              <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px;opacity: 0.8">
                <div>
                  <el-icon style="vertical-align: middle">
                    <CircleCheck/>
                  </el-icon>
                  {{ item.like }}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle">
                    <Star/>
                  </el-icon>
                  {{ item.collect }}收藏
                </div>
              </div>
            </card>
          </div>
        </div>
      </transition>
      <div class="add-topic" @click="editor = true">
        <el-icon>
          <Plus/>
        </el-icon>
      </div>
    </div>
    <div class="topic-right">
      <div class="topic-right-content">
        <card style="margin-bottom: 10px">
          <div class="collect-list-button" @click="collects = true">
            <span><el-icon><FolderOpened/></el-icon> 查看我的收藏</span>
            <el-icon style="transform: translateY(3px)">
              <ArrowRightBold/>
            </el-icon>
          </div>
        </card>
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
    <topic-collect-list :show="collects" @close="collects = false"/>
  </div>

</template>

<style lang="less" scoped>
@media (max-width: 1100px) {
  .topic-right {
    display: none;
  }
}

.dark {
  .topic-list {
    .topic-left {
      @media (max-width: 600px) {
        .type-select-card {
          flex-direction: column;
        }
      }
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
  margin: 20px;
  gap: 20px;
  max-width: 1000px;
  min-height: 100vh;

  .topic-left {
    flex: 1;

    .add-topic {
      position: fixed;
      bottom: 20px;
      right: 20px;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      font-size: 18px;
      color: var(--el-color-primary);
      text-align: center;
      line-height: 45px;
      background: var(--el-bg-color-overlay);
      box-shadow: var(--el-box-shadow-lighter);

      &:hover {
        background: var(--el-border-color-extra-light);
        cursor: pointer;
      }
    }

    .top-topic {
      display: flex;

      div:first-of-type {
        font-size: 14px;
        margin-left: 10px;
        font-weight: bold;
        opacity: 0.8;
        transition: color .3s;

        &:hover {
          color: grey;
        }
      }

      div:nth-of-type(2) {
        flex: 1;
        color: grey;
        font-size: 13px;
        text-align: right;
      }

      &:hover {
        cursor: pointer;
      }
    }

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

      .collect-list-button {
        font-size: 14px;
        display: flex;
        justify-content: space-between;
        transition: .3s;

        &:hover {
          cursor: pointer;
          opacity: 0.6;
        }
      }
    }
  }

}


</style>