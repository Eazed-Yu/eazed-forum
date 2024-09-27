<script setup>
import {reactive, ref} from "vue";
import {login} from "@/net/index.js";
import router from "@/router/index.js";
import {Lock, User} from "@element-plus/icons-vue";

const formRef = ref()
const form = reactive({
  username: "",
  password: "",
  remember: false,
})


const rule = {
  username: [
    {required: true, message: "请输入用户名", trigger: 'blur'},
  ],
  password: [
    {required: true, message: "请输入密码", trigger: 'blur'},
  ]
}

function userLogin() {
  formRef.value.validate( (valid) => {
    if (valid) {
      login(form.username, form.password, form.remember, () =>{
        router.push('/index')
      })
    }
  })
}
</script>

<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 50px;font-weight: bold">登入</div>
      <div style="font-size: 14px;color: grey">请输入账号和密码</div>
    </div>
    <div style="margin: 50px 20px">
      <el-form ref="formRef" :model="form" :rules="rule">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名/邮箱" type="text">
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
        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-form-item prop="remember">
              <el-checkbox v-model="form.remember" label="记住我"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link @click="router.push('/reset')">忘记密码?</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div style="margin-top: 00px">
      <el-button style="width: 70%" type="success" @click="userLogin">登入</el-button>
    </div>
    <el-divider content-position="center" style="margin-top: 20px">没有账号</el-divider>
    <div style="margin-top: 20px">
      <el-button plain style="width: 70%" type="warning" @click="router.push('/register')">立即注册</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>