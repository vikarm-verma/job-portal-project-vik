import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { friendlyError } from '../utils/errors';

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [state, setState] = useState({ loading: false, error: '' });

  const submit = async (event) => {
    event.preventDefault();
    setState({ loading: true, error: '' });
    try {
      const auth = await login(form);
      const destination = location.state?.from?.pathname || (auth.role === 'RECRUITER' ? '/recruiter/dashboard' : '/');
      navigate(destination, { replace: true });
    } catch (error) {
      setState({ loading: false, error: friendlyError(error) });
    }
  };

  return <AuthLayout title="Welcome back" subtitle="Pick up where your next opportunity starts.">
    <form className="stack-form" onSubmit={submit}>
      <Field label="Email" type="email" value={form.email} onChange={(email) => setForm({ ...form, email })} required />
      <Field label="Password" type="password" value={form.password} onChange={(password) => setForm({ ...form, password })} required />
      {state.error && <p className="form-error">{state.error}</p>}
      <button className="button button-primary button-wide" disabled={state.loading}>{state.loading ? 'Signing in...' : 'Sign in'}</button>
      <p className="form-note">New here? <Link to="/register">Create an account</Link></p>
    </form>
  </AuthLayout>;
}

export function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', password: '', role: 'JOB_SEEKER' });
  const [state, setState] = useState({ loading: false, error: '' });

  const update = (key, value) => setForm({ ...form, [key]: value });
  const submit = async (event) => {
    event.preventDefault();
    setState({ loading: true, error: '' });
    try {
      const auth = await register(form);
      navigate(auth.role === 'RECRUITER' ? '/recruiter/dashboard' : '/profile', { replace: true });
    } catch (error) {
      setState({ loading: false, error: friendlyError(error) });
    }
  };

  return <AuthLayout title="Make your next move" subtitle="Create a profile that gets you closer to the right work.">
    <form className="stack-form" onSubmit={submit}>
      <div className="form-grid"><Field label="First name" value={form.firstName} onChange={(value) => update('firstName', value)} required /><Field label="Last name" value={form.lastName} onChange={(value) => update('lastName', value)} required /></div>
      <Field label="Email" type="email" value={form.email} onChange={(value) => update('email', value)} required />
      <Field label="Password" type="password" value={form.password} onChange={(value) => update('password', value)} minLength="8" required />
      <label className="field"><span>I am joining as</span><select value={form.role} onChange={(event) => update('role', event.target.value)}><option value="JOB_SEEKER">Job seeker</option><option value="RECRUITER">Recruiter</option></select></label>
      {state.error && <p className="form-error">{state.error}</p>}
      <button className="button button-primary button-wide" disabled={state.loading}>{state.loading ? 'Creating account...' : 'Create account'}</button>
      <p className="form-note">Already registered? <Link to="/login">Sign in</Link></p>
    </form>
  </AuthLayout>;
}

function AuthLayout({ title, subtitle, children }) {
  return <section className="auth-layout"><div className="auth-aside"><p className="eyebrow">Junction / 2026</p><h1>{title}</h1><p>{subtitle}</p></div><div className="auth-card"><div className="card-kicker">Your workspace</div>{children}</div></section>;
}

function Field({ label, type = 'text', value, onChange, ...props }) {
  return <label className="field"><span>{label}</span><input type={type} value={value} onChange={(event) => onChange(event.target.value)} {...props} /></label>;
}
