import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export type ThemeMode = 'light' | 'dark'
export type LayoutMode = 'sidebar' | 'topmenu'
export type ThemeColor = 'rose' | 'indigo' | 'emerald' | 'navy'

export const useThemeStore = defineStore('theme', () => {
  const THEME_KEY = 'warehouse_theme'
  const LAYOUT_KEY = 'warehouse_layout'
  const COLOR_KEY = 'warehouse_theme_color'

  const mode = ref<ThemeMode>((localStorage.getItem(THEME_KEY) as ThemeMode) || 'light')
  const layout = ref<LayoutMode>((localStorage.getItem(LAYOUT_KEY) as LayoutMode) || 'sidebar')
  const themeColor = ref<ThemeColor>((localStorage.getItem(COLOR_KEY) as ThemeColor) || 'rose')

  const toggle = () => {
    mode.value = mode.value === 'light' ? 'dark' : 'light'
  }

  const setMode = (m: ThemeMode) => {
    mode.value = m
  }

  const setLayout = (l: LayoutMode) => {
    layout.value = l
  }

  const toggleLayout = () => {
    layout.value = layout.value === 'sidebar' ? 'topmenu' : 'sidebar'
  }

  const setThemeColor = (c: ThemeColor) => {
    themeColor.value = c
  }

  watch(mode, (val) => {
    document.documentElement.setAttribute('data-theme', val)
    localStorage.setItem(THEME_KEY, val)
  }, { immediate: true })

  watch(layout, (val) => {
    localStorage.setItem(LAYOUT_KEY, val)
  }, { immediate: true })

  watch(themeColor, (val) => {
    document.documentElement.setAttribute('data-theme-color', val)
    localStorage.setItem(COLOR_KEY, val)
  }, { immediate: true })

  return { mode, layout, themeColor, toggle, setMode, setLayout, toggleLayout, setThemeColor }
})
