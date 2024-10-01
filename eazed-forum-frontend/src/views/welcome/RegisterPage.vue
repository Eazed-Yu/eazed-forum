<script setup>
import {computed, reactive, ref} from 'vue'
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {get, post} from "@/net/index.js";
import {CircleCheck, Lock, Message, User} from "@element-plus/icons-vue";

const form = reactive({
  username: '',
  password: '',
  password_confirm: '',
  email: '',
  code: ''
})

const formRef = ref()

const codeTime = ref(0)

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和汉字'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 1, max: 10, message: '用户名长度在 1 到 10 个字符', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: ['blur', 'change']},
    {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: ['blur', 'change']}
  ],
  password_confirm: [
    {required: true, message: '请再次输入密码', trigger: ['blur', 'change']},
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, trigger: ['blur']
    },
    {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: ['blur', 'change']}
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

function askCode() {
  if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email)) {
    ElMessage.error('请输入正确的电子邮件')
  } else {
    get(`/api/auth/ask-code?email=${form.email}&type=register`, () => {
      ElMessage.success('验证码已发送至' + form.email + '，请查收')
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

const isEmailValid = computed(() => {
  return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email)
})

function register() {
  formRef.value.validate((vaild) => {
    if (vaild) {
      post("api/auth/register", form, () => {
        ElMessage.success('注册成功')
        router.push('/')
      }, (message) => {
        ElMessage.error(message)
      })
    } else {
      ElMessage.error('请检查输入')
    }
  })
}

</script>

<template>
  <div style="text-align: center; margin: 0 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 50px; font-weight: bold">注册</div>
      <div style="font-size: 14px; color: grey">请输入账号和密码</div>
    </div>
    <div style="margin: 50px 20px">
      <el-form ref="formRef" :model="form" :rules="rules">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" placeholder="用户名" type="text">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="20" minlength="6" placeholder="密码" type="password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_confirm">
          <el-input v-model="form.password_confirm" maxlength="20" minlength="6" placeholder="重复密码" type="password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="电子邮件" type="text">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row style="width: 100%">
            <el-col :span="16">
              <el-input v-model="form.code" placeholder="验证码" type="text">
                <template #prefix>
                  <el-icon>
                    <CircleCheck/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="8" style="text-align: right">
              <el-button :disabled="codeTime > 0 || !isEmailValid" type="success" @click="askCode" >
                {{ codeTime ? `请稍后：${codeTime}s` : '获取验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style="margin-top: 50px">
      <el-button plain style="width: 70%" type="warning" @click="register">注册</el-button>
    </div>
    <div style="margin-top: 20px">
      <span style="font-size: 14px;line-height: 15px;color: grey">已有账号?</span>
      <el-link style="translate: 0 -1px" @click="router.push('/')">立即登入</el-link>
    </div>
  </div>

</template>

<style scoped>

</style>