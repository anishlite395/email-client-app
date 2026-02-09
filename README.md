# Email Client Application

Email Client Application built using Java, Spring Boot, and React.js.  
This application allows users to securely send, receive, and manage emails using SMTP and IMAP with JWT-based authentication.

---

## Technologies Used

Backend:
- Java 8 / 11
- Spring Boot
- Spring Security
- Spring Data JPA (Hibernate)
- JavaMail (SMTP & IMAP)
- MySQL

Frontend:
- React.js
- HTML, CSS, Bootstrap
- JavaScript

Tools:
- Git, GitHub
- Maven
- Node.js, npm
- Postman

---

## Project Structure

email-client-app  
├── backend  
├── frontend  
└── README.md  

---

## Prerequisites

Make sure the following are installed:

```bash
java -version
mvn -version
node -v
npm -v
mysql --version
git --version
```
## How to Run the Application

### Backend (Spring Boot)

1. **Clone the repository and navigate to backend**

```bash
git clone https://github.com/anishlite395/email-client-app.git
cd email-client-app/backend
```
2. **Configure the Database**
   Create a MySQL database:
   ```
   CREATE DATABASE email_client;
   ```
   Update application.properties with your database credentials:
   ```
      spring.datasource.url=jdbc:mysql://localhost:3306/email_client
      spring.datasource.username=root
      spring.datasource.password=your_password
      spring.jpa.hibernate.ddl-auto=update
   ```
3.**Configure hMailServer**
  - Install and start hMailServer
  - Create a domain (e.g., example.com)
  - Add email accounts under the domain
  - Update application.properties with admin credentials:
    ```
       hmail.admin.username=Administrator
       hmail.admin.password=your_admin_password
    ```
4.**Run Spring Boot Application**
   ```
      mvn clean install
      mvn spring-boot:run
   ```
