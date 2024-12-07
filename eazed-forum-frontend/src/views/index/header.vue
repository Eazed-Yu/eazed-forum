<script setup>
import {useStore} from "@/store/index.js";
import {Back, Fold, Message, Moon, Operation, Search, Sunny} from "@element-plus/icons-vue";
import {computed, reactive, ref, watchEffect} from "vue";
import {deleteAccessToken, logout} from "@/net/index.js";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {toggleTheme} from "@/theme";
import {useDark} from "@vueuse/core";


const store = useStore();

const searchInput = reactive({
  type: '1',
  text: ''
})

const dark = useDark()

function userLogout() {
  logout(() => {
    router.push('/')
  }, (message) => {
    ElMessage.error(message)
    deleteAccessToken()
  })
}

const windowWidth = ref(window.innerWidth);
const isMobile = computed(() => {
  return windowWidth.value < 1240;
});
watchEffect(() => {
  const updateWindowWidth = () => {
    windowWidth.value = window.innerWidth;
  };
  window.addEventListener('resize', updateWindowWidth);
  return () => {
    window.removeEventListener('resize', updateWindowWidth);
  };
});

</script>

<template>
  <div class="header-container">
    <div class="header-left flex-box">
      <el-icon class="icon" size="20" @click="store.collapseMenu">
        <Fold/>
      </el-icon>
    </div>
    <div class="header-center">
      <el-input v-model="searchInput.text" placeholder="搜索...">
        <template #prefix>
          <el-icon>
            <Search/>
          </el-icon>
        </template>
        <template #append>
          <el-select v-model="searchInput.type" style="width: 120px">
            <el-option label="帖子广场" value="1"/>
            <el-option label="校园活动" value="2"/>
            <el-option label="表白墙" value="3"/>
            <el-option label="教务通知" value="4"/>
          </el-select>
        </template>
      </el-input>
    </div>
    <div class="header-right">
      <div class="theme unselectable" @click="toggleTheme">
        <span class="switch">
          <el-icon :class="`icon ${dark ? 'dark' : 'light'}`">
            <component :is="dark ? Moon : Sunny"/>
          </el-icon>
        </span>
      </div>
      <el-dropdown class="unselectable">
        <span class="el-dropdown-link flex-box">
          <el-avatar :src="store.avatarUrl" style="pointer-events: none;"/>
          <span v-if="!isMobile" class="profile">
            <span class="username">{{ store.user.username }}</span>
            <span class="email">{{ store.user.email }}</span>
          </span>
        </span>
        <template #dropdown>
          <el-dropdown-menu class="unselectable">
            <el-dropdown-item>
              <el-icon>
                <Operation/>
              </el-icon>
              个人设置
            </el-dropdown-item>
            <el-dropdown-item>
              <el-icon>
                <Message/>
              </el-icon>
              消息列表
            </el-dropdown-item>
            <el-dropdown-item divided @click="userLogout">
              <el-icon>
                <Back/>
              </el-icon>
              退出登入
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style lang="less" scoped>
@media (max-width: 1240px) {
  .header-container {
    .header-left {
      display: none;
    }
  }
}


.flex-box {
  display: flex;
  align-items: center;
}

.header-container {
  background-color: var(--el-bg-color);
  border-bottom: solid 1px var(--el-border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-right: 25px;
  height: 100%;

  .header-left {
    height: 100%;

    .icon:hover {
      background-color: var(--el-border-color);
      cursor: pointer;
    }

    .flex-box {
      height: 100%;
    }

    .icon {
      width: 45px;
      height: 100%;
    }
  }

  .header-center {
    padding: 0 20px;
    align-self: center;
  }

  .header-right {
    flex-shrink: 0;
    height: 100%;
    display: flex;
    align-items: center;

    .theme {
      margin-right: 20px;
      justify-content: center;
      display: flex;
      flex-direction: column;


      .switch {
        display: flex;
        align-items: center;
        min-width: 40px;
        height: 20px;
        border: 1px solid var(--el-border-color);
        border-radius: 10px;
        box-sizing: border-box;
        cursor: pointer;

        .icon {
          justify-self: center;
          align-self: center;
        }

        .icon.dark {
          transform: translateX(20px);
        }
      }

    }

    .theme:hover {
      cursor: pointer;
    }

    .el-dropdown-link {
      display: flex;
      align-items: center;
      cursor: pointer;

      .el-avatar {
        margin-right: 10px;
      }

      .profile {
        display: flex;
        flex-direction: column;
        align-items: flex-start;

        .username {
          margin: 2px 2px;
          font-size: 16px;
          font-weight: bold;
          line-height: 20px;
          color: var(--el-avatar-text-color);
        }

        .email {
          margin: 2px 2px;
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }
    }

    .el-dropdown-link:hover {
      cursor: pointer;
    }

  }
}
</style>