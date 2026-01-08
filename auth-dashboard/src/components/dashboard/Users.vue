<template>
  <div class="users-view">
    <div class="view-header">
      <div>
        <h1>User Management</h1>
        <p class="subtext">View and manage users registered across your applications.</p>
      </div>
    </div>

    <div class="glass-card table-container">
      <div class="table-actions">
        <label>Filter by Application:</label>
        <select v-model="selectedFilter" class="glass-input-compact" @change="loadUsers">
          <option value="all">All Applications</option>
          <option v-for="app in appStore.applications" :key="app.id" :value="app.id">
            {{ app.name }}
          </option>
        </select>
        <button class="btn-text-small" @click="loadUsers" :disabled="appStore.loading">
          <RefreshCw :size="14" :class="{ 'spin': appStore.loading }" /> Refresh
        </button>
      </div>

      <table class="glass-table">
        <thead>
          <tr>
            <th>User</th>
            <th>Application</th>
            <th>Roles</th>
            <th>Providers</th>
            <th>Registered At</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td class="user-cell">
              <div class="user-avatar">
                {{ user.email.charAt(0).toUpperCase() }}
              </div>
              <div class="user-info">
                <span class="email">{{ user.email }}</span>
                <span class="id">ID: {{ user.id.substring(0, 8) }}...</span>
              </div>
            </td>
            <td>
              <span class="app-badge">{{ user.applicationName }}</span>
            </td>
            <td>
              <div class="role-chips">
                <span v-for="role in user.roles" :key="role" class="role-chip">{{ role }}</span>
              </div>
            </td>
            <td>
              <div class="provider-icons">
                <div v-for="provider in user.providers" :key="provider" class="provider-tag" :class="provider.toLowerCase()">
                  <img v-if="provider === 'GOOGLE'" src="https://www.gstatic.com/images/branding/product/1x/gsa_512dp.png" alt="Google" width="12" />
                  <img v-if="provider === 'ZOHO'" src="https://www.vectorlogo.zone/logos/zoho/zoho-icon.svg" alt="Zoho" width="12" />
                  <Key v-if="provider === 'PASSWORD'" :size="12" />
                  <span>{{ provider }}</span>
                </div>
              </div>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td><span class="status-pill online">Active</span></td>
          </tr>
          <tr v-if="filteredUsers.length === 0">
            <td colspan="6" class="empty-state">
              No users found.
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '../../stores/app'
import { RefreshCw, Key } from 'lucide-vue-next'

const appStore = useAppStore()
const selectedFilter = ref('all')

const loadUsers = async () => {
  if (selectedFilter.value === 'all') {
    await appStore.fetchAllUsers()
  } else {
    await appStore.fetchUsers(selectedFilter.value)
  }
}

const filteredUsers = computed(() => {
  if (selectedFilter.value === 'all') {
    return appStore.users
  }
  return appStore.users.filter(user => user.applicationId === selectedFilter.value)
})

const formatDate = (dateStr: string) => {
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  } catch (e) {
    return dateStr
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.users-view {
  animation: fadeIn 0.5s ease-out;
}

.view-header {
  margin-bottom: 32px;
}

h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #fff 0%, #a5b4fc 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtext {
  color: var(--text-muted);
  font-size: 16px;
}

.table-container {
  padding: 24px;
}

.table-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.glass-input-compact {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 14px;
}

.glass-table {
  width: 100%;
  border-collapse: collapse;
}

.glass-table th {
  text-align: left;
  padding: 16px;
  color: var(--text-muted);
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.glass-table td {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 14px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--primary) 0%, #818cf8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: white;
  font-size: 14px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-info .email {
  font-weight: 600;
  color: white;
}

.user-info .id {
  font-size: 11px;
  color: var(--text-muted);
}

.role-chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.role-chip {
  background: rgba(99, 102, 241, 0.1);
  color: #818cf8;
  padding: 2px 10px;
  border-radius: 100px;
  font-size: 11px;
  font-weight: 600;
  border: 1px solid rgba(99, 102, 241, 0.2);
}

.app-badge {
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-muted);
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.1);
  display: inline-block;
}

.provider-icons {
  display: flex;
  gap: 8px;
}

.provider-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.05);
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.provider-tag.google {
  color: #fff;
  border-color: rgba(234, 67, 53, 0.2);
}

.provider-tag.zoho {
  color: #fff;
  border-color: rgba(0, 103, 179, 0.2);
}

.status-pill {
  padding: 4px 12px;
  border-radius: 100px;
  font-size: 11px;
  font-weight: 600;
}

.status-pill.online {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.empty-state {
  text-align: center;
  padding: 48px !important;
  color: var(--text-muted);
}

.btn-text-small {
  background: transparent;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: color 0.2s;
}

.btn-text-small:hover {
  color: white;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
