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

[![Watch the video](https://img.youtube.com/vi/y9GFpM_7tjM/maxresdefault.jpg)](https://youtu.be/y9GFpM_7tjM)

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
- **Payroll & Benefits** – Customize payroll, reimbursements, and deductions.
- **Inventory Management** – Monitor and manage organizational assets.
- **Finance Module** – Handle invoicing, expenses features.

Watch Beeja in action!  
[Watch Beeja Demo]

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

### **Beeja Backend - Local Setup**

#### **Clone the Repository**
```bash
git clone https://github.com/beeja-io/beeja.git
cd beeja/services
```

#### **Build the Backend Services**
```bash
./gradlew build
```

#### **Run Beeja Services**
```bash
docker compose up --build
```

#### **Verify Services**
Once the services are running, verify:
- **Service Registry (Eureka)**: `http://localhost:8761` – View all registered microservices.fs
- **Open API Documentation**: `http://localhost:<ms-specific-port>/swagger-ui` – Explore Beeja's API endpoints.


### **Beeja Web - Local Setup**

#### **Clone the Repository**
```bash
git clone https://github.com/beeja-io/beeja.git
cd beeja/web
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

 





          # #!/usr/bin/env bash
          # set -xe
          # cp /root/copy.sh /source/copy.sh
          # chmod +x /source/copy.sh
          # cd /source/{{workflow.parameters.build-context}}