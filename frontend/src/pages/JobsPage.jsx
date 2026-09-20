import React, { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { jobsApi } from '../api/jobsApi';
import { friendlyError } from '../utils/errors';

const employmentTypes = ['', 'FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'FREELANCE'];

const formatSalary = (job) => job.salaryMax
  ? `$${Math.round(job.salaryMin / 1000)}k - $${Math.round(job.salaryMax / 1000)}k`
  : job.salaryMin ? `From $${Math.round(job.salaryMin / 1000)}k` : 'Salary not listed';

const formatPostedDate = (createdAt) => createdAt
  ? `Posted ${new Date(createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}`
  : 'Recently posted';

export default function JobsPage() {
  const [filters, setFilters] = useState({ keyword: '', location: '', employmentType: '' });
  const [page, setPage] = useState(0);
  const [result, setResult] = useState({ content: [], totalPages: 0, totalElements: 0 });
  const [state, setState] = useState({ loading: true, error: '' });
  const requestId = useRef(0);

  const load = async (nextPage = page, nextFilters = filters) => {
    const currentRequestId = ++requestId.current;
    const queryFilters = {
      ...nextFilters,
      keyword: nextFilters.keyword.trim(),
      location: nextFilters.location.trim(),
    };
    setState({ loading: true, error: '' });
    try {
      const nextResult = await jobsApi.search({ ...queryFilters, page: nextPage, size: 9, sort: 'createdAt,desc' });
      if (currentRequestId !== requestId.current) return;
      setResult(nextResult);
      setPage(nextPage);
    } catch (error) {
      if (currentRequestId !== requestId.current) return;
      setState({ loading: false, error: friendlyError(error) });
      return;
    }
    setState({ loading: false, error: '' });
  };
  useEffect(() => {
    const timer = setTimeout(() => load(0, filters), 300);
    return () => clearTimeout(timer);
  }, [filters.keyword, filters.location, filters.employmentType]);

  const submit = (event) => { event.preventDefault(); load(0, filters); };
  return <section>
    <div className="hero"><div><p className="eyebrow">Find work with momentum</p><h1>Good work starts with a better search.</h1><p className="hero-copy">Browse roles from teams building what comes next.</p></div><div className="hero-index">01<span>/</span>JOBS</div></div>
    <form className="search-panel" onSubmit={submit}><input aria-label="Search jobs" placeholder="Role, skill, or keyword" value={filters.keyword} onChange={(e) => setFilters({ ...filters, keyword: e.target.value })} /><input aria-label="Location" placeholder="Location" value={filters.location} onChange={(e) => setFilters({ ...filters, location: e.target.value })} /><select aria-label="Employment type" value={filters.employmentType} onChange={(e) => setFilters({ ...filters, employmentType: e.target.value })}>{employmentTypes.map((type) => <option key={type} value={type}>{type ? type.replace('_', ' ') : 'All employment types'}</option>)}</select><button className="button button-primary">Search roles</button></form>
    <div className="section-heading"><div><p className="eyebrow">Open positions</p><h2>{result.totalElements || 0} roles worth a look</h2></div>{state.loading && <span className="muted">Updating results...</span>}</div>
    {state.error && <p className="form-error">{state.error}</p>}
    {!state.loading && !result.content?.length && <div className="empty-state">No roles match those filters yet.</div>}
    <div className="job-grid">{result.content?.map((job) => <article className="job-card" key={job.id}><div className="job-card-top"><span className="tag">{job.employmentType?.replace('_', ' ')}</span><span className="muted">{job.location}</span></div><h3><Link to={`/jobs/${job.id}`}>{job.title}</Link></h3><p className="company">{job.companyName}</p><p className="clamp">{job.description}</p><div className="job-meta"><span>{formatSalary(job)}</span><span>{job.experienceLevel || 'All experience levels'}</span></div><div className="skill-list">{job.requiredSkills?.split(',').slice(0, 3).map((skill) => <span className="skill" key={skill}>{skill.trim()}</span>)}</div><div className="job-card-bottom"><span>{formatPostedDate(job.createdAt)}</span><Link className="text-link" to={`/jobs/${job.id}`}>View role →</Link></div></article>)}</div>
    {result.totalPages > 1 && <div className="pagination"><button className="button button-ghost" disabled={page === 0} onClick={() => load(page - 1, filters)}>Previous</button><span>Page {page + 1} of {result.totalPages}</span><button className="button button-ghost" disabled={page + 1 >= result.totalPages} onClick={() => load(page + 1, filters)}>Next</button></div>}
  </section>;
}
