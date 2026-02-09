import { useEffect, useState } from "react"
import EmailItem from "../components/EmailItem";
import '../styles/layout.css';
import { fetchDrafts } from "../api/mailApi";
import { useNavigate } from "react-router-dom";

export default function Drafts(){

  

    const [emails,setEmails] = useState([]);
    const [selectedEmails,setSelectedEmails] = useState([]);
    const [filter,setFilter] = useState('');
    const token = localStorage.getItem("token");
    const navigate = useNavigate();

    const openDraft = (email) => {
        navigate("/compose",{ state: {draft: email}});
    }

    useEffect(() =>{
        loadEmails();
    },[]);

    const loadEmails = async() => {
        try{
            const res = await fetchDrafts(token);
            console.log(res.data);
            setEmails(res.data);
        }catch(error){
            console.log(error);
        }    
    };

    const handleSelect = (uid, checked) => {
   setSelectedEmails(prev => checked
                              ?[... new Set([... prev,uid])]
                              : prev.filter(e => e !== uid)
   );
  };

  const handleSelectAll = (e) => {
  if (e.target.checked) {
    setSelectedEmails(prev => [
      ...new Set([
        ...prev,
        ...filteredEmails.map(email => email.uid)
      ])
    ]);
  } else {
    setSelectedEmails(prev =>
      prev.filter(
        uid => !filteredEmails.some(email => email.uid === uid)
      )
    );
  }
};


    const handleDelete = async () => {
        await deleteEmails(selectedEmails,token);
        setSelectedEmails([]);
        loadEmails();
    }

    const filteredEmails = emails.filter(
        email => email.subject.toLowerCase().includes(filter.toLowerCase()) || 
                 email.to.toLowerCase().includes(filter.toLowerCase())
    );
    
    return(
        <div className="email-page">
            <div className="email-actions">
                <input type="checkbox" onChange={handleSelectAll} checked={
                    filteredEmails.length > 0 &&
                    filteredEmails.every(email =>
                    selectedEmails.includes(email.uid))}/> Select All
                <button onClick={handleDelete} disabled={selectedEmails.length == 0}>Delete</button>
                <input type="text" placeholder="Search Drafts..." 
                    value={filter} onChange={e => setFilter(e.target.value)}/>
            </div>
            <div className="email-list">
                {filteredEmails.map(email => (
                    <EmailItem key={email.uid} 
                    email={email} 
                    selected={selectedEmails.includes(email.uid)}
                    onSelect={handleSelect}
                    onOpen={() => openDraft(email)}/>
                ))}
            </div>
        </div>
    )
}