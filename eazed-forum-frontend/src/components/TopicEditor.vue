<script setup>
import {Document} from "@element-plus/icons-vue";
import {computed, reactive, ref} from "vue";
import {Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue";
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import axios from "axios";
import {accessHeader, get, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import ColorDot from "@/components/ColorDot.vue";

defineProps({
  show: Boolean
})

const refEditor = ref()

const editor = reactive({
  type: null,
  title: '',
  text: '',
  loading: false,
  types: []
})

function initEditor() {
  refEditor.value.setContents('', 'user')
  editor.type = null
  editor.title = ''
}

get('/api/forum/types', data => {
  editor.types = data
})

const emit = defineEmits(['close', 'success'])


function deltaToText(delta) {
  if (!delta) return ''
  let str = ''
  delta.ops.forEach(item => {
    if (item.insert) {
      if (typeof item.insert === 'string') {
        str += item.insert
      } else {
      }
    }
  })
  return str.replace(/\s/g, '')
}

const contentLength = computed(() => {
  return deltaToText(editor.text).length
})

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
  if (!editor.type) {
    ElMessage.error('请选择主题')
    return
  }
  if (!editor.title) {
    ElMessage.error('请输入标题')
    return
  }
  if (contentLength.value === 0) {
    ElMessage.error('请输入内容')
    return
  }
  if (contentLength.value > 20000) {
    ElMessage.error('内容字数超过限制')
    return
  }
  post('/api/forum/create-topic', {
    type: editor.type.id,
    title: editor.title,
    content: editor.text
  }, () => {
    ElMessage.success('发表成功')
    initEditor()
    emit('success')

  })
}
</script>

<template>
  <div>
    <el-scrollbar>
      <el-drawer :model-value="show"
                 direction="btt"
                 size="85vh"
                 @close="emit('close')"
      >
        <template #header>
          <div>
            <div style="font-weight: bold;color: var(--el-text-color-primary)">发表话题</div>
            <div style="font-size: 14px;color: var(--el-text-color-secondary)">话题内容请遵守相关规定</div>
          </div>
        </template>
        <div style="display: flex;gap: 10px">
          <div style="width: 110px">
            <el-select v-model="editor.type" :disabled="!editor.types.length" placeholder="主题" value-key="id">
              <el-option v-for="item in editor.types" :label="item.name" :value="item">
                <div>
                  <color-dot :color="item.color"/>
                  <span style="margin-left: 10px">{{ item.name }}</span>
                </div>
              </el-option>
            </el-select>
          </div>
          <div style="flex: 1">
            <el-input v-model="editor.title" :prefix-icon="Document" maxlength="30" placeholder="请输入标题"/>
          </div>
        </div>
        <div style="margin-top: 10px;font-size: 14px;color: var(--el-text-color-secondary)">
          <color-dot :color="editor.type ? editor.type.color : '#686767'"/>
          <span style="margin-left: 10px">{{ editor.type ? editor.type.description : "请选择主题" }}</span>
        </div>
        <div element-loading-text="正在上传图片..."
             style="display: flex;flex-direction: column;height: 65vh;border-radius: 5px">
          <div style="margin-top: 20px;flex: 1;overflow: hidden">
            <quill-editor v-model:content="editor.text" placeholder="今天想分享什么呢？"
                          :options="editorOption"
                          content-type="delta"
                          style="height: calc(100% - 45px)"
                          ref="refEditor"
            />
          </div>
          <div style="display: flex;justify-content: space-between;bottom: 10px">
            <div style="color: var(--el-text-color-secondary);font-size: 14px">
              当前字数 {{ contentLength }} / 20000
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