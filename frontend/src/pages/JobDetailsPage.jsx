import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { jobsApi } from '../api/jobsApi';
import { applicationsApi } from '../api/applicationsApi';
import { useAuth } from '../auth/AuthContext';
import { friendlyError } from '../utils/errors';

export default function JobDetailsPage() {
  const { id } = useParams();
  const { role, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [state, setState] = useState({ loading: true, applying: false, error: '', success: '' });

  useEffect(() => { jobsApi.getById(id).then(setJob).catch((error) => setState({ ...state, loading: false, error: friendlyError(error) })).finally(() => setState((current) => ({ ...current, loading: false }))); }, [id]);
  const apply = async () => { setState({ ...state, applying: true, error: '', success: '' }); try { await applicationsApi.apply(Number(id)); setState({ ...state, applying: false, success: 'Application submitted.' }); } catch (error) { setState({ ...state, applying: false, error: friendlyError(error) }); } };

  if (state.loading) return <div className="loading-state">Loading role...</div>;
  if (state.error && !job) return <div className="empty-state"><p className="form-error">{state.error}</p><Link className="text-link" to="/">Back to jobs</Link></div>;
  return <section className="detail-layout"><div><Link className="back-link" to="/">← All roles</Link><div className="detail-heading"><span className="tag">{job.employmentType?.replace('_', ' ')}</span><h1>{job.title}</h1><p className="company">{job.companyName} · {job.location}</p></div><div className="detail-copy"><h2>The role</h2><p>{job.description}</p><h2>What you’ll bring</h2><p>{job.requiredSkills}</p></div></div><aside className="action-card"><p className="eyebrow">Role details</p><dl><div><dt>Location</dt><dd>{job.location}</dd></div><div><dt>Experience</dt><dd>{job.experienceLevel || 'Flexible'}</dd></div><div><dt>Salary</dt><dd>{job.salaryMax ? `${job.salaryMin} – ${job.salaryMax}` : job.salaryMin || 'Not listed'}</dd></div></dl>{role === 'JOB_SEEKER' ? <button className="button button-primary button-wide" onClick={apply} disabled={state.applying}>{state.applying ? 'Applying...' : 'Apply for this role'}</button> : !isAuthenticated ? <Link className="button button-primary button-wide" to="/login">Sign in to apply</Link> : <p className="muted">Recruiter accounts cannot apply to roles.</p>}{state.success && <p className="success-message">{state.success}</p>}{state.error && <p className="form-error">{state.error}</p>}</aside></section>;
}
