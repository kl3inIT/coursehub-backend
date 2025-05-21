## Tech stack
* Build tool: maven >= 3.9.5
* Java: 21
* Framework: Spring boot 3.2.x
* DBMS: MySQL

## Prerequisites
* Java SDK 21
* A MySQL server

## 3. ⚙️ Build & Run Application

### 🔹 Using Docker Compose (Recommended for local dev)

```bash
# Step 1: Clone the repository
git clone https://github.com/kl3inIT/coursehub-backend.git

# Step 2: Build and start the app
docker-compose up -d --build

# Step 3: Check running services
docker ps

# App runs at: http://localhost:8080
# MySQL runs at: localhost:3306 (user: root / password: password)
```
### If port 3306 is already in use, change it in docker-compose.yml:
```yaml
  mysql:
    ports:
      - "3307:3306"
```