import React from 'react';
import { Link } from 'react-router-dom';

export function ForbiddenPage() { return <div className="empty-state centered"><p className="eyebrow">403 / Not your lane</p><h1>Access restricted</h1><p>Your account does not have access to this workspace.</p><Link className="button button-primary" to="/">Return to jobs</Link></div>; }
export function NotFoundPage() { return <div className="empty-state centered"><p className="eyebrow">404 / Missing page</p><h1>That page moved on.</h1><Link className="button button-primary" to="/">Return to jobs</Link></div>; }
