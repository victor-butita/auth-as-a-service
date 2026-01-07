<template>
  <div class="applications">
    <div class="header">
      <div>
        <h1 class="text-gradient">Applications</h1>
        <p class="subtext">Manage your auth clients and credentials.</p>
        <div class="tenant-selector-inline">
          <label>Viewing for Tenant:</label>
          <select v-model="selectedTenantId" @change="handleTenantChange" class="glass-select">
             <option v-for="t in appStore.tenants" :key="t.id" :value="t.id">{{ t.name }}</option>
             <option v-if="appStore.tenants.length === 0" disabled>No tenants found</option>
          </select>
        </div>
      </div>
      <button class="btn-primary" @click="showCreateModal = true" :disabled="!selectedTenantId">
        <Plus :size="18" /> Create New Application
      </button>
    </div>

    <div class="apps-list glass-card">
      <table class="apps-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Client ID</th>
            <th>Creation Date</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="appStore.loading">
            <td colspan="5" class="py-12 text-center text-muted">
               Loading applications...
            </td>
          </tr>
          <tr v-else-if="appStore.applications.length === 0">
            <td colspan="5" class="py-12 text-center text-muted">
               No applications found for this tenant. Create one to get started!
            </td>
          </tr>
          <tr 
            v-for="app in appStore.applications" 
            :key="app.id"
            :class="{ selected: appStore.selectedApplicationId === app.id }"
            @click="appStore.selectedApplicationId = app.id"
            class="clickable-row"
          >
            <td class="app-name">
              <div class="app-icon">
                <Globe :size="16" />
              </div>
              {{ app.name }}
            </td>
            <td><code>{{ app.clientId }}</code></td>
            <td>{{ new Date().toLocaleDateString() }}</td> <!-- Placeholder for date if not in API -->
            <td><span class="status-pill online">Active</span></td>
            <td>
              <button class="btn-icon-small" @click.stop><Settings :size="14" /></button>
              <button class="btn-icon-small" @click.stop><Key :size="14" /></button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create App Modal -->
    <div v-if="showCreateModal" class="modal-overlay">
      <div class="modal-content glass-card">
        <h2>Create Application</h2>
        <p class="subtext">Give your application a name to get started.</p>
        <div class="form-group">
          <label>Application Name</label>
          <input v-model="newAppForm.name" type="text" placeholder="e.g. My Awesome App" class="glass-input" />
        </div>
        <div class="form-group">
          <label>Redirect URIs (comma separated)</label>
          <input v-model="newAppForm.redirectUris" type="text" placeholder="http://localhost:3000, https://myapp.com" class="glass-input" />
        </div>
        <div class="form-group">
          <label>Define Roles (comma separated)</label>
          <input v-model="newAppForm.roles" type="text" placeholder="ADMIN, USER, EDITOR" class="glass-input" />
        </div>
        <div class="form-group">
          <label>Role-Based Redirects (one per line: ROLE=URL)</label>
          <textarea v-model="newAppForm.roleRedirects" placeholder="ADMIN=https://myapp.com/admin&#10;USER=https://myapp.com/home" class="glass-input glass-textarea" rows="3"></textarea>
        </div>
        <div class="modal-actions">
          <button class="btn-text" @click="closeModal">Cancel</button>
          <button class="btn-primary" @click="handleCreate" :disabled="appStore.loading">
            {{ appStore.loading ? 'Creating...' : 'Create Application' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Globe, Settings, Key } from 'lucide-vue-next'
import { useAppStore } from '../../stores/app'

const appStore = useAppStore()
const showCreateModal = ref(false)
const newAppForm = reactive({
  name: '',
  redirectUris: '',
  roles: '',
  roleRedirects: ''
})
const selectedTenantId = ref('')

onMounted(async () => {
  await appStore.fetchTenants()
  if (appStore.tenants.length > 0 && !selectedTenantId.value) {
    selectedTenantId.value = appStore.tenants[0].id
    appStore.fetchApplications(selectedTenantId.value)
  }
})

const handleTenantChange = () => {
  if (selectedTenantId.value) {
    appStore.fetchApplications(selectedTenantId.value)
  }
}

const closeModal = () => {
  showCreateModal.value = false
  newAppForm.name = ''
  newAppForm.redirectUris = ''
  newAppForm.roles = ''
  newAppForm.roleRedirects = ''
}

const handleCreate = async () => {
  if (!newAppForm.name || !selectedTenantId.value) return
  
  const uris = newAppForm.redirectUris
    .split(',')
    .map(u => u.trim())
    .filter(u => u !== '')

  const roles = newAppForm.roles
    .split(',')
    .map(r => r.trim())
    .filter(r => r !== '')

  const roleRedirectsMap: Record<string, string> = {}
  newAppForm.roleRedirects.split('\n').forEach(line => {
    const [role, url] = line.split('=').map(s => s.trim())
    if (role && url) {
      roleRedirectsMap[role] = url
    }
  })
    
  try {
    await appStore.createApplication(
      selectedTenantId.value, 
      newAppForm.name, 
      uris,
      roles,
      roleRedirectsMap
    )
    closeModal()
  } catch (err) {
    alert('Failed to create application. Check if tenant ID is valid.')
  }
}
</script>

<style scoped>
.applications {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.tenant-selector-inline {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.tenant-selector-inline label {
  color: var(--text-muted);
  font-weight: 600;
}

.glass-select {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  padding: 4px 12px;
  color: white;
  font-size: 13px;
  cursor: pointer;
}

.header h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.header .subtext {
  color: var(--text-muted);
}

.apps-list {
  overflow: hidden;
}

.apps-table {
  width: 100%;
  border-collapse: collapse;
}

.apps-table th {
  text-align: left;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  color: var(--text-muted);
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.apps-table td {
  padding: 16px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 14px;
}

.app-name {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
}

.app-icon {
  background: rgba(255, 255, 255, 0.05);
  padding: 6px;
  border-radius: 6px;
  color: var(--text-muted);
}

code {
  background: rgba(255, 255, 255, 0.05);
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
}

.status-pill {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
}

.status-pill.online {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.clickable-row {
  cursor: pointer;
  transition: background 0.2s;
}

.clickable-row:hover {
  background: rgba(255, 255, 255, 0.05);
}

.clickable-row.selected {
  background: rgba(99, 102, 241, 0.1);
  border-left: 3px solid var(--primary);
}

.btn-icon-small {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--text-muted);
  padding: 6px;
  border-radius: 6px;
  cursor: pointer;
  margin-right: 8px;
  transition: all 0.2s;
}

.btn-icon-small:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 400px;
  padding: 32px;
}

.modal-content h2 {
  margin-bottom: 8px;
}

.form-group {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-muted);
}

.form-group input,
.form-group textarea {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
  color: white;
  font-family: inherit;
}

.glass-textarea {
  resize: vertical;
}

.modal-actions {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

.btn-text {
  background: transparent;
  border: none;
  color: var(--text-muted);
  font-weight: 600;
  cursor: pointer;
}
</style>
