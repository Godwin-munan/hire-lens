# 🕶️ HireLens

HireLens is a 🔥 cutting-edge CV parser app built with **☕ Spring Boot** & **🌐 Angular**. It uses 🤖 AI & modern 🌍 tech to process 📄 CVs, extract 🎯 insights, & streamline 🚀 recruitment.

---

## ✨ Features

### 🛠️ Backend:

- **📄 CV Parsing:** Extracts 🔑 details like name, 📞 contact info, 🛠️ skills, 🎓 education, 🏢 experience.
- **⏱️ Real-Time Notifications:** Uses **📡 SSE** to notify users when parsing is ✅.
- **🔗 RESTful API:** Upload & retrieve parsed 📊 data.
- **🔒 Authentication:** Secured with **🔑 JWT**, though it may be 💤 temporarily disabled.
- **💾 Database Integration:** Supports **🐬 MySQL** & **🐘 PostgreSQL** for scalable 🗄️ storage.
- **⚡ Caching:** Uses **Spring Cache** for fast performance 🚀.
- **📊 Actuator Monitoring:** Exposes 🩺 health & 📈 metrics.

### 🎨 Frontend:

- **🤝 Interactive UI:** Built with **🎨 Angular** & styled with **Darkly Bootswatch 🖤**.
- **⏱️ Real-Time Parsing Feedback:** Progress updates 🛠️.
- **📤 Upload & Manage CVs:** Drag-&-drop 📂 interface.
- **📊 Insights Dashboard:** Visualizes 🎯 extracted data.

### 🌟 Additional Features:

- **🌍 i18n:** Multi-language 🌐 support.
- **✅ Testing:** Unit tests with **JUnit** & **Cypress 🕵️**.

---

## 🛠️ Technologies Used

### 🚀 Backend:

- **☕ Java 17**
- **🌱 Spring Boot 3**
- **🛠️ JHipster** for scaffolding 🏗️
- **🤖 OpenAI GPT** for AI-powered parsing 📄
- **🐬 MySQL / 🐘 PostgreSQL** for 🗄️ storage
- **🔄 Liquibase** for 📦 migrations
- **🔒 Spring Security + JWT** for 🔐 auth

### 🎨 Frontend:

- **🌐 Angular 16**
- **⚙️ TypeScript**
- **🎨 Tailwind CSS** for custom 🖌️ styles
- **Darkly 🖤 Bootswatch** for themes

---

## 📖 Installation Guide

### 🧰 Prerequisites:

- **☕ Java 17**
- **🖥️ Node.js** + **npm**
- **🐬 MySQL** or **🐘 PostgreSQL**
- **🐋 Docker** (optional for 🛳️ deployment)

### 🔧 Steps:

1. **📥 Clone the Repo:**

   ```bash
   git clone https://github.com/your-username/hirelens.git
   cd hirelens
   ```

2. **⚙️ Backend Setup:**

   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

3. **🌐 Frontend Setup:**

   ```bash
   npm install
   npm start
   ```

4. **🚀 Run the App:**

   - Backend: `http://localhost:8080`
   - Frontend: `http://localhost:4200`

---

## 🚢 Deployment Guide

### 🐋 Docker:

1. Build Docker 🛠️ images:
   ```bash
   docker-compose build
   ```
2. Start containers 🚀:
   ```bash
   docker-compose up
   ```

### 🌐 Hosting:

- Deploy on **☁️ Heroku**, **Render**, or other ☁️ providers.

---

## 🛡️ Testing

- **✅ Unit Tests:**
  ```bash
  ./mvnw test
  ```
- **🔗 Integration Tests:**
  ```bash
  npm run e2e
  ```

---

## 🤝 Contributing

1. 🪜 Fork this repo.
2. Create 🆕 branch: (`git checkout -b feature-name`).
3. Commit your ✨ changes (`git commit -m '✨ Add feature'`).
4. 📤 Push branch: (`git push origin feature-name`).
5. Open 🔗 pull request.

---

## 📜 License

🚫 No license yet. Contributions welcome 🙌.

---

## ✉️ Contact

- **Name:** Godwin N Jethro 🧑‍💻
- **📧 Email:** [godwinjethro@gmail.com](mailto:godwinjethro@gmail.com)
- **🐙 GitHub:** [https://github.com/Godwin-munan](https://github.com/Godwin-munan)

---

## 🌈 Future Enhancements

- 🤖 AI resume scoring.
- 📊 Advanced analytics.
- 📁 Multi-format support (PDF, DOCX).
- 💬 WebSocket for real-time interactivity 🌟.

