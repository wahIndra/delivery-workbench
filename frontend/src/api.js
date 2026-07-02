import axios from 'axios';

// Create an Axios instance
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// For MVP, we will mock authentication by sending a custom header based on selected role in LocalStorage
api.interceptors.request.use((config) => {
  const userRole = localStorage.getItem('mockRole') || 'BUSINESS_USER';
  const username = localStorage.getItem('mockUsername') || 'business.user';
  
  // Custom header to tell backend who is acting
  // Note: in a real app this would be an Authorization Bearer token.
  // The backend uses a mock JWT filter or simple Header filter for MVP if implemented,
  // but if we are just calling standard endpoints we might need to rely on Spring Security configuration.
  // For now, if the backend uses standard Spring Security with Basic Auth or similar, 
  // we might need to send credentials. Let's assume the backend relies on SecurityUtils.getCurrentUsername()
  // which may come from a mock filter if it exists. 
  config.headers['X-Mock-Username'] = username;
  config.headers['X-Mock-Role'] = userRole;

  return config;
});

export const priorityScoreApi = {
  getScore: (requestId) => api.get(`/requests/${requestId}/priority-score`),
  updateScore: (requestId, data) => api.put(`/requests/${requestId}/priority-score`, data),
  generateAIScore: (requestId) => api.post(`/requests/${requestId}/priority-score/generate-ai`),
};

export const requirementApi = {
  get: (requestId) => api.get(`/requests/${requestId}/requirement`),
  save: (requestId, data) => api.put(`/requests/${requestId}/requirement`, data),
  generateAi: (requestId) => api.post(`/requests/${requestId}/requirement/ai-generate`),
  getVersions: (requestId) => api.get(`/requests/${requestId}/requirement/versions`)
};

export const slaApi = {
  getAgingForRequest: (requestId) => api.get(`/requests/${requestId}/aging`),
  getDashboardMetrics: () => api.get(`/dashboard/sla-metrics`)
};

export const bottleneckApi = {
  analyzeRequest: (requestId) => api.post(`/requests/${requestId}/bottlenecks/analyze`),
  getFindingsForRequest: (requestId) => api.get(`/requests/${requestId}/bottlenecks`),
  getActiveFindings: () => api.get(`/dashboard/bottlenecks/active`),
  updateStatus: (requestId, findingId, status) => api.put(`/requests/${requestId}/bottlenecks/${findingId}/status`, { status })
};

export const nextBestActionApi = {
  generate: (requestId) => api.post(`/requests/${requestId}/next-best-actions/generate`),
  getActions: (requestId) => api.get(`/requests/${requestId}/next-best-actions`),
  updateStatus: (requestId, actionId, status) => api.put(`/requests/${requestId}/next-best-actions/${actionId}/status`, { status })
};

export const decisionLogApi = {
  create: (requestId, data) => api.post(`/requests/${requestId}/decision-logs`, data),
  getLogs: (requestId) => api.get(`/requests/${requestId}/decision-logs`)
};

export const meetingNoteApi = {
  create: (requestId, data) => api.post(`/requests/${requestId}/meeting-notes`, data),
  getNotes: (requestId) => api.get(`/requests/${requestId}/meeting-notes`),
  summarize: (requestId, rawNotes) => api.post(`/requests/${requestId}/meeting-notes/summarize`, { rawNotes })
};

export const attachmentApi = {
  upload: (requestId, formData) => api.post(`/requests/${requestId}/attachments`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  getList: (requestId) => api.get(`/requests/${requestId}/attachments`),
  delete: (requestId, attachmentId) => api.delete(`/requests/${requestId}/attachments/${attachmentId}`)
  // For download, we usually do it via a direct href link using the API base URL to rely on browser download behavior.
};

export const approvalApi = {
  getForRequest: (requestId) => api.get(`/requests/${requestId}/approvals`),
  requestApproval: (requestId, data) => api.post(`/requests/${requestId}/approvals`, data),
  processApproval: (requestId, approvalId, data) => api.put(`/requests/${requestId}/approvals/${approvalId}`, data)
};

export const notificationApi = {
  getMyNotifications: () => api.get('/notifications'),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  markAsRead: (id) => api.put(`/notifications/${id}/read`)
};

export default api;
