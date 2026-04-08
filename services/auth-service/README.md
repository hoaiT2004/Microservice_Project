# Auth Service

## Tổng quan

Auth Service là service xác thực và phân quyền người dùng, cung cấp JWT-based authentication cho toàn bộ hệ thống.

## Chức năng chính

- Đăng ký tài khoản người dùng mới
- Đăng nhập và cấp phát JWT access token + refresh token
- Làm mới access token khi hết hạn
- Xác thực tính hợp lệ của token (dùng bởi API Gateway)

## Cấu trúc API

| Endpoint                    | Phương thức | Mô tả                                        |
|-----------------------------|-------------|----------------------------------------------|
| `/health`                   | GET         | Kiểm tra trạng thái hoạt động của service    |
| `/api/v1/auth/register`     | POST        | Đăng ký tài khoản người dùng mới             |
| `/api/v1/auth/login`        | POST        | Đăng nhập, nhận access token + refresh token |
| `/api/v1/auth/refresh`      | POST        | Làm mới access token bằng refresh token      |
| `/api/v1/auth/validate`     | GET         | Xác thực tính hợp lệ của token               |

### Health Check

```
GET /health
Response: { "status": "ok" }
```

### Token Strategy

- **Access Token**: hết hạn sau 10 phút
- **Refresh Token**: hết hạn sau 24 giờ; tự động gia hạn nếu còn dưới 2 giờ

## Mô hình dữ liệu

Service sử dụng cơ sở dữ liệu MySQL với bảng:

- `user`: Lưu thông tin tài khoản người dùng (username, password đã hash, v.v.)

## Kết nối với các Service khác

- **API Gateway**: Gateway gọi `/api/v1/auth/validate` để xác thực token trước khi chuyển tiếp request

## Cấu hình

Service chạy trên port **8085** với các cấu hình cơ bản:

```properties
server.port=8085
spring.application.name=auth-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Yêu cầu

- Java 17+
- Maven
- MySQL

### Cấu hình cơ sở dữ liệu

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/user_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=24052004
```

### Lệnh chạy

```bash
# Đi đến thư mục Auth Service
cd services/auth-service

# Build project
./mvnw clean package -DskipTests

# Chạy service
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

### Sử dụng Docker

```bash
# Build Docker image
docker build -t auth-service .

# Chạy container
docker run -p 8085:8085 auth-service
```
