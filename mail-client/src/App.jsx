import { BrowserRouter as Router,Routes,Route,Navigate} from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Inbox from "./pages/Inbox";

import { Children } from "react";
import Sent from "./pages/Sent";
import Drafts from "./pages/Drafts";
import PublicHome from "./pages/PublicHome";
import Sidebar from "./components/Sidebar";
import Topbar from "./components/Topbar";
import ComposeModal from "./components/ComposeModal";
import EmailDetails from "./components/EmailDetails";

function App() {

  /* ---------------------------------
  -------Auth Guard-------------------*/
  const PrivateRoute = ({children}) => {
    const token = localStorage.getItem("token");
    return token ? children: <Navigate to='/login' replace />
  };
  
  const MailLayout = ({ children }) => {
    return(
      <div className="app"> 
        <Sidebar />
        <div className="content">
          <Topbar />
          <div className="page-content">
            {children}
          </div>
        </div>
      </div>
    )
  }

  const PublicOnlyRoute = ({children}) => {
      const token = localStorage.getItem("token");
      return token ? <Navigate to='/inbox' replace /> : children;
  }

  return (
    <Router>
      <Routes>
        {/* Public Routes*/}
        <Route path="/login" element={<Login/>} />
        <Route path="/register" element={<Register />} />
        <Route path="/" 
               element={
                <PublicOnlyRoute>
                  <PublicHome />
                </PublicOnlyRoute>
        } />

        {/* Private Routes*/}
        <Route path="/inbox" element={
          <PrivateRoute>
            <MailLayout>
              <Inbox />
            </MailLayout>
          </PrivateRoute>
        }
        /> 
        <Route path="/inbox/:id" element={
              <PrivateRoute>
                <MailLayout>
                  <EmailDetails folder="inbox"/>
                </MailLayout>
              </PrivateRoute>
        }/>
        <Route path="/sent" element={
          <PrivateRoute>
            <MailLayout>
              <Sent />
            </MailLayout>
          </PrivateRoute>
        } />
        <Route path="/sent/:id" element={
          <PrivateRoute>
            <MailLayout>
              <EmailDetails folder="sent" />
            </MailLayout>
          </PrivateRoute>
        } />



        <Route path="/drafts" element={
          <PrivateRoute>
            <MailLayout>
              <Drafts />
            </MailLayout>
          </PrivateRoute>
        } />

        <Route path="/compose" element={
          <PrivateRoute>
            <MailLayout>
              <ComposeModal />
            </MailLayout>
          </PrivateRoute>
        } />
        {/* Default Redirect */}
        
      </Routes>
    </Router>
  );
}

export default App;
