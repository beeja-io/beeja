# Welcome to the Beeja Contributing Guide! <!-- omit in toc -->

🌟 Thank you for your interest in contributing to Beeja! Your time and effort mean a lot to us. Together, we can build an incredible open-source solution for organizational operations.  

This guide will walk you through the contribution process, from reporting issues to submitting your first pull request (PR). Let’s get started!

---

## **Table of Contents**
- [Code of Conduct](#code-of-conduct)
- [New Contributor Guide](#new-contributor-guide)
- [Getting Started](#getting-started)
  - [Navigate the Codebase](#navigate-the-codebase)
  - [Issues](#issues)
    - [Creating a New Issue](#creating-a-new-issue)
    - [Solving an Issue](#solving-an-issue)
  - [Making Changes](#making-changes)
  - [Commit Guidelines](#commit-guidelines)
  - [Submitting a Pull Request (PR)](#submitting-a-pull-request-pr)
- [Congratulations, Your PR is Merged! 🎉](#congratulations-your-pr-is-merged-)

---

## **Code of Conduct**
Please take a moment to review our [Code of Conduct](./CODE_OF_CONDUCT.md). We are committed to fostering a welcoming and inclusive community for everyone.  

---

## **New Contributor Guide**

If this is your first time contributing to Beeja, we recommend you:
- Read the [README](../README.md) for an overview of the project.
- Familiarize yourself with the purpose and structure of the codebase.
- Explore the **issues** section to find tasks you’d like to help with.

---

## **Getting Started**

### **Navigate the Codebase**
Our repository is structured with:
- **`/services`**: Contains backend microservices built with Java and Spring Boot.
- **`/web`**: Contains the frontend application built with React and TypeScript.

---

### **Issues**

#### **Creating a New Issue**
If you’ve found a bug, typo, or improvement opportunity:
1. **Search Existing Issues**: Check if someone else has already reported it.  
   - You can search the [Issues tab](https://github.com/beeja-io/issues) for similar issues.
2. **Open a New Issue**: If it’s a new problem, submit an issue using our [issue templates](https://github.com/beeja-io/issues/new/choose).  
   - Provide clear details, screenshots (if applicable), and steps to reproduce the problem.

#### **Solving an Issue**
Looking to contribute?  
1. Browse our [open issues](https://github.com/beeja-io/issues).  
2. Pick an issue that interests you—no need to ask for permission! Just leave a comment so others know you’re working on it.
3. Fork the repository and start solving the issue (details below).  

---

### **Making Changes**

Follow these steps to contribute effectively:  

1. **Fork the Repository**: Click the "Fork" button at the top of this repository to create your copy.
2. **Clone Your Fork**:  
   ```bash
   git clone https://github.com/beeja-io/beeja.git
   cd beeja
   ```
3. **Create a Branch**:  
   - Always create a branch off the `master` branch. Use meaningful branch names, like `fix-bug-123` or `add-new-feature`.  
   ```bash
   git checkout -b your-branch-name
   ```
4. **Write Tests**:  
   - If you’re adding new code, include relevant tests to verify your implementation.
   - For API changes, ensure the OpenAPI spec is updated in the corresponding module to reflect your changes.

5. **Ensure All Tests Pass**:  
   - Run the existing tests to verify that your changes don’t introduce any regressions.
   - Use the following command (adjust based on your service or framework):
   ```bash
   ./gradlew test
   ```
   For frontend changes, you can run:
   ```bash
   npm run test
   ```

### **Commit Guidelines**

When committing your changes:

1. **Follow Proper Linting**:  
   - Use the provided linting tools to ensure clean, consistent code.
   - For backend code, check for adherence to the project’s coding standards.
   - For frontend code, run the linter using:
     ```bash
     npm run lint
     ```

2. **Run Checkstyle**:  
   - Ensure that all backend code passes Checkstyle validation before committing.

3. **Write Clear Commit Messages**:  
   - Use a meaningful and concise commit message to describe your changes.  
     **Examples**:
     - `fix: resolve null pointer exception in EmployeeService`
     - `feature: add employee onboarding workflow support`
     - `docs: update README with API documentation link`

4. **Commit Your Changes**:  
   - Stage your changes:
     ```bash
     git add .
     ```
   - Commit with a meaningful message:
     ```bash
     git commit -m "Your commit message here"
     ```

---

### **Submitting a Pull Request (PR)**

Once your changes are ready to be reviewed:

1. **Push Your Branch**:
   ```bash
   git push origin your-branch-name
   ```
2. **Open a Pull Request (PR)**:
   - Navigate to your forked repository on GitHub.
   - Click the "Compare & Pull Request" button.
   - Fill out the PR template with the following details:
     - **Title**: Summarize your changes concisely (e.g., `feature: add employee onboarding`).
     - **Description**: Clearly explain what the PR does, including:
       - The problem it solves or the feature it implements.
       - Any relevant links to related issues or discussions.
       - Any limitations or areas for future work.
     - **Checklist**: Ensure your PR adheres to the guidelines:
       - Code is linted and formatted.
       - Tests are added or updated if necessary.
       - OpenAPI spec is updated for API changes.

3. **Automated Checks**:
   - Your PR will automatically trigger the following checks:
     - Linting and formatting.
     - Unit and integration tests.
   - Ensure all checks pass before requesting a review.

4. **Respond to Feedback**:
   - Reviewers may leave comments or suggest changes to improve the code.
   - Address their feedback and push your updates to the same branch:
     ```bash
     git push origin your-branch-name
     ```

5. **Squash Commits**:
   - If your PR contains multiple commits, squash them into a single commit to keep the history clean:
    ```bash
     git rebase -i master
    ```
   - Use a meaningful commit message when squashing.

---

### **Congratulations, Your PR is Merged! 🎉**

Thank you for contributing to Beeja! Your efforts are now part of an open-source platform helping organizations worldwide. 🌟  

#### What’s Next?
Looking for more ways to contribute?  
- Explore the [issues tab](https://github.com/beeja-io/issues) for more tasks.  
- Join our [community discussions](https://github.com/beeja-io/discussions) to share ideas and get updates.  

Together, let’s shape the future of organizational operations with Beeja. 🚀
