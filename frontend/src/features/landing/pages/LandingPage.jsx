import React from 'react';
import Navbar from '../components/Navbar';
import HeroSection from '../components/HeroSection';
import HexagonFeatures from '../components/HexagonFeatures';
import ManufacturingCycle from '../components/ManufacturingCycle';
import MetricsAndFooter from '../components/MetricsAndFooter';

export default function LandingPage() {
  return <div className="min-h-screen bg-[#F8F9FA] font-sans"><Navbar /><main><HeroSection /><HexagonFeatures /><ManufacturingCycle /><MetricsAndFooter /></main></div>;
}
