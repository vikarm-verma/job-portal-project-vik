import { apiRequest } from './client';

export const profileApi = {
  get: () => apiRequest('/api/profile'),
  update: (payload) => apiRequest('/api/profile', { method: 'PUT', body: payload }),
  uploadResume: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiRequest('/api/profile/resume', { method: 'POST', body: formData });
  },
  getResume: () => apiRequest('/api/profile/resume'),
};
