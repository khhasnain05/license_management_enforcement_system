# License Management & Enforcement System (LMES)

A complete **License Management & Enforcement System (LMES)** developed as a **Fourth Semester University Project** for the **Database Systems** and **Software Design and Architecture** courses.

The system digitalizes the complete driving license process, allowing applicants to register, apply for learner permits and driving licenses, upload required documents, submit payments, take tests, receive notifications, and track application progress. Different officers are provided with dedicated dashboards to perform their responsibilities throughout the licensing workflow.

The project uses **Oracle Database** as the backend database and follows the **N-Layered Architecture** to keep the application modular, organized, and easy to maintain.

---

## Features

### Applicant
- Register and login securely
- Apply for a learner permit
- Upload required documents
- Submit learner permit payment
- Track application progress
- View learner permit details
- View scheduled driving tests
- Submit driving license fee
- View issued driving license
- Receive notifications
- View traffic violations and challans

### Licensing Officer
- Review learner permit payments
- Approve or reject learner permit payments
- Review driving license payments
- Approve or reject driving license payments
- Approve final driving licenses
- View payment audit history
- View assigned applications

### Testing Officer
- Review medical certificates
- Approve or reject medical certificates
- View scheduled driving tests
- Evaluate practical driving tests
- View practical test history

### Traffic Police Officer
- Verify licenses using License ID or CNIC
- Issue challans
- Record traffic violations
- Mark challans as paid
- View previously issued challans

### Administrator
- Create staff accounts
- Update staff information
- Suspend or activate staff accounts
- View officer audit logs
- Access analytics dashboards

---

## Technologies Used

- **Backend:** Java 17, Spring Boot, Spring Security (BCrypt), Spring Data JPA, Hibernate, Maven
- **Database:** Oracle Database, PL/SQL, Stored Procedures, Triggers, Views, Sequences
- **Frontend:** HTML, CSS, JavaScript, Thymeleaf
- **Design Patterns:** Observer Pattern, Factory Pattern, Strategy Pattern
- **Architecture:** N-Layered Architecture

---

## Project Structure

```text
src
├── controller
├── service
├── repository
├── model
├── dto
├── config
├── factory
├── strategy
├── observer
├── templates
├── static
└── resources
```

---

## Database

This project is designed specifically for Oracle Database. The complete database script containing all Tables, Constraints, Stored Procedures, Triggers, Views, and Sequences is available in the root directory:

```text
schema.sql
```

---

## Prerequisites

Before running the project, ensure you have the following installed:

- Java 17
- Maven
- Oracle Database (19c, 21c, or XE)
- Eclipse IDE, IntelliJ IDEA, or VS Code
- Git

---

## System Workflow

Before installing, it helps to understand how the data flows through the system:

1. **Registration:** An applicant creates an account and applies for a Learner Permit by uploading their CNIC and photo.
2. **Learner Phase:** The applicant submits the fee. The Licensing Officer verifies the payment and issues the 6-month Learner Permit.
3. **Permanent License Phase:** The applicant applies for a permanent license, uploads a medical certificate, and pays the final fee.
4. **Testing Phase:** The Testing Officer approves the medical certificate and evaluates the applicant's practical driving test.
5. **Issuance & Enforcement:** Once passed, the Licensing Officer issues the final Driving License. Traffic Police can then verify this license and issue challans against it if traffic rules are broken.

---

## Installation & Setup

Follow these detailed steps to run the project locally.

### 1. Clone the Repository

Open your terminal and clone the project:

```bash
git clone https://github.com/khhasnain05/license_management_enforcement_system.git
cd dlms
```

### 2. Initialize the Oracle Database

1. Open Oracle SQL Developer.
2. Connect to your database using a dedicated user.
3. Open and run the `schema.sql` file provided in the project root. This will create all the necessary empty tables, views, and procedures.

### 3. Configure Database Connection

Open the `src/main/resources/application.properties` file. You will see placeholder variables for the database connection:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

You have two options:

- **Option A:** Replace `${DB_URL}`, `${DB_USERNAME}`, and `${DB_PASSWORD}` directly with your Oracle credentials.
- **Option B:** Set these as Environment Variables in your operating system or IDE run configurations.

### 4. Create the Uploads Directory

The application requires a folder to save uploaded documents (CNIC, Medical files, etc.). Create a folder named `uploads` in the root of the project:

```bash
mkdir uploads
```

### 5. Build and Run the Project

Compile the project and start the Spring Boot server using Maven:

```bash
mvn clean install
mvn spring-boot:run
```

### 6. Initial Setup & Role Creation

Because the database is completely empty upon creation and uses BCrypt password hashing, the system relies on a `DataSeeder` file to insert the very first Admin record.

Follow these exact steps to populate the system:

1. **Start the Application:** Once the Spring Boot server is running, navigate to the first endpoint:
   👉 `http://localhost:8080/login`
2. **Log in as Admin:** The `DataSeeder` file automatically inserts the Admin record into the `dlms_user` table. Log in using the Admin ID and Password defined in your specific `DataSeeder` configuration.
3. **Create Staff Roles:** As the Admin, go to the Admin Dashboard. Use the staff management tab to create the accounts for your Licensing Officers, Testing Officers, and Traffic Police. The system will securely hash their passwords and save them to the database.
4. **Applicant Registration:** Applicants do not need to be created by the Admin. Any citizen can visit `http://localhost:8080/register` to create their own applicant account and begin the license workflow.

---

## License

This project is developed for educational and learning purposes.