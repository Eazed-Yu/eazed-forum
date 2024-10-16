import {createApp} from 'vue'
import App from './App.vue'
import './style.css'
import router from "@/router";
import axios from "axios";
import 'element-plus/theme-chalk/dark/css-vars.css'
import {createPinia} from "pinia";
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'


axios.defaults.baseURL = import.meta.env.VITE_BASE_URL
const app = createApp(App)

app.use(ElementPlus, {
    locale: zhCn,
})
app.use(createPinia())
app.use(router)

app.mount('#app')
