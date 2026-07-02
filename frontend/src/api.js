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

export default api;
