import { useEffect, useState } from "react";
import '../styles/compose.css';
import { saveDrafts,sendingMail,sendingMailMultipart } from "../api/mailApi";
import { useLocation } from "react-router-dom";

export default function ComposeModal(){
    const [form,setForm] = useState({to:'',subject:'',body:''});
    const [file,setFile] = useState(null);
    const [schedule,setSchedule] = useState("");
    const location = useLocation();
    const draft = location.state?.draft;

    useEffect(() => {
        if(draft) {
            console.log(draft);
            setForm({
                to: draft.recipient || '',
                subject: draft.subject || '',
                body: draft.body || ''
            });
        }
    },[draft]);

    const sendMail = async (isDraft=false) => {
        const token = localStorage.getItem("token");
        if(!token){
            alert("You are not logged in!")
            return;
        }
        
        try{
            if(file){
                const data = new FormData();
                Object.keys(form).forEach(key => data.append(key,form[key]));
                data.append("attachment",file);
                if(schedule) data.append("scheduleAt",schedule);
                if(isDraft){
                    await saveDraftsMultipart(data,token);
                    console.log("Saved as Drafts")
                }else{
                    await sendingMailMultipart(data,token);
                    console.log("Sent the Mail");
                }           
            }else{
                const data = {... form};
                if(schedule) data.scheduleAt = schedule;

                if(isDraft){
                    console.log(data);
                    await saveDrafts(data,token);
                    console.log("Saved as Drafts")
                }else{
                    await sendingMail(data,token);
                    console.log("Sent the Mail");
                }
            }

            //Resetting the form
            setForm({to:'',subject:'',body:''});
            setFile(null);
            setSchedule("");
        }catch(error){
            console.error(error);
            alert("Error Sending mail/drafts");
        }
    };

    return(
        <div className="compose">
            <input type="text" placeholder="To" onChange={e => setForm({... form,to:e.target.value})} value={form.to} />
            <input type="text" placeholder="Subject" onChange={e => setForm({... form,subject:e.target.value})} value={form.subject}/>
            <textarea type="text" placeholder="Body" onChange={e => setForm({... form,body:e.target.value})}  value={form.body}/>
            <input type="file" onChange={e => setFile(e.target.files[0])} />
            <input type="datetime-local" onChange={e => setSchedule(e.target.value)} />
            <button onClick={() => sendMail(false)}>Send</button>
            <button onClick={() => sendMail(true)}>Save Draft</button>
        </div>
    );
}