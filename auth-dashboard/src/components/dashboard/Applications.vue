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
            <td class="actions-cell">
              <button class="btn-action edit" @click.stop="openEditModal(app)">
                <Settings :size="14" /> <span>Edit</span>
              </button>
              <button class="btn-action rotate" title="Rotate Secret" @click.stop="appStore.rotateClientSecret(app.id)">
                <Key :size="14" />
              </button>
              <button class="btn-action delete" title="Delete" @click.stop="handleDeleteApplication(app)">
                <Trash2 :size="14" />
              </button>
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
          <textarea v-model="newAppForm.roleRedirects" placeholder="ADMIN=https://myapp.com/admin&#10;USER=https://myapp.com/home" class="glass-input glass-textarea" rows="2"></textarea>
        </div>
        <div class="form-group">
          <label>Required Registration Fields (comma separated)</label>
          <input v-model="newAppForm.registrationFields" type="text" placeholder="ID_NUMBER, PHONE_NUMBER, DEPARTMENT" class="glass-input" />
        </div>
        <div class="form-group">
          <label>Enabled Authentication Methods</label>
          <div class="checkbox-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="newAppForm.identityProviders" value="google" />
              Google SSO
            </label>
            <label class="checkbox-label">
              <input type="checkbox" v-model="newAppForm.identityProviders" value="zoho" />
              Zoho
            </label>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-text" @click="closeModal">Cancel</button>
          <button class="btn-primary" @click="handleCreate" :disabled="appStore.loading">
            {{ appStore.loading ? 'Creating...' : 'Create Application' }}
          </button>
        </div>
      </div>
    </div>
    <!-- Edit App Modal -->
    <div v-if="showEditModal" class="modal-overlay">
      <div class="modal-content glass-card">
        <h2>Edit Application</h2>
        <p class="subtext">Update application settings.</p>
        <div class="form-group">
          <label>Application Name</label>
          <input v-model="editAppForm.name" type="text" class="glass-input" />
        </div>
        <div class="form-group">
          <label>Redirect URIs (comma separated)</label>
          <input v-model="editAppForm.redirectUris" type="text" class="glass-input" />
        </div>
        <div class="form-group">
          <label>Define Roles (comma separated)</label>
          <input v-model="editAppForm.roles" type="text" class="glass-input" />
        </div>
        <div class="form-group">
          <label>Role-Based Redirects (one per line: ROLE=URL)</label>
          <textarea v-model="editAppForm.roleRedirects" class="glass-input glass-textarea" rows="2"></textarea>
        </div>
        <div class="form-group">
          <label>Required Registration Fields (comma separated)</label>
          <input v-model="editAppForm.registrationFields" type="text" class="glass-input" />
        </div>
        <div class="form-group">
          <label>Authentication Providers</label>
          <div class="providers-config">
            <div v-for="provider in ['google', 'zoho']" :key="provider" class="provider-item glass-card">
              <div class="provider-header">
                <input type="checkbox" v-model="editAppForm.identityProviders" :value="provider" />
                <span class="provider-label">{{ provider.toUpperCase() }}</span>
              </div>
              <div v-if="editAppForm.identityProviders.includes(provider)" class="provider-details">
                <input v-model="editAppForm.providerConfigs[provider].clientId" type="text" placeholder="Client ID" class="glass-input-compact" />
                <input v-model="editAppForm.providerConfigs[provider].clientSecret" type="password" placeholder="Client Secret" class="glass-input-compact" />
                <input v-model="editAppForm.providerConfigs[provider].discoveryUrl" type="text" placeholder="Discovery URL (Optional)" class="glass-input-compact" />
              </div>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn-text" @click="showEditModal = false">Cancel</button>
          <button class="btn-primary" @click="handleUpdate" :disabled="appStore.loading">
            {{ appStore.loading ? 'Updating...' : 'Save Changes' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Globe, Settings, Key, Trash2 } from 'lucide-vue-next'
import { useAppStore } from '../../stores/app'

const appStore = useAppStore()
const showCreateModal = ref(false)
const showEditModal = ref(false)
const currentEditId = ref<string | null>(null)

const newAppForm = reactive({
  name: '',
  redirectUris: '',
  roles: '',
  roleRedirects: '',
  identityProviders: [] as string[],
  registrationFields: ''
})

const editAppForm = reactive({
  name: '',
  redirectUris: '',
  roles: '',
  roleRedirects: '',
  identityProviders: [] as string[],
  registrationFields: '',
  providerConfigs: {
    google: { clientId: '', clientSecret: '', discoveryUrl: '' },
    zoho: { clientId: '', clientSecret: '', discoveryUrl: '' }
  } as Record<string, any>
})
const selectedTenantId = ref('')

const handleDeleteApplication = async (app: any) => {
  if (confirm(`Are you sure you want to delete "${app.name}"?`)) {
    try {
      await appStore.deleteApplication(app.id)
    } catch (e) {
      alert('Failed to delete application')
    }
  }
}

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
  newAppForm.identityProviders = []
  newAppForm.registrationFields = ''
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
      roleRedirectsMap,
      newAppForm.identityProviders,
      newAppForm.registrationFields.split(',').map(f => f.trim()).filter(f => f !== '')
    )
    closeModal()
  } catch (err) {
    alert('Failed to create application.')
  }
}

