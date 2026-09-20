import React, { useState } from 'react';
import { Link, NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export default function Layout() {
  const { isAuthenticated, role, user, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);
  const home = role === 'RECRUITER' ? '/recruiter/dashboard' : '/';
  const closeMenu = () => setMenuOpen(false);

  return (
    <div className="app-shell">
      <header className="topbar">
        <Link className="brand" to={home} onClick={closeMenu}><span className="brand-mark">J</span><span>Junction</span></Link>
        <button className="menu-toggle" type="button" aria-label="Toggle navigation" aria-expanded={menuOpen} onClick={() => setMenuOpen(!menuOpen)}><span /><span /><span /></button>
        <nav className={`nav-links ${menuOpen ? 'is-open' : ''}`}>
          <NavLink to="/" onClick={closeMenu}>Find jobs</NavLink>
          <a href="#companies" onClick={closeMenu}>Companies</a>
          <a href="#employers" onClick={closeMenu}>For employers</a>
          {role === 'JOB_SEEKER' && <><NavLink to="/applications/my" onClick={closeMenu}>My applications</NavLink><NavLink to="/profile" onClick={closeMenu}>Profile</NavLink></>}
          {role === 'RECRUITER' && <><NavLink to="/recruiter/dashboard" onClick={closeMenu}>Dashboard</NavLink><NavLink to="/recruiter/jobs" onClick={closeMenu}>My jobs</NavLink><NavLink to="/profile" onClick={closeMenu}>Company profile</NavLink></>}
        </nav>
        <div className="account-actions">
          {isAuthenticated ? <><span className="user-chip">{user?.firstName} {user?.lastName}</span><button className="button button-ghost" onClick={logout}>Log out</button></> : <><Link className="button button-ghost" to="/login">Log in</Link><Link className="button button-primary" to="/register">Get started</Link></>}
        </div>
      </header>
      <main className="page"><Outlet /></main>
      <footer className="footer">
        <div className="footer-brand"><span className="brand-mark">J</span><div><strong>Junction</strong><span>Better work starts here.</span></div></div>
        <div className="footer-links"><a href="#companies">Companies</a><a href="#employers">For employers</a><Link to="/">Find jobs</Link></div>
        <small>© 2026 Junction. Built for the next good move.</small>
      </footer>
    </div>
  );
}
