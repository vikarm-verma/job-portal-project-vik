import { apiRequest, buildQuery } from './client';

export const jobsApi = {
  search: (params = {}) => apiRequest(`/api/jobs${buildQuery(params)}`),
  getById: (id) => apiRequest(`/api/jobs/${id}`),
  create: (payload) => apiRequest('/api/jobs', { method: 'POST', body: payload }),
  update: (id, payload) => apiRequest(`/api/jobs/${id}`, { method: 'PUT', body: payload }),
  remove: (id) => apiRequest(`/api/jobs/${id}`, { method: 'DELETE' }),
  applications: (jobId) => apiRequest(`/api/jobs/${jobId}/applications`),
};
