import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const ProtectedRoute = ({ roles }) => {
  const { user, loading, isAuthenticated, hasRole } = useAuth();

  // 1. Pendant le chargement de la session (ex: vérification du token JWT)
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#101525] text-slate-300">
        Chargement...
      </div>
    );
  }

  // 2. Si l'utilisateur n'est pas connecté -> Redirection vers /login
  if (!isAuthenticated()) return <Navigate to="/login" replace />;

  // 3. Si des rôles spécifiques sont requis et que l'utilisateur ne les a pas
  if (roles && !hasRole(...roles)) return <Navigate to="/unauthorized" replace />;

  // 4. Si tout est OK, Outlet affiche le composant enfant (ici AppLayout)
  return <Outlet />;
};

export default ProtectedRoute;