import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

// Auth Context
import { AuthProvider } from '@/context/AuthContext';

// Layouts & Protections
import AppLayout from '@/layouts/AppLayout';
import ProtectedRoute from '@/components/auth/ProtectedRoute';

// Pages
import LoginPage from '@/features/auth/pages/LoginPage';
import UserManagementPage from '@/features/users/pages/UserManagementPage';
import ConfigurationPage from '@/features/stock/pages/ConfigurationPage';
import ReceptionLotsPage from '@/features/stock/pages/ReceptionLotsPage';
import ProductionCockpitPage from '@/features/production/pages/ProductionCockpitPage';
import AffinagePage from '@/features/production/pages/AffinagePage';
import QualityControlPage from '@/features/quality/pages/QualityControlPage';
import LandingPage from '@/features/landing/pages/LandingPage';
import LivraisonPage from '@/features/delivery/pages/LivraisonPage';
import TraceabilityPage from '@/features/traceability/pages/TraceabilityPage';
import DashboardPage from '@/features/dashboard/pages/DashboardPage';

export default function App() {
  return (
    <AuthProvider>
      <main className="min-h-screen bg-[#101525] antialiased">
        <Routes>
          {/* 1. Route Publique : Sans AppLayout (Plein écran) */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />

          {/* 2. Routes Protégées : Encapsulées dans AppLayout (avec Sidebar) */}
          <Route element={<ProtectedRoute />}>
            <Route element={<AppLayout />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              {/* Redirection par défaut vers la gestion des utilisateurs pour le moment */}
              {/* Page de gestion des utilisateurs */}
              <Route path="/admin/utilisateurs" element={<UserManagementPage />} />
              <Route path="/stock/configuration" element={<ConfigurationPage />} />
              <Route path="/stock/reception-mp" element={<ReceptionLotsPage />} />
              <Route path="/fabrication/production" element={<ProductionCockpitPage />} />
              <Route path="/fabrication/affinage" element={<AffinagePage />} />
              <Route path="/qualite" element={<QualityControlPage />} />
              <Route path="/expedition" element={<LivraisonPage />} />
              <Route path="/tracabilite" element={<TraceabilityPage />} />
              
              {/* Futurs modules (Collecte, Production, Qualité...) */}
              {/* <Route path="/collecte" element={<CollectePage />} /> */}
            </Route>
          </Route>

          {/* Redirection si la route n'existe pas */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </main>
    </AuthProvider>
  );
}