<script setup>

import {Lock, User} from "@element-plus/icons-vue";
</script>
assssssasssasssssssssssssssssssddasssassssssssaaaaa
<template>
  <div>
    <el-form>
      <el-form-item label="用户名">
        <el-input v-model="form.username" placeholder="用户名/邮箱" type="text">
          <template #prefix>
            <el-icon>
              <User/>
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" maxlength="20" minlength="6" placeholder="密码" type="password">
          <template #prefix>
            <el-icon>
              <Lock/>
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>

</style>