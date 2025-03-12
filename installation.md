## installation.md

# BCID Installation Guide

## Prerequisites

- **Java JDK 8+**
- **Gradle** (build tool)
- **PostgreSQL** (database for storing identifier metadata)

## Setup Steps

1. **Clone the Repository**

   ```bash
   git clone https://github.com/biocodellc/bcid.git
   cd bcid
   ```

2. **Configure the Database**

   ```sql
   CREATE DATABASE bcid_db;
   CREATE USER bcid_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE bcid_db TO bcid_user;
   ```

   Update `application.properties` with:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/bcid_db
   spring.datasource.username=bcid_user
   spring.datasource.password=your_password
   ```

3. **Build and Run the Application**

   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

4. **Access the Service**

   - Open `http://localhost:8080/` in a browser.
   - API services are available at `http://localhost:8080/api/`

