<script setup>
import {Document} from "@element-plus/icons-vue";
import {computed, reactive, ref} from "vue";
import { EditorContent, useEditor } from "@tiptap/vue-3";
import StarterKit from "@tiptap/starter-kit";
import axios from "axios";
import {accessHeader, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import ColorDot from "@/components/ColorDot.vue";
import {useStore} from "@/store/index.js";

const props = defineProps({
  show: Boolean,
  defaultTitle: {
    default: '',
    type: String
  },
  defaultText: {
    default: '',
    type: String
  },
  defaultType: {
    default: null,
    type: Number
  },
  submitButton: {
    default: '立即发表主题',
    type: String
  },
  submit: {
    default: (editor, success) => {
      post('/api/forum/create-topic', {
        type: editor.type.id,
        title: editor.title,
        content: editor.text
      }, () => {
        ElMessage.success("帖子发表成功！")
        success()
      })
    },
    type: Function
  }
});
const store = useStore()
const refEditor = ref()

const editor = reactive({
  type: null,
  title: '',
  text: '',
  loading: false,
})

const tiptapEditor = useEditor({
  extensions: [StarterKit],
  content: "",
});

function initEditor() {
  if (props.defaultText)
    tiptapEditor.commands.setContent(props.defaultText);
  else
    tiptapEditor.commands.clearContent();
  editor.title = props.defaultTitle
  editor.type = findTypeById(props.defaultType)
}


function findTypeById(id) {
  for (let type of store.forum.types) {
    if (type.id === id)
      return type
  }
}

const emit = defineEmits(['close', 'success'])


function deltaToText(delta) {
  const div = document.createElement("div");
  div.innerHTML = delta;
  return div.textContent || div.innerText || "";
}

const contentLength = computed(() => {
  return deltaToText(tiptapEditor.getHTML()).length;
})

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
    content: tiptapEditor.getHTML()
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
                 size="95vh"
                 @close="emit('close')"
                 @open="initEditor"
      >
        <template #header>
          <div>
            <div style="font-weight: bold;color: var(--el-text-color-primary)">发表话题</div>
            <div style="font-size: 14px;color: var(--el-text-color-secondary)">话题内容请遵守相关规定</div>
          </div>
        </template>
        <div style="display: flex;gap: 10px">
          <div style="width: 110px">
            <el-select v-model="editor.type" :disabled="!store.forum.types.length" placeholder="主题" value-key="id">
              <el-option v-for="item in store.forum.types" :label="item.name" :value="item">
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
            <EditorContent :editor="tiptapEditor" />
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

</style>
