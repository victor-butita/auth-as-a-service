<template>
  <div class="app-container">
    <Sidebar :activeView="currentView" @changeView="currentView = $event" />
    <main class="main-content">
      <header class="top-nav">
        <div class="search-bar glass-card">
          <Search :size="18" />
          <input type="text" placeholder="Search applications, users..." />
        </div>
        <div class="actions">
          <button class="btn-icon glass-card"><Bell :size="18" /></button>
          <button class="btn-primary" @click="currentView = 'applications'">Create App</button>
        </div>
      </header>
      
      <div class="dashboard-content">
        <Overview v-if="currentView === 'overview'" />
        <Tenants v-if="currentView === 'tenants'" />
        <Applications v-if="currentView === 'applications'" />
        <Playground v-if="currentView === 'playground'" />
        <Integration v-if="currentView === 'integration'" />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Sidebar from './components/dashboard/Sidebar.vue'
import Overview from './components/dashboard/Overview.vue'
import Tenants from './components/dashboard/Tenants.vue'
import Applications from './components/dashboard/Applications.vue'
import Playground from './components/dashboard/Playground.vue'
import Integration from './components/dashboard/Integration.vue'
import { Search, Bell } from 'lucide-vue-next'
import { useAppStore } from './stores/app'

const currentView = ref('overview')
const appStore = useAppStore()

onMounted(async () => {
  await appStore.fetchTenants()
  if (appStore.tenants.length > 0) {
    await appStore.fetchApplications(appStore.tenants[0].id)
  }
})
</script>

<style>
.app-container {
  display: flex;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  margin-left: 300px; /* Sidebar width + margin */
  padding: 20px 40px 20px 20px;
}

.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  height: 60px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  width: 400px;
  height: 48px;
}

.search-bar input {
  background: transparent;
  border: none;
  color: white;
  width: 100%;
  font-size: 14px;
}

.search-bar input:focus {
  outline: none;
}

.actions {
  display: flex;
  gap: 12px;
}

.btn-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-muted);
}
</style>
