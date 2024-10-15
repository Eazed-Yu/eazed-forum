import {defineStore} from "pinia";
import axios from "axios";

export const useStore = defineStore('general', {
    state: () => {
        return {
            user: {
                username: '',
                email: '',
                role: '',
                registerTime: null,
                avatar: ''
            },
            menu: {
                isCollapse: false,
            },
            forum: {
                types: [],
                selectType: []
            }
        }
    },
    actions: {
        collapseMenu () {
            this.menu.isCollapse = !this.menu.isCollapse;
        },
        findTypeById(id) {
            for (let type of this.forum.types) {
                if (type.id === id) {
                    return type;
                }
            }
        },
        avatarUserUrl(avatar) {
            if (avatar)
                return `${axios.defaults.baseURL}/images${avatar}`
            else
                return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
        }
    },
    getters: {
        avatarUrl() {
            if (this.user.avatar) {
                return `${axios.defaults.baseURL}/images${this.user.avatar}`;
            } else {
                return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png';
            }
        }
    }
})