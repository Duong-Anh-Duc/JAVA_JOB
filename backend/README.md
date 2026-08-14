# CV Backend - Spring Boot migration

Đây là backend Spring Boot chạy song song với backend NestJS hiện tại. Hai dự án nằm cùng cấp trong `JAVA_JOB`; bản Spring Boot dùng cùng MongoDB cho dữ liệu nghiệp vụ và PostgreSQL cho bảng `analytics`.

Kiến trúc đã được tổ chức theo package-by-feature; xem [docs/architecture.md](docs/architecture.md).

## Chạy bằng Docker

Từ thư mục `backend`, build và chạy toàn bộ MongoDB, PostgreSQL, MailHog, Spring Boot và React frontend:

```bash
docker compose up -d --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8081/api/v1
- MailHog: http://localhost:8025

## Chạy backend local

Yêu cầu Java 17 và Maven 3.9+.

```bash
cd JAVA_JOB/backend
docker compose up -d mongodb postgres mailhog
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

API giữ prefix tương thích: `/api/v1/...`. Cấu hình lấy từ các biến môi trường cũ của NestJS (`MONGODB_URI`, `POSTGRES_*`, `JWT_*`, `MAIL_*`, `SHOULD_INIT`, `INIT_PASSWORD`). Có thể copy `.env.example` để tham khảo.

## Trạng thái chuyển đổi

- Đã dựng cấu hình Spring Boot, MongoDB, PostgreSQL/JPA, response envelope và JWT.
- Đã chuyển nhóm auth, users, companies, jobs, roles, permissions, resumes, subscribers, analytics, upload và email/cron.
- Migration PostgreSQL cho bảng `analytics` nằm trong `src/main/resources/db/migration` và được Flyway chạy tự động.

Không bật `spring.jpa.hibernate.ddl-auto=update`: bảng analytics nên được tạo bằng migration PostgreSQL hiện có trước khi chạy production.
