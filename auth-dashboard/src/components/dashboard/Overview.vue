<template>
  <div class="overview">
    <div class="welcome-section">
      <div class="header-with-selector">
        <div>
          <h1 class="text-gradient">{{ greeting }}, Victor</h1>
          <p class="subtext">Here's what's happening across your applications.</p>
        </div>
        <div class="app-selector glass-card">
          <Globe :size="16" />
          <select v-model="appStore.selectedApplicationId" @change="handleAppChange" class="clean-select">
            <option v-for="app in appStore.applications" :key="app.id" :value="app.id">
              {{ app.name }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <div class="stats-grid">
      <StatCard 
        label="Total Users" 
        :value="appStore.currentAnalytics?.totalUsers || '0'" 
        :trend="12" 
        :icon="Users" 
      />
      <StatCard 
        label="Active Sessions" 
        :value="appStore.currentAnalytics?.activeSessions || '0'" 
        :trend="5" 
        :icon="Zap" 
      />
      <StatCard 
        label="Registrations" 
        :value="appStore.currentAnalytics?.totalRegistrations || '0'" 
        :trend="2" 
        :icon="UserPlus" 
      />
      <StatCard 
        label="Logins" 
        :value="appStore.currentAnalytics?.totalLogins || '0'" 
        :trend="24" 
        :icon="Activity" 
      />
    </div>

    <div class="bottom-grid">
      <div class="recent-activity glass-card">
        <div class="card-header">
          <h3>Recent Activity</h3>
          <button class="btn-text">View All</button>
        </div>
        <div v-if="appStore.loading" class="loading-state">Loading...</div>
        <div v-else-if="!appStore.selectedApplicationId" class="empty-state">
          Select an application to see activity
        </div>
        <div v-else class="activity-list">
          <div v-for="event in appStore.currentAnalytics?.recentEvents" :key="event.id" class="activity-item">
            <div class="activity-icon">
              <UserCheck :size="16" />
            </div>
            <div class="activity-info">
              <p class="activity-title">{{ formatEventType(event.type) }}</p>
              <p class="activity-time">
                {{ formatTime(event.timestamp) }} • {{ event.ipAddress || 'Unknown IP' }} 
                <span class="ua-chip" :title="event.userAgent">UA Info</span>
              </p>
            </div>
            <div class="activity-status">Success</div>
          </div>
          <div v-if="appStore.currentAnalytics?.recentEvents.length === 0" class="text-center py-8 text-muted">
            No recent activity
          </div>
        </div>
      </div>

      <div class="app-health glass-card">
        <div class="card-header">
          <h3>Application Health</h3>
        </div>
        <div class="health-metrics">
          <div v-for="(status, service) in appStore.currentAnalytics?.systemHealth" :key="service" class="metric">
            <span>{{ service }}</span>
            <div :class="['status-pill', status.toLowerCase() === 'online' ? 'online' : 'offline']">
              {{ status }}
            </div>
          </div>
          <div v-if="!appStore.currentAnalytics?.systemHealth" class="text-muted">
             Select an application to see health status
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, watch, onMounted } from 'vue'
import { Users, Zap, UserPlus, Activity, UserCheck, Globe } from 'lucide-vue-next'
import StatCard from './StatCard.vue'
import { useAppStore } from '../../stores/app'

const appStore = useAppStore()

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return 'Good morning'
  if (hour < 17) return 'Good afternoon'
  return 'Good evening'
})

const handleAppChange = () => {
  if (appStore.selectedApplicationId) {
    appStore.fetchAnalytics(appStore.selectedApplicationId)
  }
}

const formatEventType = (type: string) => {
  return type.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
}

const formatTime = (timestamp: string) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

watch(() => appStore.selectedApplicationId, (newId) => {
  if (newId) {
    appStore.fetchAnalytics(newId)
  }
})

onMounted(() => {
  if (appStore.selectedApplicationId) {
    appStore.fetchAnalytics(appStore.selectedApplicationId)
  }
})
</script>

<style scoped>
.overview {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.header-with-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-selector {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 12px;
}

.clean-select {
  background: transparent;
  border: none;
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  outline: none;
}

.clean-select option {
  background: #1e1b4b;
  color: white;
}

.welcome-section h1 {
  font-size: 32px;
  font-weight: 800;
  margin-bottom: 8px;
}

.welcome-section .subtext {
  color: var(--text-muted);
}

.stats-grid {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.bottom-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.recent-activity, .app-health {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.btn-text {
  background: transparent;
  border: none;
  color: var(--primary);
  font-weight: 600;
  cursor: pointer;
  font-size: 14px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.activity-icon {
  background: rgba(99, 102, 241, 0.1);
  color: var(--primary);
  padding: 8px;
  border-radius: 8px;
}

.activity-info {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  font-weight: 600;
}

.activity-time {
  font-size: 12px;
  color: var(--text-muted);
}

.activity-status {
  font-size: 12px;
  color: #10b981;
  font-weight: 600;
}

.loading-state, .empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: var(--text-muted);
  font-size: 14px;
}

.ua-chip {
  background: rgba(255, 255, 255, 0.05);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  cursor: help;
  margin-left: 8px;
}

.ua-chip:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.health-metrics {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.metric {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.status-pill {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
}

.status-pill.online {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.status-pill.offline {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}
</style>
