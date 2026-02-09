import axios from "axios";
import { data } from "react-router-dom";
import api from "./axios";


export const sendingMailMultipart = (data,token) => api.post(`/email/send/multipart`,data,{
    headers: {Authorization: `Bearer ${token}`}
});

export const saveDrafts = (data,token) => api.post(`/email/saveDrafts`,data,{
    headers: {Authorization: `Bearer ${token}`}
});

export const fetchDrafts = (token) => api.get(`/email/drafts`,{
    headers: {Authorization: `Bearer ${token}`}
});

export const fetchInbox = (token) => api.get(`/email/inbox`,{
    headers: {Authorization: `Bearer ${token}`}
});

export const fetchDraftsMultipart = (token) => api.get(`/email/drafts/multipart`,{
    headers: {Authorization: `Bearer ${token}`}
});

export const sendingMail = (data,token) => api.post(`/email/send`,data,{
    headers: {Authorization: `Bearer ${token}`}
});

export const fetchEmailById = (id,folder,token) => api.get(`/email/${folder}/${id}`,{
    headers: {Authorization: `Bearer ${token}`}
});

export const deleteEmails = (uids) => api.delete(`/email/inbox/delete`,{
    data: uids
});

export const fetchSent = () => api.get(`/email/sent`);

export const deleteSentEmails = (uids) => api.delete(`/email/sent/delete`,{
    data: uids
});
