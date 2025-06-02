# ✨ Metquay Blogs – Sleek Java Blog Platform


## 🚀 Key Features

- 🔐 **User Authentication**  
  Register and log in securely using email and password.

- 📝 **Post Management**  
  - Create blog posts with title, optional subtitle, and rich content.  
  - View, edit, and delete your posts.  
  - Elegant list and detailed views.

- 📱 **Responsive Design**  
  Mobile-friendly Light Mode with a cohesive color scheme:
    - Primary: `#007bff` (Blue)  
    - Accent: `#4db6ac` (Teal)

- ✅ **Content Validation**  
  - Title: 5–100 characters  
  - Subtitle: 5–100 characters 
  - Content: 50–10,000 characters

---

## ⚙️ Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Prayag-09/Metquay-Blogs.git
cd Metquay-Blogs
```

### 2. Configure the Backend

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blogdb
spring.datasource.username=bloguser
spring.datasource.password=blogpass
```

Ensure your backend API is running. Adjust endpoints in `ApiService.java` if needed.

### 3. Install Dependencies

```bash
mvn clean install
```

### 4. Run the App

```bash
mvn spring-boot:run
```

Visit: [http://localhost:3000](http://localhost:3000)


## 🌐 Navigation Overview

| Page                  | Description                                      |
|-----------------------|--------------------------------------------------|
| `/register`           | Create an account with name, email, and password |
| `/login`              | Log in to access the platform                    |
| `/posts`              | Browse all published blog posts                  |
| `/post`               | Create a new blog post                           |
| `/posts/:id`          | View, edit, or delete your own posts             |
| `/posts/:id [PUT]`    | Update an existing post                          |
| `/posts/:id [DELETE]` | Delete your own post                             |
| `/api/posts/current-user` | Get the currently logged-in user's email  |
| `/logout` (client-side)  | Clear JWT token and log out                  |
---

## 🖼️ UI Highlights

- ✅ Post published successfully  
- ❌ Please fill in all required fields

Inline styling and feedback are fully integrated into the user interface.

---

## 🛠️ Tech Stack

- **Frontend:** Vaadin Flow (Java-based UI)
- **Backend:** Spring Boot (REST API)
- **Database:** PostgreSQL
- **Build Tool:** Maven
- **Security:**  Spring Security (JWT)

---
