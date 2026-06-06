<template>
  <component :is="layoutComponent" />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useThemeStore } from '@/stores/theme'
import SidebarLayout from './SidebarLayout.vue'
import TopMenuLayout from './TopMenuLayout.vue'

const themeStore = useThemeStore()

const layoutComponent = computed(() =>
  themeStore.layout === 'topmenu' ? TopMenuLayout : SidebarLayout
)
</script>
