<script setup>
import {Lock, Setting} from "@element-plus/icons-vue";
import Card from "@/components/Card.vue";
import {computed, reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {accessHeader, deleteAccessToken, get, post} from "@/net/index.js";
import axios from "axios";
import {useStore} from "@/store/index.js";

const store = useStore()
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())

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

get('api/user/details', data => {
  description.value = data.description
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

const description = ref('')
</script>

<template>
  <div style="display: flex;max-width: 950px;margin: auto">
    <div class="privacy-main">
      <div class="privacy-cards unselectable">
        <card :icon="Setting"
              description="在这里设置哪些内容可以被其他人看见"
              style="border-radius: 15px;border: solid 2px var(--el-border-color);" title="隐私设置">
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
        <card :icon="Setting" description="在这里修改您的密码"
              style="border-radius: 15px;border: solid 2px var(--el-border-color);margin-top: 20px"
              title="修改密码">
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
.privacy-main {
  flex: 2;
  margin: 20px;
  justify-content: center;

  .privacy-cards {


    .checkbox-list {
      display: flex;
      flex-direction: column;
      margin: 10px 0 0 10px;
    }
  }
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