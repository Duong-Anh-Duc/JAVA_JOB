# Kiến trúc backend

Backend dùng package-by-feature. Mỗi module nghiệp vụ sở hữu các thành phần của mình:

```text
com.example.cv
├── auth/
│   ├── controller/       # login, register, refresh, logout, account
│   ├── dto/              # request/response của auth
│   └── service/          # token flow và authentication nghiệp vụ
├── user/
│   ├── controller/
│   ├── dto/
│   ├── document/         # MongoDB document
│   ├── repository/
│   └── service/
├── company/              # cùng cấu trúc như user
├── job/
├── resume/
├── role/
├── permission/
├── subscriber/
├── analytics/
│   ├── controller/
│   ├── dto/
│   ├── entity/            # PostgreSQL/JPA entity
│   ├── repository/
│   └── service/
├── file/controller/
├── mail/
│   ├── controller/
│   └── service/
├── common/                # response, security context, shared value objects
└── config/                # database, security, scheduler, repository wiring
```

## Quy tắc dữ liệu

- MongoDB giữ các collection nghiệp vụ và tiếp tục dùng ObjectId của hệ thống NestJS.
- PostgreSQL chỉ giữ `analytics`; Flyway quản lý migration tại `db/migration`.
- `isDeleted` được dùng cho soft delete.
- Các response HTTP giữ envelope `{ statusCode, message, data }` tương thích NestJS.

## Chạy local

```bash
docker compose up -d --build
cp .env.example .env
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
