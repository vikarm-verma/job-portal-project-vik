export function friendlyError(error) {
  if (!error) return '';
  if (error.status === 400) return error.message || 'Please check the information you submitted.';
  if (error.status === 401) return 'Your session has expired. Please sign in again.';
  if (error.status === 403) return 'You do not have permission to perform that action.';
  if (error.status === 404) return 'The requested resource was not found.';
  if (error.status === 409) return error.message || 'That action conflicts with existing data.';
  if (error.status >= 500) return 'The server is unavailable right now. Please try again shortly.';
  return error.message || 'Something went wrong. Please try again.';
}
