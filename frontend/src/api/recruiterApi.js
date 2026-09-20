import { apiRequest, buildQuery } from './client';

export const recruiterApi = {
  jobs: (params = {}) => apiRequest(`/api/recruiter/jobs${buildQuery(params)}`),
  dashboard: () => apiRequest('/api/recruiter/dashboard'),
  applications: (jobId) => apiRequest(`/api/recruiter/jobs/${jobId}/applications`),
};
