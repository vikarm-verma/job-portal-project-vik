import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { applicationsApi } from '../api/applicationsApi';
import { friendlyError } from '../utils/errors';

export default function ApplicationsPage() {
  const [applications, setApplications] = useState([]);
  const [state, setState] = useState({ loading: true, error: '' });
  useEffect(() => { applicationsApi.mine().then(setApplications).catch((error) => setState({ loading: false, error: friendlyError(error) })).finally(() => setState((s) => ({ ...s, loading: false }))); }, []);
  if (state.loading) return <div className="loading-state">Loading applications...</div>;
  return <section className="narrow-page"><div className="page-heading"><p className="eyebrow">Your applications</p><h1>Keep an eye on what’s moving.</h1></div>{state.error && <p className="form-error">{state.error}</p>}{!applications.length && !state.error && <div className="empty-state">No applications yet. <Link className="text-link" to="/">Browse open roles →</Link></div>}<div className="application-list">{applications.map((application) => <article className="application-row" key={application.id}><div><span className="eyebrow">{application.status}</span><h3><Link to={`/jobs/${application.jobId}`}>{application.jobTitle}</Link></h3><p className="muted">Applied {new Date(application.appliedAt).toLocaleDateString()}</p></div><span className={`status status-${application.status.toLowerCase()}`}>{application.status}</span></article>)}</div></section>;
}
