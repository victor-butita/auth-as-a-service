import { defineStore } from 'pinia'
import axios from 'axios'

const API_BASE = 'http://localhost:8080/api/v1'

export const useAppStore = defineStore('app', {
    state: () => ({
        tenants: [] as any[],
        applications: [] as any[],
        selectedApplicationId: null as string | null,
        currentAnalytics: null as any,
        currentUserId: null as string | null,
        playgroundLogs: [] as any[],
        users: [] as any[],
        loading: false,
        error: null as string | null,
    }),
    actions: {
        async fetchTenants() {
            this.loading = true
            try {
                const res = await axios.get(`${API_BASE}/tenants`)
                this.tenants = res.data
            } catch (err: any) {
                this.error = 'Failed to fetch tenants'
            } finally {
                this.loading = false
            }
        },
        async createTenant(name: string, contactEmail: string) {
            this.loading = true
            try {
                const res = await axios.post(`${API_BASE}/tenants`, { name, contactEmail })
                this.tenants.push(res.data)
                return res.data
            } catch (err: any) {
                this.error = 'Failed to create tenant'
                throw err
            } finally {
                this.loading = false
            }
        },
        async deleteTenant(id: string) {
            this.loading = true
            try {
                await axios.delete(`${API_BASE}/tenants/${id}`)
                this.tenants = this.tenants.filter(t => t.id !== id)
            } catch (err: any) {
                this.error = 'Failed to delete tenant'
                throw err
            } finally {
                this.loading = false
            }
        },
        async fetchApplications(tenantId: string) {
            this.loading = true
            try {
                const res = await axios.get(`${API_BASE}/applications/tenant/${tenantId}`)
                this.applications = res.data
                if (this.applications.length > 0 && !this.selectedApplicationId) {
                    this.selectedApplicationId = this.applications[0].id
                    this.fetchAnalytics(this.selectedApplicationId!)
                }
            } catch (err: any) {
                this.error = 'Failed to fetch applications'
            } finally {
                this.loading = false
            }
        },
        async createApplication(tenantId: string, name: string, redirectUris: string[], roles: string[], roleRedirects: any, identityProviders: string[] = [], registrationFields: string[] = []) {
            this.loading = true
            try {
                const res = await axios.post(`${API_BASE}/applications`, {
                    tenantId,
                    name,
                    redirectUris,
                    roles,
                    roleRedirects,
                    identityProviders,
                    registrationFields
                })
                this.applications.push(res.data)
                return res.data
            } catch (err: any) {
                this.error = 'Failed to create application'
                throw err
            } finally {
                this.loading = false
            }
        },
        async updateApplication(id: string, data: any) {
            this.loading = true
            try {
                const res = await axios.put(`${API_BASE}/applications/${id}`, data)
                const index = this.applications.findIndex(a => a.id === id)
                if (index !== -1) {
                    this.applications[index] = res.data
                }
                return res.data
            } catch (err: any) {
                this.error = 'Failed to update application'
                throw err
            } finally {
                this.loading = false
            }
        },
        async fetchAnalytics(applicationId: string) {
            this.loading = true
            try {
                const res = await axios.get(`${API_BASE}/analytics/application/${applicationId}`)
                this.currentAnalytics = res.data
            } catch (err: any) {
                this.error = 'Failed to fetch analytics'
            } finally {
                this.loading = false
            }
        },
        async deleteApplication(id: string) {
            this.loading = true
            try {
                await axios.delete(`${API_BASE}/applications/${id}`)
                this.applications = this.applications.filter(a => a.id !== id)
                if (this.selectedApplicationId === id) {
                    this.selectedApplicationId = this.applications.length > 0 ? this.applications[0].id : null
                }
            } catch (err: any) {
                this.error = 'Failed to delete application'
                throw err
            } finally {
                this.loading = false
            }
        },
        async fetchUsers(applicationId: string) {
            this.loading = true
            try {
                const res = await axios.get(`${API_BASE}/users/application/${applicationId}`)
                this.users = res.data
            } catch (err: any) {
                this.error = 'Failed to fetch users'
            } finally {
                this.loading = false
            }
        },
        async fetchAllUsers() {
            this.loading = true
            try {
                const res = await axios.get(`${API_BASE}/users`)
                this.users = res.data
            } catch (err: any) {
                this.error = 'Failed to fetch all users'
            } finally {
                this.loading = false
            }
        },
        async rotateClientSecret(id: string) {
            this.loading = true
            try {
                const res = await axios.post(`${API_BASE}/applications/${id}/rotate-secret`)
                const index = this.applications.findIndex(a => a.id === id)
                if (index !== -1) {
                    this.applications[index] = res.data
                }
                return res.data
            } catch (err: any) {
                this.error = 'Failed to rotate client secret'
                throw err
            } finally {
                this.loading = false
            }
        }
    }
})
