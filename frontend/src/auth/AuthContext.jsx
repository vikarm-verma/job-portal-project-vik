import React, { createContext, useContext, useEffect, useState } from 'react';
import { authApi } from '../api/authApi';
import { clearStoredAuth, getStoredAuth, storeAuth } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => getStoredAuth());

  useEffect(() => {
    const handleUnauthorized = () => setAuth(null);
    window.addEventListener('jobportal:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('jobportal:unauthorized', handleUnauthorized);
  }, []);

  const authenticate = async (operation, payload) => {
    const result = await operation(payload);
    storeAuth(result);
    setAuth(result);
    return result;
  };

  const login = (payload) => authenticate(authApi.login, payload);
  const register = (payload) => authenticate(authApi.register, payload);
  const logout = () => {
    clearStoredAuth();
    setAuth(null);
  };

  const value = {
    auth,
    user: auth,
    isAuthenticated: Boolean(auth?.token),
    role: auth?.role || null,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used inside AuthProvider');
  return context;
}
