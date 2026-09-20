import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { jobsApi } from '../api/jobsApi';
import { recruiterApi } from '../api/recruiterApi';
import { applicationsApi } from '../api/applicationsApi';
import { friendlyError } from '../utils/errors';

const emptyJob = { title: '', description: '', location: '', employmentType: 'FULL_TIME', salaryMin: '', salaryMax: '', experienceLevel: '', requiredSkills: '', status: 'OPEN' };

export function RecruiterDashboardPage() {
  const [data, setData] = useState(null); const [state, setState] = useState({ loading: true, error: '' });
  useEffect(() => { recruiterApi.dashboard().then(setData).catch((error) => setState({ loading: false, error: friendlyError(error) })).finally(() => setState((s) => ({ ...s, loading: false }))); }, []);
  if (state.loading) return <div className="loading-state">Loading dashboard...</div>;
  return <section><div className="page-heading"><p className="eyebrow">Recruiter workspace</p><h1>See the signal in your hiring.</h1><p>One view for your roles and the people behind the applications.</p></div>{state.error && <p className="form-error">{state.error}</p>}<div className="metric-grid"><Metric label="Open roles" value={data?.totalJobs || 0} /><Metric label="Total applicants" value={data?.totalApplicants || 0} />{Object.entries(data?.applicationCountsByStatus || {}).map(([key, value]) => <Metric key={key} label={key} value={value} />)}</div><div className="panel dashboard-callout"><div><p className="eyebrow">Keep moving</p><h2>Review applicants while the context is fresh.</h2></div><Link className="button button-primary" to="/recruiter/jobs">View my jobs</Link></div></section>;
}

export function RecruiterJobsPage() {
  const [jobs, setJobs] = useState({ content: [] }); const [editing, setEditing] = useState(null); const [state, setState] = useState({ loading: true, saving: false, error: '', success: '' });
  const load = () => { setState((s) => ({ ...s, loading: true })); recruiterApi.jobs({ page: 0, size: 50, sort: 'createdAt,desc' }).then(setJobs).catch((error) => setState((s) => ({ ...s, error: friendlyError(error) }))).finally(() => setState((s) => ({ ...s, loading: false }))); };
  useEffect(load, []);
  const remove = async (id) => { if (!window.confirm('Delete this job?')) return; try { await jobsApi.remove(id); load(); } catch (error) { setState((s) => ({ ...s, error: friendlyError(error) })); } };
  return <section><div className="page-heading inline-heading"><div><p className="eyebrow">Recruiter workspace</p><h1>My jobs</h1></div><button className="button button-primary" onClick={() => setEditing(editing ? null : { ...emptyJob })}>{editing ? 'Close editor' : 'Post a role'}</button></div>{editing && <JobForm initial={editing.id ? editing : emptyJob} saving={state.saving} error={state.error} onCancel={() => setEditing(null)} onSaved={() => { setEditing(null); load(); }} />}{state.error && !editing && <p className="form-error">{state.error}</p>}{state.loading ? <div className="loading-state">Loading roles...</div> : <div className="job-list">{jobs.content?.map((job) => <article className="job-row" key={job.id}><div><span className="tag">{job.status}</span><h3>{job.title}</h3><p className="muted">{job.location} · {job.employmentType}</p></div><div className="row-actions"><Link className="text-link" to={`/recruiter/jobs/${job.id}/applications`}>Applicants</Link><button className="button button-ghost" onClick={() => setEditing({ ...job })}>Edit</button><button className="button button-danger" onClick={() => remove(job.id)}>Delete</button></div></article>)}</div>}</section>;
}

function JobForm({ initial, saving, error, onCancel, onSaved }) {
  const [form, setForm] = useState(initial); const [localError, setLocalError] = useState('');
  const update = (key, value) => setForm({ ...form, [key]: value });
  const submit = async (event) => { event.preventDefault(); setLocalError(''); try { const payload = { ...form, salaryMin: Number(form.salaryMin), salaryMax: form.salaryMax === '' ? null : Number(form.salaryMax) }; if (form.id) await jobsApi.update(form.id, payload); else await jobsApi.create(payload); onSaved(); } catch (e) { setLocalError(friendlyError(e)); } };
  return <form className="panel stack-form job-form" onSubmit={submit}><div className="form-grid"><Field label="Title" value={form.title} onChange={(v) => update('title', v)} required /><Field label="Location" value={form.location} onChange={(v) => update('location', v)} required /></div><div className="form-grid"><label className="field"><span>Employment type</span><select value={form.employmentType} onChange={(e) => update('employmentType', e.target.value)}>{['FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'FREELANCE'].map((type) => <option key={type}>{type}</option>)}</select></label><Field label="Experience level" value={form.experienceLevel} onChange={(v) => update('experienceLevel', v)} /></div><div className="form-grid"><Field label="Minimum salary" type="number" value={form.salaryMin} onChange={(v) => update('salaryMin', v)} required /><Field label="Maximum salary" type="number" value={form.salaryMax || ''} onChange={(v) => update('salaryMax', v)} /></div><Field label="Required skills" value={form.requiredSkills} onChange={(v) => update('requiredSkills', v)} required /><label className="field"><span>Description</span><textarea rows="7" value={form.description} onChange={(e) => update('description', e.target.value)} required /></label>{(localError || error) && <p className="form-error">{localError || error}</p>}<div className="row-actions"><button className="button button-primary" disabled={saving}>{saving ? 'Saving...' : form.id ? 'Save changes' : 'Publish role'}</button><button type="button" className="button button-ghost" onClick={onCancel}>Cancel</button></div></form>;
}

export function RecruiterApplicationsPage() {
  const { jobId } = useParams(); const [applications, setApplications] = useState([]); const [state, setState] = useState({ loading: true, error: '' });
  const load = () => recruiterApi.applications(jobId).then(setApplications).catch((error) => setState({ loading: false, error: friendlyError(error) })).finally(() => setState((s) => ({ ...s, loading: false })));
  useEffect(load, [jobId]);
  const updateStatus = async (id, status) => { try { await applicationsApi.updateStatus(id, status); load(); } catch (error) { setState((s) => ({ ...s, error: friendlyError(error) })); } };
  if (state.loading) return <div className="loading-state">Loading applicants...</div>;
  return <section className="narrow-page"><Link className="back-link" to="/recruiter/jobs">← My jobs</Link><div className="page-heading"><p className="eyebrow">Applications</p><h1>Review candidates</h1></div>{state.error && <p className="form-error">{state.error}</p>}<div className="application-list">{applications.map((application) => <article className="application-row" key={application.id}><div><h3>{application.jobSeekerProfileId ? `Candidate #${application.jobSeekerProfileId}` : 'Candidate'}</h3><p className="muted">Applied {new Date(application.appliedAt).toLocaleDateString()}</p></div><select className="status-select" value={application.status} onChange={(e) => updateStatus(application.id, e.target.value)}>{['APPLIED', 'SHORTLISTED', 'REJECTED', 'HIRED'].map((status) => <option key={status}>{status}</option>)}</select></article>)}</div></section>;
}

function Metric({ label, value }) { return <div className="metric"><span>{label}</span><strong>{value}</strong></div>; }
function Field({ label, value, onChange, ...props }) { return <label className="field"><span>{label}</span><input value={value || ''} onChange={(e) => onChange(e.target.value)} {...props} /></label>; }
