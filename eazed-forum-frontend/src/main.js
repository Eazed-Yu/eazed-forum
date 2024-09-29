import { createApp } from 'vue'
import App from './App.vue'
import './style.css'
import router from "@/router";
import axios from "axios";
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import {createPinia} from "pinia";
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

axios.defaults.baseURL = 'http://localhost:8080'

const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(ElementPlus, {
    locale: zhCn,
})
app.use(createPinia())
app.use(router)

app.mount('#app')
