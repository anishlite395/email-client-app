import { NavLink } from "react-router-dom";

export default function Sidebar(){
    return(
        <div className="sidebar">
            <h3>MailBoxes</h3>
            <nav>
                <NavLink to='/inbox' className='sidebar-link'>
                    Inbox
                </NavLink>
                <NavLink to='/compose' className='sidebar-link'>
                    Compose
                </NavLink>
                <NavLink to='/sent' className='sidebar-link'>
                    Sent
                </NavLink>
                <NavLink to='/drafts' className='sidebar-link'>
                    Drafts
                </NavLink>
            </nav>
        </div>
    )
}