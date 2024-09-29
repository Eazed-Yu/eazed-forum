import {defineStore} from "pinia";

export const useStore = defineStore('general', {
    state: () => {
        return {
            user: {
                username: '',
                email: '',
                role: '',
                registerTime: null
            },
            menu: {
                isCollapse: false,
            }
        }
    },
    actions: {
        collapseMenu () {
            this.menu.isCollapse = !this.menu.isCollapse;
        }
    }
})