import {useDark} from '@vueuse/core'
import {nextTick} from 'vue'

// 使用 useDark 来处理深色模式切换
const isDark = useDark({
    selector: 'html',
    attribute: 'class',
    valueDark: 'dark',
    valueLight: 'light',
})

// 处理按钮点击时的主题切换
function toggleTheme(event) {
    // @ts-expect-error experimental API
    const isAppearanceTransition = document.startViewTransition
        && !window.matchMedia('(prefers-reduced-motion: reduce)').matches

    if (!isAppearanceTransition) {
        isDark.value = !isDark.value
        return
    }

    const x = event.clientX
    const y = event.clientY
    const endRadius = Math.hypot(
        Math.max(x, innerWidth - x),
        Math.max(y, innerHeight - y),
    )
    // @ts-expect-error: Transition API
    const transition = document.startViewTransition(async () => {
        isDark.value = !isDark.value
        await nextTick()
    })
    transition.ready
        .then(() => {
            const clipPath = [
                `circle(0px at ${x}px ${y}px)`,
                `circle(${endRadius}px at ${x}px ${y}px)`,
            ]
            document.documentElement.animate(
                {
                    clipPath: isDark.value
                        ? [...clipPath].reverse()
                        : clipPath,
                },
                {
                    duration: 400,
                    easing: 'ease-out',
                    pseudoElement: isDark.value
                        ? '::view-transition-old(root)'
                        : '::view-transition-new(root)',
                },
            )
        })
}

export {toggleTheme}