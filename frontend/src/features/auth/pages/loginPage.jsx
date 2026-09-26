// src/features/auth/pages/LoginPage.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Lock, User, AlertCircle, Loader2, ShieldCheck, GitFork, ArrowRight } from 'lucide-react';
import loginBg from '@/assets/images/login-bg.jpg';

export default function LoginPage() {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials((prev) => ({ ...prev, [name]: value }));
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!credentials.username || !credentials.password) {
      setError('Veuillez remplir tous les champs.');
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      await login(credentials);
      navigate('/admin/utilisateurs');
    } catch (err) {
      console.error('Login error:', err);
      setError(
        err.response?.data?.message || 
        'Identifiants incorrects ou serveur indisponible.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full flex bg-white font-sans">
      
      {/* ========================================================= */}
      {/* PARTIE GAUGHE (50%) : Formulaire de connexion           */}
      {/* ========================================================= */}
      <div className="w-full lg:w-1/2 flex flex-col justify-between p-8 sm:p-12 lg:p-16 bg-white z-10">
        
        {/* Top Header : Logo & Nom de Marque */}
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-[#101525] rounded-lg flex items-center justify-center shadow-md border border-slate-700">
            <span className="text-[#FFD700] font-black text-xl tracking-wider">TC</span>
          </div>
          <div>
            <span className="text-xl font-bold text-[#101525] tracking-tight block leading-none">
              TRUST<span className="text-amber-500">CHEESE</span>
            </span>
            <span className="text-[9px] font-mono font-bold text-slate-400 uppercase tracking-widest mt-1 block">
              Traçabilité industriel
            </span>
          </div>
        </div>

        {/* Formulaire au Centre */}
        <div className="my-auto max-w-md w-full mx-auto py-8">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-[#101525] tracking-tight">
              Espace de Connexion
            </h1>
            <p className="text-sm text-slate-500 mt-2">
              Saisissez vos identifiants pour accéder aux modules de gestion, de contrôle qualité et de traçabilité.
            </p>
          </div>

          {/* Affichage des Erreurs */}
          {error && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-center gap-3 text-red-700 text-sm animate-shake">
              <AlertCircle size={18} className="flex-shrink-0 text-red-600" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-[#101525] uppercase tracking-wider mb-2">
                Nom d'utilisateur ou Email
              </label>
              <div className="relative">
                <User className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                <input
                  type="text"
                  name="username"
                  value={credentials.username}
                  onChange={handleChange}
                  placeholder="Ex: admin_qc"
                  className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:border-[#101525] focus:bg-white transition-all placeholder:text-slate-400"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-[#101525] uppercase tracking-wider mb-2">
                Mot de passe
              </label>
              <div className="relative">
                <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                <input
                  type="password"
                  name="password"
                  value={credentials.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm focus:outline-none focus:border-[#101525] focus:bg-white transition-all placeholder:text-slate-400"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full py-3.5 px-4 bg-[#FFD700] hover:bg-amber-400 text-[#101525] font-bold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 flex items-center justify-center gap-2 cursor-pointer disabled:opacity-70 disabled:cursor-not-allowed mt-4"
            >
              {isLoading ? (
                <>
                  <Loader2 size={18} className="animate-spin" />
                  <span>Authentification en cours...</span>
                </>
              ) : (
                <>
                  <span>SE CONNECTER</span>
                  <ArrowRight size={18} />
                </>
              )}
            </button>
          </form>
        </div>

        {/* Footer */}
        <div className="text-xs text-slate-400 border-t border-slate-100 pt-4 flex justify-between items-center">
          <span>TrustCheese © 2026</span>
          <span className="font-mono text-[10px] bg-slate-100 px-2 py-1 rounded text-slate-500">v1.0.0-PROD</span>
        </div>
      </div>

      {/* ========================================================= */}
      {/* PARTIE DROITE (50%) : Visuel Traçabilité & Branding      */}
      {/* ========================================================= */}
      <div className="hidden lg:flex w-1/2 bg-[#101525] relative overflow-hidden flex-col justify-between p-16 text-white">
        
        {/* Image de fond avec overlay sombre pour assurer la lisibilité */}
        <div 
          className="absolute inset-0 bg-cover bg-center opacity-25 mix-blend-luminosity scale-105 transition-transform duration-10000 hover:scale-100"
          style={{ 
            backgroundImage: `url(${loginBg})`
          }}
        />
        
        {/* Gradient superposé aux couleurs de la marque */}
        <div className="absolute inset-0 bg-gradient-to-t from-[#101525] via-[#101525]/80 to-transparent" />

        {/* Badge en haut à droite */}
        <div className="relative z-10 self-end">
          <span className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-xs font-semibold text-amber-300">
            <ShieldCheck size={14} />
            Système Conforme GS1 & HACCP
          </span>
        </div>

        {/* Message Métier & Définition de la Traçabilité */}
        <div className="relative z-10 max-w-lg space-y-6">
          <div className="w-12 h-12 bg-[#FFD700] rounded-2xl flex items-center justify-center text-[#101525] shadow-lg">
            <GitFork size={24} />
          </div>

          <h2 className="text-3xl font-extrabold tracking-tight text-white leading-tight">
            La traçabilité totale, du lot de lait jusqu'au client final.
          </h2>

          <p className="text-slate-300 text-sm leading-relaxed">
            TrustCheese assure la généalogie complète de votre production fromagère : enregistrement des réceptions de matières premières, suivi fin des caves d'affinage, validation des contrôles qualité et numérotation des lots GTIN.
          </p>

          {/* Indicateurs / KPIs clés */}
          <div className="grid grid-cols-3 gap-4 pt-4 border-t border-white/10">
            <div>
              <span className="block text-xl font-bold text-[#FFD700]">100%</span>
              <span className="text-[11px] text-slate-400">Transparence Lots</span>
            </div>
            <div>
              <span className="block text-xl font-bold text-[#FFD700]">Double</span>
              <span className="text-[11px] text-slate-400">Entrée Stock</span>
            </div>
            <div>
              <span className="block text-xl font-bold text-[#FFD700]">0 Risk</span>
              <span className="text-[11px] text-slate-400">Non-Conformité</span>
            </div>
          </div>
        </div>

        {/* Footer de la partie visuelle */}
        <div className="relative z-10 text-xs text-slate-500">
          Plateforme de gestion de traçabilité fromagère industrielle.
        </div>
      </div>

    </div>
  );
}