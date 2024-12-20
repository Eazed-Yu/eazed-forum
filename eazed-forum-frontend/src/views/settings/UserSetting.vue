<script setup>

import Card from "@/components/Card.vue";
import {CircleCheck, Message, Select, User,} from "@element-plus/icons-vue";
import {useStore} from "@/store/index.js";
import {computed, reactive, ref} from "vue";
import {accessHeader, get, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import axios from "axios";

const store = useStore()
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())


const description = ref('')
const baseFormRef = ref()
const emailFormRef = ref()

const baseForm = reactive({
  username: '',
  gender: 1,
  phone: '',
  qq: '',
  wechat: '',
  description: ''
})
const emailFrom = reactive({
  email: '',
  code: ''
})
const rules = {
  username: [
    {required: true, message: '请输入用户名', trigger: ['blur', 'change']},
    {min: 1, max: 10, message: '用户名长度在 1 到 10 个字符', trigger: 'blur'}
  ],
  email: [
    {required: true, message: '请输入电子邮件', trigger: ['blur', 'change']},
    {type: 'email', message: '请输入正确的电子邮件', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: ['blur', 'change']},
    {min: 6, max: 6, message: '验证码长度为6位', trigger: ['blur', 'change']}
  ]
}


function saveDetails() {
  baseFormRef.value.validate((valid) => {
    if (valid) {
      loading.base = true
      post('/api/user/save-details', baseForm, () => {
        ElMessage.success('保存成功')
        loading.base = false
        store.user.username = baseForm.username
        description.value = baseForm.description
      }, message => {
        loading.base = false
        ElMessage(message)
      })
    }
  })
}

const loading = reactive({
  form: true,
  base: false
})

function modifyEmail() {
  emailFormRef.value.validate((valid) => {
    if (valid) {
      post('/api/user/modify-email', emailFrom, () => {
        ElMessage.success('修改成功')
        store.user.email = emailFrom.email
      }, message => {
        ElMessage.error(message)
      })
    }
  })
}


get('api/user/details', data => {
  baseForm.username = store.user.username
  baseForm.gender = data.gender
  baseForm.phone = data.phone
  baseForm.qq = data.qq
  baseForm.wechat = data.wechat
  baseForm.description = data.description
  description.value = data.description
  loading.form = false
  emailFrom.email = store.user.email
})

const codeTime = ref(0)

function askCode() {
  if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(emailFrom.email)) {
    ElMessage.error('请输入正确的电子邮件')
  } else {
    get(`/api/auth/ask-code?email=${emailFrom.email}&type=modify`, () => {
      ElMessage.success('验证码已发送至' + emailFrom.email + '，请查收')
      codeTime.value = 60
      const intervalId = setInterval(() => {
        codeTime.value--
        if (codeTime.value === 0) {
          clearInterval(intervalId)
        }
      }, 1000)
    }, (message) => {
      ElMessage.error(message)
      codeTime.value = 0
    })
  }
}

function beforeAvatarUpload(rawFile) {
  const isJPG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png'
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG 和 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2M!')
  }
  return isJPG && isLt2M
}


function avatarUploadSuccess(response) {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    store.user.avatar = response.data
  } else {
    ElMessage.error('上传失败')
  }
}

</script>

