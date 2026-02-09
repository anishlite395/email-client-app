import { Link } from "react-router-dom";
import '../styles/PublicHome.css';

export default function PublicHome(){
    return(
        <div className="public-home">
            <nav className="public-nav">
                <h1 className="logo">MailBox</h1>
                <div className="nav-actions">
                    <Link to="/login" className="btn btn-outline">Login</Link>
                    <Link to="/register" className="btn btn-primary">Sign Up</Link>
                </div>
            </nav>

            <section className="hero">
                <h2>Simple. Secure. Fast Email</h2>
                <p>
                    Manage your inbox, send emails, and stay productive with MailBox -
                    a modern email client built for speed and security.
                </p>
                <div className="hero-actions">
                    <Link to='/register' className="btn btn-primary btn-large">
                        Get Started
                    </Link>
                    <Link to='/login' className="btn btn-secondary btn-large">
                        Login
                    </Link>
                </div>
            </section>

            <section className="features">
                <div className="feature-card">
                    <h3>📥 Smart Inbox</h3>
                    <p>Keep your emails organized and clutter free.</p>
                </div>
                <div className="feature-card">
                    <h3>✉️ Fast Compose</h3>
                    <p>Send Mails Quickly with a clean compose experience.</p>
                </div>
                <div className="feature-card">
                    <h3>🔒 Secure</h3>
                    <p>Your emails are protected with secure authentication.</p>
                </div>
            </section>

            <footer className="public-footer">
                <p>© {new Date().getFullYear()} MailBox. All rights reserved.</p>
            </footer>
        </div>
    )
}