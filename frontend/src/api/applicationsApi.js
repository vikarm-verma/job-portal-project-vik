import { apiRequest } from './client';

export const applicationsApi = {
  apply: (jobId) => apiRequest('/api/applications', { method: 'POST', body: { jobId } }),
  mine: () => apiRequest('/api/applications/my'),
  updateStatus: (id, status) => apiRequest(`/api/applications/${id}/status`, {
    method: 'PATCH',
    body: { status },
  }),
};