<template>
  <div style="display: flex;max-width: 950px;margin: auto">
    <div class="settings-left">
      <div class="cards unselectable">
        <card :icon="User"
              style="border-radius: 15px;border: solid 2px var(--el-border-color);"
              class="card"
              v-loading="loading.form"
              description="在这里编辑您的个人信息，您可以在隐私设置中选择是否展示这些信息"
              title="账号信息设置">
          <el-form ref="baseFormRef" :model="baseForm" :rules="rules" label-position="top"
                   style="margin: 0 10px 10px 10px">
            <el-form-item label="用户名">
              <el-input v-model="baseForm.username"></el-input>
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="baseForm.gender">
                <el-radio :value="0">男</el-radio>
                <el-radio :value="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-model="baseForm.phone" label="手机号" prop="phone">
              <el-input v-model="baseForm.phone" maxlength="11"/>
            </el-form-item>

            <el-form-item label="qq号" prop="qq">
              <el-input v-model="baseForm.qq" maxlength="13"/>
            </el-form-item>
            <el-form-item label="微信号" prop="wechat">
              <el-input v-model="baseForm.wechat" maxlength="20"/>
            </el-form-item>
            <el-form-item label="个人简介" prop="description">
              <el-input v-model="baseForm.description" :rows="6" maxlength="200" type="textarea"/>
            </el-form-item>
            <div>
              <el-button
                  :icon="Select"
                  :loading="loading.base"
                  type="success"
                  style="margin-top: 5px"
                  @click="saveDetails">保存用户信息
              </el-button>
            </div>
          </el-form>
        </card>
        <card :icon="Message"
              description="您可以修改绑定的电子邮件地址"
              class="card"
              style="border-radius: 15px;border: solid 2px var(--el-border-color);margin-top: 20px"
              title="电子邮件设置">
          <el-form ref="emailFormRef" :model="emailFrom" :rules="rules" label-position="top"
                   style="margin: 0 10px 10px 10px">
            <el-form-item label="电子邮件" prop="email">
              <el-input v-model="emailFrom.email"/>
            </el-form-item>
            <el-form-item prop="code">
              <el-row style="width: 100%;">
                <el-col :span="16">
                  <el-input v-model="emailFrom.code" placeholder="验证码" type="text">
                    <template #prefix>
                      <el-icon>
                        <CircleCheck/>
                      </el-icon>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="8" style="text-align: right">
                  <el-button :disabled="codeTime > 0" type="success" @click="askCode">
                    {{ codeTime ? `请稍后：${codeTime}s` : '获取验证码' }}
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
            <div>
              <el-button
                  :icon="Select"
                  style="margin-top: 20px"
                  type="success"
                  @click="modifyEmail">保存用户信息
              </el-button>
            </div>
          </el-form>
        </card>
      </div>
    </div>
    <div class="settings-right unselectable">
      <div style="position: sticky;top: 20px">
        <card style="  border-radius: 15px;border: solid 2px var(--el-border-color);">
          <div style="text-align: center;padding: 5px 15px 15px 15px">
            <div>
              <el-avatar :src="store.avatarUrl" size="large"/>
              <div style="margin: 5px 0">
                <el-upload :action="axios.defaults.baseURL + '/api/image/avatar'"
                           :before-upload="beforeAvatarUpload"
                           :headers="accessHeader()"
                           :on-success="avatarUploadSuccess"
                           :show-file-list="false"
                >
                  <el-button round size="small">修改头像</el-button>
                </el-upload>
              </div>
              <div class="user-name">你好，{{ store.user.username }}</div>
            </div>
            <el-divider style="margin: 10px 0"/>
            <div class="user-introduction">
              {{ description || "这个人很懒，什么都没留下" }}
            </div>
          </div>
        </card>
        <card style="border-radius: 15px;border: solid 2px var(--el-border-color);margin-top: 14px">
          <div style="color: var(--el-text-color-primary);font-weight: bold;">用户注册时间：{{ registerTime }}</div>
          <div style="color: var(--el-text-color-secondary)">欢迎加入我们的学习论坛</div>
        </card>
      </div>

    </div>
  </div>
</template>

<style lang="less" scoped>
.settings-left {
  flex: 2;
  margin: 20px;
  justify-content: center;
}

.settings-right {
  width: 280px;
  margin: 20px;
  .user-name {

    font-size: 20px;
    font-weight: bold;
    color: var(--el-text-color-primary);
    margin-top: 10px;
  }
  .user-introduction {
    font-size: 14px;
    color: var(--el-text-color-secondary);
    margin-top: 10px;
  }
}
</style>