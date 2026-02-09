export default function Topbar(){

    const handleLogout = () => {
        localStorage.removeItem("token");
        window.location.href='/login';
    };

    return(
        <div className="topbar">
            <div className="topbar-left">
                <h2>Email Client</h2>
            </div>
            <div className="topbar-right">
                <button className="logout-btn" onClick={handleLogout}>Logout</button>
            </div>
        </div>
    )
}