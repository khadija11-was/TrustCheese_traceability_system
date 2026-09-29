import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Lock, User, AlertCircle, Loader2, ShieldCheck, GitFork, ArrowRight, Eye, EyeOff, ArrowLeft } from 'lucide-react';
import loginBg from '@/assets/images/login-bg.jpg';

export default function LoginPage() {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
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
      setError('Veuillez remplir tous les champs requis.');
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
    <div className="min-h-screen w-full flex bg-[#F8FAFC] font-sans">
      
      {/* ========================================================= */}
      {/* PARTIE GAUCHE (50%) : Formulaire de connexion             */}
      {/* ========================================================= */}
      <div className="w-full lg:w-1/2 flex flex-col justify-between p-6 sm:p-12 lg:p-16 bg-white z-10 shadow-2xl lg:shadow-none">
        
        {/* Top Header : Logo & Retour */}
        <div className="flex items-center justify-between">
          <Link to="/" className="flex items-center gap-3 group">
            <img 
              src="/logo.png" 
              alt="TrustCheese Logo" 
              className="h-24 md:h-20 w-auto object-contain transition-transform duration-300 group-hover:scale-105"
            />
          </Link>

          <Link 
            to="/" 
            className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-[#0F2027] transition-colors bg-slate-100 hover:bg-slate-200 px-3 py-2 rounded-lg"
          >
            <ArrowLeft size={14} /> Accueil
          </Link>
        </div>

        {/* Formulaire au Centre */}
        <div className="my-auto max-w-md w-full mx-auto py-8">
          <div className="mb-8">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-md bg-[#F2C94C]/15 border border-[#F2C94C]/30 text-[#0F2027] text-xs font-bold uppercase tracking-wider mb-3">
              <ShieldCheck size={14} className="text-[#F2994A]" /> Espace Sécurisé
            </div>
            <h1 className="text-3xl font-black text-[#0F2027] tracking-tight sm:text-4xl">
              Connexion
            </h1>
            <p className="text-sm text-slate-500 mt-2 leading-relaxed">
              Saisissez vos identifiants pour accéder au système de traçabilité et de contrôle qualité.
            </p>
          </div>

          {/* Alert Message Erreur */}
          {error && (
            <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded-r-xl flex items-center gap-3 text-red-800 text-sm shadow-sm">
              <AlertCircle size={20} className="flex-shrink-0 text-red-600" />
              <span className="font-medium">{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Nom d'utilisateur */}
            <div>
              <label className="block text-xs font-extrabold text-[#0F2027] uppercase tracking-wider mb-2">
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
                  className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm font-medium focus:outline-none focus:border-[#F2994A] focus:bg-white focus:ring-2 focus:ring-[#F2994A]/20 transition-all placeholder:text-slate-400"
                  required
                />
              </div>
            </div>

            {/* Mot de passe */}
            <div>
              <div className="flex items-center justify-between mb-2">
                <label className="block text-xs font-extrabold text-[#0F2027] uppercase tracking-wider">
                  Mot de passe
                </label>
              </div>
              <div className="relative">
                <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={credentials.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-10 py-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-900 text-sm font-medium focus:outline-none focus:border-[#F2994A] focus:bg-white focus:ring-2 focus:ring-[#F2994A]/20 transition-all placeholder:text-slate-400"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 focus:outline-none"
                  aria-label="Afficher ou masquer le mot de passe"
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {/* Bouton de Soumission */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full py-3.5 px-4 bg-gradient-to-r from-[#F2994A] to-[#F2C94C] hover:brightness-105 text-[#0F2027] font-extrabold rounded-xl shadow-lg shadow-[#F2994A]/20 transition-all duration-200 flex items-center justify-center gap-2 cursor-pointer disabled:opacity-70 disabled:cursor-not-allowed active:scale-[0.99] mt-6"
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
          <span className="font-mono text-[10px] bg-slate-100 px-2.5 py-1 rounded-md font-semibold text-slate-600 border border-slate-200">
            v1.0.0-PROD
          </span>
        </div>
      </div>

      {/* ========================================================= */}
      {/* PARTIE DROITE (50%) : Visuel Traçabilité & Branding      */}
      {/* ========================================================= */}
      <div className="hidden lg:flex w-1/2 bg-[#0F2027] relative overflow-hidden flex-col justify-between p-16 text-white">
        
        {/* Image de fond avec overlay sombre */}
        <div 
          className="absolute inset-0 bg-cover bg-center opacity-20 mix-blend-luminosity scale-105 transition-transform duration-10000 hover:scale-100"
          style={{ 
            backgroundImage: `url(${loginBg})`
          }}
        />
        
        {/* Gradients superposés */}
        <div className="absolute inset-0 bg-gradient-to-t from-[#0F2027] via-[#0F2027]/80 to-transparent" />
        <div className="absolute -left-32 -top-32 h-96 w-96 rounded-full bg-[#2C5364]/40 blur-3xl" />

        {/* Badge en haut à droite */}
        <div className="relative z-10 self-end">
          <span className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-xs font-semibold text-[#F2C94C]">
            <ShieldCheck size={14} />
            Conforme Normes HACCP & GS1
          </span>
        </div>

        {/* Message Métier & Définition */}
        <div className="relative z-10 max-w-lg space-y-6">
          <div className="w-12 h-12 bg-gradient-to-r from-[#F2994A] to-[#F2C94C] rounded-2xl flex items-center justify-center text-[#0F2027] shadow-lg shadow-[#F2994A]/20">
            <GitFork size={26} />
          </div>

          <h2 className="text-3xl font-black tracking-tight text-white leading-tight">
            La traçabilité industrielle,<br />du lot de lait au client final.
          </h2>

          <p className="text-slate-300 text-sm leading-relaxed">
            TrustCheese assure la généalogie complète de votre production fromagère : suivi des réceptions, gestion des caves d'affinage, validation qualité et traçabilité des lots en temps réel.
          </p>

          {/* Indicateurs / KPIs clés */}
          <div className="grid grid-cols-3 gap-4 pt-6 border-t border-white/10">
            <div>
              <span className="block text-2xl font-black text-[#F2C94C]">100%</span>
              <span className="text-[11px] font-medium text-slate-400">Transparence Lots</span>
            </div>
            <div>
              <span className="block text-2xl font-black text-[#F2C94C]">Double</span>
              <span className="text-[11px] font-medium text-slate-400">Entrée Stock</span>
            </div>
            <div>
              <span className="block text-2xl font-black text-[#F2C94C]">Zero</span>
              <span className="text-[11px] font-medium text-slate-400">Non-Conformité</span>
            </div>
          </div>
        </div>

        {/* Footer de la partie visuelle */}
        <div className="relative z-10 text-xs text-slate-500">
          Système de gestion de la qualité et de la traçabilité laitière.
        </div>
      </div>

    </div>
  );
}