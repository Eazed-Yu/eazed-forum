<script setup>
import {Document} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue";
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import axios from "axios";
import {accessHeader} from "@/net/index.js";
import {ElMessage} from "element-plus";

defineProps({
  show: Boolean
})

const editor = reactive({
  type: null,
  title: '',
  text: '',
  loading: false
})


const types = [
  {id: 1, name: '日常闲聊', description: '日常闲聊，聊聊生活'},
  {id: 2, name: '真诚交友', description: '真诚交友，寻找知己'},
  {id: 3, name: '问题反馈', description: '问题反馈，建议意见'},
  {id: 4, name: '恋爱官宣', description: '恋爱官宣，晒晒幸福'},
  {id: 5, name: '踩坑记录', description: '踩坑记录，分享经验'}
]


const emit = defineEmits(['close'])


Quill.register('modules/imageResize', ImageResize);
Quill.register('modules/ImageExtend', ImageExtend);
const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike", "clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        {header: [1, 2, 3, 4, 5, 6, false]}, {list: "ordered"},
        {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        {indent: '-1'}, {indent: '+1'}
      ],
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      }
    },
    imageResize: {
      modules: ['Resize', 'DisplaySize', 'Toolbar']
    },
    ImageExtend: {
      action: axios.defaults.baseURL + '/api/image/cache',
      name: 'file',
      size: 5,
      loading: true,
      accept: 'image/png, image/jpeg',
      response: (response) => {
        if (response.data) {
          return axios.defaults.baseURL + '/images' + response.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization)
      },
      start: () => editor.loading = true,
      success: () => {
        ElMessage.success('上传成功')
        editor.loading = false
      },
      error: () => {
        ElMessage.error('上传失败')
        editor.loading = false
      }
    }
  }
}


function submitTopic() {
  console.log(editor.text)
}
</script>

<template>
  <div>
    <el-scrollbar>
      <el-drawer :model-value="show"
                 direction="btt"
                 size="85vh"
                 @close="emit('close')">
        <template #header>
          <div>
            <div style="font-weight: bold;color: var(--el-text-color-primary)">发表话题</div>
            <div style="font-size: 14px;color: var(--el-text-color-secondary)">话题内容请遵守相关规定</div>
          </div>
        </template>
        <div style="display: flex;gap: 10px">
          <div style="width: 110px">
            <el-select v-model="editor.type" placeholder="主题">
              <el-option v-for="item in types" :label="item.name" :value="item.id"/>
            </el-select>
          </div>
          <div style="flex: 1">
            <el-input :prefix-icon="Document" placeholder="请输入标题"/>
          </div>
        </div>
        <div v-loading="editor.loading" element-loading-text="正在上传图片..."
             style="display: flex;flex-direction: column;height: 65vh;border-radius: 5px">
          <div style="margin-top: 20px;flex: 1;overflow: hidden">
            <quill-editor v-model:content="editor.text" placeholder="今天想分享什么呢？"
                          :options="editorOption"
                          content-type="delta"
                          style="height: calc(100% - 45px)"
            />
          </div>
          <div style="display: flex;justify-content: space-between;bottom: 10px">
            <div style="color: var(--el-text-color-secondary);font-size: 14px">
              当前字数 500 / 500
            </div>
            <div>
              <el-button type="success" @click="submitTopic">发表</el-button>
            </div>
          </div>
        </div>

      </el-drawer>
    </el-scrollbar>

  </div>

</template>

<style lang="less" scoped>
:deep(.el-drawer) {
  width: 75vw;
  margin: auto;
  border-radius: 15px 15px 0 0;
}

:deep(.el-drawer__header) {
  margin: 0;
}


:deep(.ql-toolbar) {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}

:deep(.ql-container) {
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}

:deep(.ql-editor.ql-blank::before) {
  color: var(--el-text-color-placeholder);
}
</style>