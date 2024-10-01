<script setup>
import {ref, reactive, computed} from "vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net/index.js";
import router from "@/router/index.js";
import {CircleCheck, Lock, Message} from "@element-plus/icons-vue";

const active = ref(0)
const formRef = ref()
const codeTime = ref(0)

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

function confirmReset() {
  formRef.value.validate((valid) => {
    if (valid) {
      post('/api/auth/reset-confirm', {
        email: form.email,
        code: form.code
      }, () => {
        active.value = 1;
      }, (message) => {
        ElMessage.error(message)
      })
    }
  })
}

function doReset() {
  formRef.value.validate((valid) => {
    if (valid) {
      post('/api/auth/reset-password', {...form}, () => {
        ElMessage.success('密码重置成功')
        active.value = 2
      }, (message) => {
        ElMessage.error(message)
      })
    }
  })
}


const form = reactive({
  email: "",
  code: "",
  password_confirm: "",
  password: ""
})


const rules = {
  email: [
    {required: true, message: '请输入电子邮件', trigger: ['blur', 'change']},
    {type: 'email', message: '请输入正确的电子邮件', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: ['blur', 'change']},
    {min: 6, max: 6, message: '验证码长度为6位', trigger: ['blur', 'change']}
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
  ]
}
const isEmailValid = computed(() => {
  return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email)
})
</script>

<template>
  <div style="text-align: center">
    <div style="margin-top: 30px">
      <el-steps :active="active" align-center finish-status="success">
        <el-step title="验证电子邮件"/>
        <el-step title="重置密码"/>
        <el-step title="完成"/>
      </el-steps>
      <div v-if="active === 0">
        <div style="margin-top: 80px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: grey">请输入您的电子邮件地址</div>
        </div>
        <div style="margin: 40px 50px">
          <el-form ref="formRef" :model="form" :rules="rules">
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
                  <el-button :disabled="codeTime > 0 || !isEmailValid" type="success" @click="askCode">
                    {{ codeTime ? `请稍后：${codeTime}s` : '获取验证码' }}
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 50px">
          <el-button plain style="width: 70%" type="warning" @click="confirmReset">下一步</el-button>
        </div>
        <div style="margin-top: 20px">
          <el-link style="translate: 0 -1px" @click="router.push('/')">返回登入</el-link>
        </div>
      </div>
      <div v-if="active === 1">
        <div style="margin-top: 80px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: grey">请输入您的新密码</div>
        </div>
        <div style="margin: 40px 50px">
          <el-form ref="formRef" :model="form" :rules="rules">
            <el-form-item prop="password">
              <el-input v-model="form.password" placeholder="密码" type="password">
                <template #prefix>
                  <el-icon>
                    <Lock/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password_confirm">
              <el-input v-model="form.password_confirm" placeholder="重复密码" type="password">
                <template #prefix>
                  <el-icon>
                    <Lock/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 50px">
          <el-button plain style="width: 70%" type="warning" @click="doReset">重置</el-button>
        </div>
        <div style="margin-top: 20px">
          <el-link style="translate: 0 -1px" @click="router.push('/')">返回登入</el-link>
        </div>
      </div>
      <div v-if="active === 2"><!--注册成功提示，返回登入页面，使用Element ui-->
        <div style="margin-top: 80px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <el-result
              icon="success"
              status="success"
              sub-title="您的密码已重置成功"
              title="密码重置成功"
          >
            <template #extra>
              <el-button type="primary" @click="router.push('/')">返回登入</el-button>
            </template>
          </el-result>
        </div>
      </div>
    </div>

  </div>

</template>

<style scoped>

</style>