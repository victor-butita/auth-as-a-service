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
        async createApplication(
            tenantId: string,
            name: string,
            redirectUris: string[] = [],
            roles: string[] = [],
            roleRedirects: Record<string, string> = {}
        ) {
            this.loading = true
            try {
                const res = await axios.post(`${API_BASE}/applications`, {
                    tenantId,
                    name,
                    redirectUris,
                    roles,
                    roleRedirects
                })
                this.applications.push(res.data)
                return res.data
            } catch (err: any) {
                console.error('Failed to create application:', err.response?.data || err.message)
                this.error = 'Failed to create application'
                throw err
            } finally {
                this.loading = false
            }
        },
        async fetchAnalytics(applicationId: string) {
            console.log('Fetching analytics for:', applicationId)
            this.loading = true
            try {
                const res = await axios.get(`${API_BASE}/analytics/application/${applicationId}`)
                console.log('Analytics response:', res.data)
                this.currentAnalytics = res.data
            } catch (err: any) {
                console.error('Failed to fetch analytics:', err)
                this.error = 'Failed to fetch analytics'
            } finally {
                this.loading = false
            }
        }
    }
})
