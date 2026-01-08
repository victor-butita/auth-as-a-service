<template>
  <div class="tenants">
    <div class="header">
      <div>
        <h1 class="text-gradient">Tenants</h1>
        <p class="subtext">Manage high-level organizations and insurance partners.</p>
      </div>
      <button class="btn-primary" @click="showCreateModal = true">
        <Plus :size="18" /> Create Tenant
      </button>
    </div>

    <div class="tenants-list glass-card">
      <table class="tenants-table">
        <thead>
          <tr>
            <th>Organization Name</th>
            <th>Contact Email</th>
            <th>ID</th>
            <th class="text-right">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tenant in appStore.tenants" :key="tenant.id">
            <td class="tenant-name">
              <div class="tenant-icon">
                <Building2 :size="16" />
              </div>
              {{ tenant.name }}
            </td>
            <td>{{ tenant.contactEmail }}</td>
            <td><code>{{ tenant.id }}</code></td>
            <td class="text-right">
              <button class="btn-action delete" title="Delete" @click.stop="handleDeleteTenant(tenant)">
                <Trash2 :size="16" />
              </button>
            </td>
          </tr>
          <tr v-if="appStore.tenants.length === 0">
            <td colspan="3" class="text-center py-8 text-muted">No tenants found.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Create Tenant Modal -->
    <div v-if="showCreateModal" class="modal-overlay">
      <div class="modal-content glass-card">
        <h2>Create Tenant</h2>
        <p class="subtext">Register a new organization on the platform.</p>
        <div class="form-group">
          <label>Organization Name</label>
          <input v-model="newTenant.name" type="text" placeholder="e.g. Acme Insurance" class="glass-input" />
        </div>
        <div class="form-group">
          <label>Contact Email</label>
          <input v-model="newTenant.contactEmail" type="email" placeholder="admin@acme.com" class="glass-input" />
        </div>
        <div class="modal-actions">
          <button class="btn-text" @click="showCreateModal = false">Cancel</button>
          <button class="btn-primary" @click="handleCreate" :disabled="appStore.loading">
            {{ appStore.loading ? 'Creating...' : 'Create Tenant' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus, Building2, Trash2 } from 'lucide-vue-next'
import { useAppStore } from '../../stores/app'

const appStore = useAppStore()
const showCreateModal = ref(false)
const newTenant = ref({ name: '', contactEmail: '' })

const handleCreate = async () => {
  if (!newTenant.value.name) return
  try {
    await appStore.createTenant(newTenant.value.name, newTenant.value.contactEmail)
    showCreateModal.value = false
    newTenant.value = { name: '', contactEmail: '' }
  } catch (err) {
    alert('Failed to create tenant')
  }
}

const handleDeleteTenant = async (tenant: any) => {
  if (confirm(`Are you sure you want to delete "${tenant.name}"? This will also affect associated applications.`)) {
    try {
      await appStore.deleteTenant(tenant.id)
    } catch (e) {
      alert('Failed to delete tenant')
    }
  }
}

onMounted(() => {
  appStore.fetchTenants()
})
</script>

<style scoped>
.tenants {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.tenants-table {
  width: 100%;
  border-collapse: collapse;
}

.tenants-table th {
  text-align: left;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  color: var(--text-muted);
  font-size: 13px;
}

.tenants-table td {
  padding: 16px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 14px;
}

.tenant-name {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
}

.tenant-icon {
  background: rgba(99, 102, 241, 0.1);
  padding: 6px;
  border-radius: 6px;
  color: var(--primary);
}

code {
  background: rgba(255, 255, 255, 0.05);
  padding: 4px 8px;
  border-radius: 4px;
  font-family: inherit;
  font-size: 12px;
}

/* Modal and Input styles shared with other components */
.glass-input {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 12px;
  color: white;
  width: 100%;
}

.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
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

.form-group {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.modal-actions {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}
.text-right { text-align: right; }

.btn-action {
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--text-muted);
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-action:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
  border-color: rgba(255, 255, 255, 0.2);
}

.btn-action.delete:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  border-color: rgba(239, 68, 68, 0.2);
}
</style>
