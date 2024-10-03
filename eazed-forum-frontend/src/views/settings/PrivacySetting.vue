<script setup>
import {Lock, Setting} from "@element-plus/icons-vue";
import Card from "@/components/Card.vue";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {deleteAccessToken, get, post} from "@/net/index.js";

const formRef = ref();

const rules = {
  origin_password: [
    {required: true, message: '请输入原始密码', trigger: ['blur', 'change']}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: ['blur', 'change']},
    {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: ['blur', 'change']},
    {
      validator: (rule, value, callback) => {
        if (value === form.origin_password) {
          callback(new Error('新密码不能和原密码相同'))
        } else {
          callback()
        }
      }, trigger: ['blur']
    }
  ],
  password_confirm: [
    {required: true, message: '请再次输入密码', trigger: ['blur', 'change']},
    {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: ['blur', 'change']},
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, trigger: ['blur']
    }
  ]
}

const form = reactive({
  origin_password: "",
  password: "",
  password_confirm: ""
})

function doReset() {
  formRef.value.validate((valid) => {
    if (valid) {
      post('/api/user/change-password', {...form}, () => {
        ElMessage.success('密码重置成功，请重新登录')
        deleteAccessToken()
        setTimeout(() => {
          window.location.href = '/'
        }, 1000)

      }, (message) => {
        ElMessage.error(message)
      })
    }
  })
}

const privacy = reactive({
  phone: false,
  wechat: false,
  qq: false,
  email: false,
  gender: false
})

get('/api/user/privacy', data => {
  privacy.phone = data.phone
  privacy.wechat = data.wechat
  privacy.qq = data.qq
  privacy.email = data.email
  privacy.gender = data.gender
})

function savePrivacy(type, status) {
  post('/api/user/save-privacy', {
    type: type,
    status: status
  }, () => {
    ElMessage.success("修改成功")
  })
}
</script>

<template>
  <div class="privacy-main">
    <div class="privacy-cards">
      <card :icon="Setting" description="在这里设置哪些内容可以被其他人看见" title="隐私设置">
        <div class="checkbox-list">
          <el-checkbox v-model="privacy.phone"
                       @change="savePrivacy('phone', privacy.phone)">公开展示我的手机号
          </el-checkbox>
          <el-checkbox v-model="privacy.email"
                       @change="savePrivacy('email', privacy.email)">公开展示我的电子邮件地址
          </el-checkbox>
          <el-checkbox v-model="privacy.qq"
                       @change="savePrivacy('qq', privacy.qq)">公开展示我的qq
          </el-checkbox>
          <el-checkbox v-model="privacy.wechat"
                       @change="savePrivacy('wechat', privacy.wechat)">公开展示我的微信号
          </el-checkbox>
          <el-checkbox v-model="privacy.gender"
                       @change="savePrivacy('gender', privacy.gender)">公开展示我的性别
          </el-checkbox>
        </div>
      </card>
      <card :icon="Setting" description="在这里修改您的密码" style="margin-top: 20px" title="修改密码">
        <el-form ref="formRef" :model="form" :rules="rules">
          <el-form-item prop="origin_password">
            <el-input v-model="form.origin_password" placeholder="原始密码" type="password">
              <template #prefix>
                <el-icon>
                  <Lock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
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
        <div style="margin-top: 50px;text-align: center">
          <el-button plain style="width: 50%;margin: auto" type="warning" @click="doReset">重置</el-button>
        </div>
      </card>
    </div>
  </div>
</template>

<style lang="less" scoped>
.privacy-main {
  margin: auto;
  max-width: 600px;

  .privacy-cards {
    margin-top: 20px;

    .checkbox-list {
      display: flex;
      flex-direction: column;
      margin: 10px 0 0 10px;
    }
  }
}
</style>