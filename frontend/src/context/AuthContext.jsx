import { createContext, useState, useEffect, useCallback } from 'react';
import authApi from '../api/authApi';

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser]       = useState(null);
  const [loading, setLoading] = useState(true);

  // Au démarrage — essayer de récupérer l'utilisateur via le cookie existant
  useEffect(() => {
    let cancelled = false;

    const initAuth = async () => {
      try {
        const authenticatedUser = await authApi.refresh();
        if (!cancelled) setUser(authenticatedUser);
      } catch {
        if (!cancelled) setUser(null);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    initAuth();

    return () => {
      cancelled = true;
    };
  }, []);

  const login = useCallback(async (credentials) => {
    const authenticatedUser = await authApi.login(credentials);
    setUser(authenticatedUser);
    return authenticatedUser;
  }, []);

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } finally {
      setUser(null);
      window.location.href = '/login';
    }
  }, []);

  const isAuthenticated = () => !!user;

  const hasRole = (...roles) => {
    if (!user?.role) return false;

    const userRole = user.role.replace(/^ROLE_/, '');
    return roles.some((role) => userRole === role.replace(/^ROLE_/, ''));
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, isAuthenticated, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
};
