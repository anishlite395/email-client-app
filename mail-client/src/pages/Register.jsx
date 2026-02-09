import { useState } from "react";
import {registerUser} from  '../api/authApi';
import axios from "axios";

export default function Register(){

    const [username,setUsername] = useState("");
    const [location,setLocation] = useState("");
    const [email,setEmail] = useState("");
    const [password,setPassword] = useState("");

    const register = async () => {
        const res = await registerUser({username,location,email,password});
        window.location.href='/login';
    };

    return(
        <div className="auth">
            <div className="auth-card">
                <h2>Register</h2>
                <input placeholder="Username" onChange={e => setUsername(e.target.value)} />
                <input placeholder="Location" onChange={e => setLocation(e.target.value)} />
                <input placeholder="Email" onChange={e => setEmail(e.target.value)} />
                <input placeholder="Password" type="password" onChange={e => setPassword(e.target.value)} />
                <button onClick={register}>Login</button>
                <p onClick={() => window.location.href='/login'}>Already Registered?Please Login Here</p>
            </div>
            
        </div>
    )
}