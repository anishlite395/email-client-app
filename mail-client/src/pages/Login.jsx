import axios from "axios";
import { useState } from "react";
import  '../styles/auth.css';
import {loginUser} from '../api/authApi';

export default function Login(){
    const [email,setEmail] = useState("");
    const [password,setPassword] = useState("");

    const login = async () => {
        try{
            const res = await loginUser({email,password});
            localStorage.setItem("token",res.data.token);
            window.location.href='/inbox';
        }catch(error){
            alert("Unable to Login. Server not reachable.");
            console.error("Login Failed:",error);
        }
    };

    return(
        <div className="auth">
            <div className="auth-card">
                <h2>Login</h2>
                <input placeholder="Email" onChange={e => setEmail(e.target.value)} />
                <input placeholder="Password" type="password" onChange={e => setPassword(e.target.value)} />
                <button onClick={login}>Login</button>
                <p onClick={() => window.location.href='/register'}>Not Registred?Please Register Here</p>
            </div>
        </div>
    )
}