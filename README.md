# Beeja

**Beeja**

Welcome to **Beeja**, your open-source solution for **end-to-end organizational operations**! 🎉

Beeja is a highly interactive, self-customizable platform designed to simplify and streamline organizational processes. Whether you're managing employees, finances, clients, contracts, or expenses, Beeja provides a one-stop solution for small-scale businesses and startups worldwide.

---

## **Table of Contents**
- [License](#license)
- [Website](#website)
- [Code of Conduct](#code-of-conduct)
- [Contributing](#contributing)
- [Features Overview](#features-overview)
- [Installation and Local Setup](#installation-and-local-setup)
  - [Backend Setup](#beeja-backend---local-setup)
  - [Frontend Setup](#beeja-ui---local-setup)
- [API Documentation](#api-documentation)
- [Code Quality](#code-quality)
- [Modules and Features](#modules-and-features)
- [Community and Support](#community-and-support)

---


## **License**
Beeja is licensed under the [Apache 2.0 License](./LICENSE).

---

## **Website**
Discover more about Beeja and its ecosystem on our [official website](https://www.beeja.io).

---

## **Code of Conduct**
We value collaboration and respect in our community. Please read our [Code of Conduct](./.github/CODE_OF_CONDUCT.md) to ensure a welcoming environment for everyone.

---

## **Contributing**
Beeja is an open-source project, and we welcome contributions! Check out our [Contributing Guidelines](./.github/CONTRIBUTING.md) to get started.

---

## **Features Overview**

Here’s a quick look at what Beeja offers:
- **Employee Records Management** – Manage employee data, roles, and organizational structures.
- **Employee Document Management** – Manage employee documents.
- **Inventory Management** – Monitor and manage organizational assets.
- **Finance Module** – Handle invoicing, expenses features.

---

## **Installation and Local Setup**

### **Prerequisites**
Before starting, ensure you have the following installed:
- **Java 17**
- **Docker**
- **Node.js (v16 or later)**
- **npm** (comes with Node.js)
- A suitable location on your drive to store files.

---

##  Beeja Backend - Run Options (Docker & Manual)

You can run the Beeja backend services in two different ways:

---

###  Option 1: Run with Docker 


####  Steps:

1. **Clone the Repository**
   ```bash
   git clone https://github.com/beeja-io/beeja.git
   ```

2. **Build and Run the Services**
   ```bash
   docker compose up --build
   ```

3. **Verify Running Services**
   -  Eureka Service Registry: [http://localhost:8761]
   -  Swagger API Documentation for each service:
     ```
     http://localhost:<microservice-port>/swagger-ui
     ```

 Tip: Make sure Docker is installed and running on your system before executing these commands.

---

###  Option 2: Run Manually (Without Docker)

Use this method if you prefer running services directly on your machine.

#### Prerequisites:
- Java 17  
- Gradle (or use the included Gradle wrapper)
- Set any required environment variables or configuration files as needed

####  Steps:

1. **Clone the Repository**
   ```bash
   git clone https://github.com/beeja-io/beeja.git
   ```

2. **Build All Backend Services**
   ```bash
   ./gradlew build
   ```

3. **Configure Environment Files**
   - Before running the services, ensure all necessary environment variables are properly set.
   - You can configure them in `.env` files or directly in the `application.yml`of each microservice as   required.

4. **Run Microservices Individually**  
   Navigate into each microservice folder:
   ```bash
   ./gradlew bootRun
   ```

5. **Verify Services**
   - Access each service on its configured port (check `application.yml` or `.env` files).
   - Swagger UI will be available at:
     ```
     http://localhost:<microservice-port>/swagger-ui
     ```



### **Beeja Web - Local Setup**

#### **Clone the Repository**
```bash
git clone https://github.com/beeja-io/beeja.git
```

#### **Install Dependencies**
```bash
npm install
```
#### **Configure API Endpoint**
Create a .env file in the web directory and add:
```text
VITE_API_BASE_URL=http://localhost:8000
```

#### **Run the Frontend Application**
```bash
npm run dev
```
Visit http://localhost:3000 to access the Beeja UI. Use the default credentials from the init script (email: beeja.admin@domain.com and password: password).

## **API Documentation**
Beeja includes a built-in OpenAPI documentation interface.  
Access it at: `http://localhost:8080/swagger-ui`.

## **Code Quality**

### **Formatting Code**
Before committing changes, format your code using:
```bash
npm run format
```

## **Modules and Features**

### **Roles**
- **Super Admin**: Manages accounts, creates employees, tracks inventory, and oversees expenses.

### **Modules**

#### **Account Management**
- Manages organizational data and user roles.
- Comes pre-configured with one organization and a Super Admin user.

#### **Employee Management**
- Stores job details, contact information, KYC, and bank account data.
- Sensitive information (e.g., KYC, bank accounts) is encrypted in the database.

#### **Finance**
- Tracks clients, payroll, invoices, contracts, and inventory.
- Includes country-specific settings for localized operations.

---

## **Community and Support**

Join the Beeja community to connect with other contributors and get support:
- **GitHub Discussions**: [Community Forum](https://github.com/beeja-io/beeja/discussions)
- **Slack**: [Join our workspace](https://join.slack.com/t/beeja-io/shared_invite/zt-2wh0tptfq-UoFoRvSvIyH2OOplV~6Azw)


---

Thank you for choosing Beeja! We can’t wait to see how you use and contribute to the platform. Let’s build the future of organizational operations—together! 🚀
