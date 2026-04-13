# AI Exam Trainer

Simple fullstack web application for practicing AI-related exam questions.

## 🚀 Features

- Practice mode with random questions
- Filtering by section and source type
- Multiple choice questions
- Answer validation with explanation
- AI-generated questions (basic integration)

## 🛠 Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Data JPA
- H2 (in-memory DB)

### Frontend
- React
- Vite
- Axios

---

## ▶️ How to run

### 1. Backend

```bash
./mvnw spring-boot:run 
```

Backend runs on:
👉 http://localhost:8080

### 2. Frontend
cd frontend
```bash
npm install
npm run dev
```

Frontend runs on:
👉 http://localhost:5173
 (or 5174)

### 📡 API Endpoints
GET /api/questions/random — get random question
POST /api/questions/answer — submit answer
GET /api/questions/sections — get available sections

### 📚 Data

Questions are loaded from JSON files.

### 🎯 Future Improvements
Exam mode (score, timer)
Better UI/UX
Full AI integration (Claude/OpenAI)
Authentication
Database (PostgreSQL)
Test coverage

###
👨‍💻 Author

Evgenii Stepanov email: penumbraspb@gmail.com

docker run -p 3000:80 ai-exam-frontend

docker run -p 8080:8080 ai-exam-backend

docker-compose up --build
docker-compose down


chmod 400 ~/Downloads/ai-exam-trainer-dev.pem
ssh -i ~/Downloads/ai-exam-trainer-dev.pem ec2-user@13.49.125.103

EC2 setup cheat sheet
1. Создать EC2 instance

Параметры:

AMI: Amazon Linux 2023
Instance type: t3.micro или t3.small
Key pair: создать новый .pem
Storage: 16 GB
Security Group:
22 SSH — только с моего IP
80 HTTP — открыть наружу

После запуска сохранить:

Public IP
key file .pem
2. Подключиться к серверу

На Mac:

chmod 400 ~/Downloads/ai-exam-trainer-dev.pem
ssh -i ~/Downloads/ai-exam-trainer-dev.pem ec2-user@<EC2_PUBLIC_IP>

Пример:

ssh -i ~/Downloads/ai-exam-trainer-dev.pem ec2-user@13.49.125.103
3. Установить Docker и git

На сервере:

sudo yum update -y
sudo yum install -y docker git
sudo service docker start
sudo usermod -aG docker ec2-user
4. Установить Docker Compose

Если пакет docker-compose-plugin не находится через yum, установить вручную:

mkdir -p ~/.docker/cli-plugins

curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
-o ~/.docker/cli-plugins/docker-compose

chmod +x ~/.docker/cli-plugins/docker-compose
5. Перелогиниться

После добавления пользователя в группу docker:

exit

И снова подключиться:

ssh -i ~/Downloads/ai-exam-trainer-dev.pem ec2-user@<EC2_PUBLIC_IP>
6. Проверить, что всё установлено
   docker --version
   docker compose version
   git --version
   docker ps

7. Что сделать на сервере

Поставь Buildx вручную:

mkdir -p ~/.docker/cli-plugins

curl -SL https://github.com/docker/buildx/releases/download/v0.17.1/buildx-v0.17.1.linux-amd64 \
-o ~/.docker/cli-plugins/docker-buildx

chmod +x ~/.docker/cli-plugins/docker-buildx

docker buildx version
docker buildx create --use --name mybuilder
docker buildx inspect --bootstrap



on server after
docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d


docker compose -f docker-compose.yml -f docker-compose.prod.yml down
docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d


1. Посмотреть контейнер БД
   docker ps

Найди ai-exam-db.

2. Зайти внутрь
   docker exec -it ai-exam-db psql -U aiexam -d aiexamdb
3. Проверить таблицы
   \dt
4. Посмотреть количество записей

Например:

select count(*) from questions;
select count(*) from practical_task;
5. Выйти
   \q


