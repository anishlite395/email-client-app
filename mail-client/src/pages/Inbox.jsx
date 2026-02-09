import { useEffect, useState } from "react";
import EmailItem from "../components/EmailItem";
import '../styles/layout.css';
import { deleteEmails, fetchInbox } from "../api/mailApi";
import { useNavigate } from "react-router-dom";

export default function Inbox({email,selected,onSelect}){

  const [emails,setEmails] = useState([]);
  const [selectedEmails,setSelectedEmails] = useState([]);
  const [filter,setFilter] = useState('');
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  useEffect(() => {
    loadEmails();
  }, []);

  const loadEmails = async () => {
    try {
      const res = await fetchInbox(token);
      setEmails(res.data);
    } catch (err) {
      console.error(err);
    }
  };

   const handleSelect = (uid, checked) => {
   setSelectedEmails(prev => checked
                              ?[... new Set([... prev,uid])]
                              : prev.filter(e => e !== uid)
   );
  };

  const handleSelectAll = (e) => {
    if (e.target.checked) 
      setSelectedEmails(emails.map(email => email.uid));
    else 
      setSelectedEmails([]);
  };

  const handleDelete = async () => {
      try{
        await deleteEmails(selectedEmails);
        setSelectedEmails([]);
        loadEmails();
      }catch(err){
        console.error(err);
      }
    };

    const filteredEmails = emails.filter(
    email => email.subject.toLowerCase().includes(filter.toLowerCase()) ||
             email.to.toLowerCase().includes(filter.toLowerCase())
  );
 
    return(
      <div className="email-page">
        <div className="email-actions">
          <input type="checkbox" onChange={handleSelectAll} />
          <button onClick={handleDelete} disabled={selectedEmails.length === 0}>Delete</button>
          <input type="text" placeholder="Search Emails" value={filter} 
              onChange={e => setFilter(e.target.value)} />
        </div>
        <div className="email-list">
          {filteredEmails.map(email => (
            <EmailItem
                    key={email.uid}
                    email={email}
                    selected={selectedEmails.includes(email.uid)}
                    onSelect={handleSelect}
                    onOpen = {() => navigate(`/inbox/${email.uid}`)} />
          ))}
        </div>
      </div>
    );
}