const openEditModal = (app: any) => {
  currentEditId.value = app.id
  editAppForm.name = app.name
  editAppForm.redirectUris = Array.from(app.redirectUris).join(', ')
  editAppForm.roles = Array.from(app.roles).join(', ')
  editAppForm.roleRedirects = Object.entries(app.roleRedirects).map(([r, u]) => `${r}=${u}`).join('\n')
  editAppForm.identityProviders = [...(app.identityProviders || [])]
  editAppForm.registrationFields = Array.from(app.registrationFields || []).join(', ')
  
  // Reset provider configs
  editAppForm.providerConfigs = {
    google: { clientId: '', clientSecret: '', discoveryUrl: '' },
    zoho: { clientId: '', clientSecret: '', discoveryUrl: '' }
  }
  
  // Fill with existing ones
  if (app.providerConfigs) {
    app.providerConfigs.forEach((pc: any) => {
      if (editAppForm.providerConfigs[pc.providerName]) {
        editAppForm.providerConfigs[pc.providerName] = {
          clientId: pc.clientId,
          clientSecret: pc.clientSecret,
          discoveryUrl: pc.discoveryUrl
        }
      }
    })
  }
  
  showEditModal.value = true
}

const handleUpdate = async () => {
  if (!currentEditId.value) return

  const uris = editAppForm.redirectUris
    .split(',')
    .map(u => u.trim())
    .filter(u => u !== '')

  const roles = editAppForm.roles
    .split(',')
    .map(r => r.trim())
    .filter(r => r !== '')

  const roleRedirectsMap: Record<string, string> = {}
  editAppForm.roleRedirects.split('\n').forEach(line => {
    const [role, url] = line.split('=').map(s => s.trim())
    if (role && url) {
      roleRedirectsMap[role] = url
    }
  })

  try {
    await appStore.updateApplication(currentEditId.value, {
      name: editAppForm.name,
      redirectUris: uris,
      roles: roles,
      roleRedirects: roleRedirectsMap,
      identityProviders: editAppForm.identityProviders,
      providerConfigs: editAppForm.identityProviders.map(p => ({
        providerName: p,
        ...editAppForm.providerConfigs[p]
      })),
      registrationFields: editAppForm.registrationFields.split(',').map(f => f.trim()).filter(f => f !== '')
    })
    showEditModal.value = false
  } catch (err) {
    alert('Failed to update application.')
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

.btn-action {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-action.edit {
  background: rgba(99, 102, 241, 0.1);
  border-color: rgba(99, 102, 241, 0.2);
  color: #818cf8;
}

.btn-action:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.btn-action.edit:hover {
  background: var(--primary);
  color: white;
}

.actions-cell {
  display: flex;
  gap: 8px;
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

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: rgba(255, 255, 255, 0.05);
  padding: 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  cursor: pointer;
}

.checkbox-label input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.providers-config {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.provider-item {
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
}

.provider-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.provider-label {
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.provider-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-left: 26px;
}

.glass-input-compact {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  padding: 8px 10px;
  color: white;
  font-size: 12px;
}
</style>
