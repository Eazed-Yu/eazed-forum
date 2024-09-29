<script setup>

import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store/index.js";
import {computed} from "vue";
const store = useStore()
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())
</script>

<template>
  <div style="display: flex; justify-content: center;">
    <div class="settings-left">
      <card :icon="User"
            description="在这里编辑您的个人信息，您可以在隐私设置中选择是否展示这些信息"
            title="账号信息设置">
        <el-form label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="用户名">
            <el-input></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group>
              <el-radio value="1">男</el-radio>
              <el-radio value="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input></el-input>
          </el-form-item>

          <el-form-item label="qq号">
            <el-input></el-input>
          </el-form-item>
          <el-form-item label="微信号">
            <el-input></el-input>
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input :rows="6" type="textarea"></el-input>
          </el-form-item>
          <div>
            <el-button :icon="Select" type="success">保存用户信息</el-button>
          </div>
        </el-form>
      </card>
      <card :icon="Message"
            description="您可以修改绑定的电子邮件地址" style="margin-top: 10px"
            title="电子邮件设置">
        <el-form label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="电子邮件">
            <el-input></el-input>
          </el-form-item>
          <el-form-item>
            <el-row :gutter="10" style="width: 100%;">
              <el-col :span="18">
                <el-input placeholder="请输入验证码"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success">获取验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button :icon="Refresh" type="success">保存用户信息</el-button>
          </div>
        </el-form>
      </card>
    </div>
    <div class="settings-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center;padding: 5px 15px 15px 15px">
            <div>
              <el-avatar size="large" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
              <div class="user-name">你好，{{store.user.username}}</div>
            </div>
            <el-divider style="margin: 10px 0"/>
            <div class="user-introduction">
              个人信息介绍
              阿斯顿发大水发射点发射点发射点发射点发
              阿斯蒂芬阿斯顿发顺丰阿斯顿富士达法撒旦发大水f大师傅发送十大发
            </div>
          </div>
        </card>
        <card style="margin-top: 14px">
          <div style="color: var(--el-text-color-primary);font-weight: bold;">用户注册时间：{{registerTime}}</div>
          <div style="color: var(--el-text-color-secondary)">欢迎加入我们的学习论坛</div>
        </card>
      </div>

    </div>
  </div>
</template>

<style scoped>
.settings-left {
  flex: 2;
  min-width: 450px;
  max-width: 500px;
  margin: 20px;
}

.settings-right {
  flex: 1;
  margin: 20px;
  max-width: 300px;
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