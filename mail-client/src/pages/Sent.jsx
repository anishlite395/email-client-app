import { useEffect, useState } from "react";
import EmailItem from "../components/EmailItem";
import { deleteSentEmails, fetchSent } from "../api/mailApi";
import { useNavigate } from "react-router-dom";

export default function Sent(){


  const [emails, setEmails] = useState([]);
  const [selectedEmails, setSelectedEmails] = useState([]);
  const [filter, setFilter] = useState('');
  const token = localStorage.getItem('token');
  const navigate = useNavigate();

  useEffect(() => {
    loadEmails();
  }, []);

  const loadEmails = async () => {
    try {
      const res = await fetchSent(token);
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
    await deleteSentEmails(selectedEmails);
    setSelectedEmails([]);
    loadEmails();
  };

  const filteredEmails = emails.filter(
    email => email.subject.toLowerCase().includes(filter.toLowerCase()) ||
             email.to.toLowerCase().includes(filter.toLowerCase())
  );

  return (
    <div className="email-page">
      <div className="email-actions">
        <input type="checkbox" onChange={handleSelectAll} checked={
             emails.length > 0 &&
             selectedEmails.length === emails.length
  } /> Select All
        <button onClick={handleDelete} disabled={selectedEmails.length === 0}>Delete</button>
        <input
          type="text"
          placeholder="Search emails..."
          value={filter}
          onChange={e => setFilter(e.target.value)}
        />
      </div>
      <div className="email-list">
        {filteredEmails.map(email => (
          <EmailItem
            key={email.uid}
            email={email}
            selected={selectedEmails.includes(email.uid)}
            onSelect={handleSelect}
            onOpen={() => navigate(`/sent/${email.uid}`)}
          />
        ))}
      </div>
    </div>
  );
};
