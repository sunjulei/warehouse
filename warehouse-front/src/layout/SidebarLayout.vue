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
  background: linear-gradient(180deg, var(--sidebar-bg) 0%, var(--sidebar-bg-light) 100%);
  transition: width var(--transition-base), box-shadow var(--transition-base);
  overflow: hidden;
  box-shadow: var(--shadow-lg);
  position: relative;
  z-index: 10;
}

.layout-aside::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 1px;
  background: linear-gradient(180deg, rgba(var(--primary-rgb), 0.2) 0%, rgba(var(--primary-rgb), 0.05) 50%, rgba(var(--primary-rgb), 0.2) 100%);
  pointer-events: none;
}

.layout-wrapper {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all var(--transition-base);
}

.layout-header {
  background: var(--bg-primary);
  box-shadow: var(--shadow-sm);
  padding: 0 var(--spacing-lg);
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--border-light);
  position: relative;
  z-index: 5;
  backdrop-filter: blur(8px);
  transition: background-color var(--transition-base), border-color var(--transition-base), box-shadow var(--transition-base);
}

.layout-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(var(--primary-rgb), 0.15) 20%, rgba(var(--primary-rgb), 0.3) 50%, rgba(var(--primary-rgb), 0.15) 80%, transparent 100%);
  pointer-events: none;
}

.layout-main {
  background: var(--bg-secondary);
  padding: var(--spacing-lg);
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
  background: rgba(var(--primary-rgb), 0.15);
  border-radius: 3px;
  transition: background var(--transition-fast);
}

.layout-main::-webkit-scrollbar-thumb:hover {
  background: rgba(var(--primary-rgb), 0.3);
}
</style>
