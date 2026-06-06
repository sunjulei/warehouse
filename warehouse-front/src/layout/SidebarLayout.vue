<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? 'var(--sidebar-collapsed-width)' : 'var(--sidebar-width)'" class="layout-aside">
      <Sidebar :is-collapse="isCollapse" />
    </el-aside>
    <el-container class="layout-wrapper">
      <el-header class="layout-header" height="var(--header-height)">
        <Header :is-collapse="isCollapse" @toggle-collapse="isCollapse = !isCollapse" />
      </el-header>
      <TabsBar />
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'
import TabsBar from './components/TabsBar.vue'

const isCollapse = ref(false)
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-aside {
  background: var(--bg-primary);
  transition: width var(--transition-base);
  overflow: hidden;
  border-right: 1px solid var(--border-light);
  position: relative;
  z-index: 10;
}

.layout-wrapper {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all var(--transition-base);
}

.layout-header {
  background: var(--bg-primary);
  padding: 0 var(--spacing-xl);
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--border-light);
  position: relative;
  z-index: 5;
  transition: background-color var(--transition-base), border-color var(--transition-base);
}

.layout-main {
  background: var(--bg-secondary);
  padding: var(--spacing-xl);
  overflow-y: auto;
  flex: 1;
  transition: background-color var(--transition-base);
}

.layout-main::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.layout-main::-webkit-scrollbar-track {
  background: transparent;
}

.layout-main::-webkit-scrollbar-thumb {
  background: rgba(var(--primary-rgb), 0.12);
  border-radius: 3px;
}

.layout-main::-webkit-scrollbar-thumb:hover {
  background: rgba(var(--primary-rgb), 0.25);
}
</style>
