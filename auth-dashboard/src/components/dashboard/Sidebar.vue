<template>
  <aside class="sidebar glass-card">
    <div class="logo">
      <div class="logo-icon">
        <Lock :size="20" color="#6366f1" />
      </div>
      <h2 class="text-gradient">AuthService</h2>
    </div>

    <nav class="nav-links">
      <a href="#" :class="{ active: activeView === 'overview' }" @click.prevent="$emit('changeView', 'overview')">
        <LayoutDashboard :size="18" /> Overview
      </a>
      <a href="#" :class="{ active: activeView === 'tenants' }" @click.prevent="$emit('changeView', 'tenants')">
        <Building2 :size="18" /> Tenants
      </a>
      <a href="#" :class="{ active: activeView === 'applications' }" @click.prevent="$emit('changeView', 'applications')">
        <Globe :size="18" /> Applications
      </a>
      <a href="#" :class="{ active: activeView === 'playground' }" @click.prevent="$emit('changeView', 'playground')">
        <FlaskConical :size="18" /> Playground
      </a>
      <a href="#" :class="{ active: activeView === 'integration' }" @click.prevent="$emit('changeView', 'integration')">
        <Code :size="18" /> Integration
      </a>
      <a href="#"><Settings :size="18" /> Settings</a>
    </nav>

    <div class="user-profile">
      <div class="avatar">VB</div>
      <div class="info">
        <p class="name">Victor Butita</p>
        <p class="email">victor@example.com</p>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { Lock, LayoutDashboard, Globe, Settings, Code, Building2, FlaskConical } from 'lucide-vue-next'

defineProps<{
  activeView: string
}>()

defineEmits(['changeView'])
</script>

<style scoped>
.sidebar {
  width: 260px;
  height: calc(100vh - 40px);
  margin: 20px;
  display: flex;
  flex-direction: column;
  padding: 24px;
  position: fixed;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 48px;
}

.logo-icon {
  background: rgba(99, 102, 241, 0.1);
  padding: 8px;
  border-radius: 10px;
  display: flex;
  align-items: center;
}

.logo h2 {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.nav-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.nav-links a {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  text-decoration: none;
  color: var(--text-muted);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.nav-links a:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-main);
}

.nav-links a.active {
  background: rgba(99, 102, 241, 0.1);
  color: #818cf8;
}

.user-profile {
  margin-top: auto;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 36px;
  height: 36px;
  background: var(--primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}

.info .name {
  font-size: 13px;
  font-weight: 600;
}

.info .email {
  font-size: 11px;
  color: var(--text-muted);
}
</style>
