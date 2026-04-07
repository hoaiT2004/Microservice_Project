# Notification Service

## Tổng quan

Notification Service là service xử lý và gửi email thông báo tới người dùng dựa trên các sự kiện nhận được từ Kafka.

## Chức năng chính

- Gửi email xác nhận đặt vé thành công
- Gửi email chào mừng khi đăng ký tài khoản mới
- Lắng nghe sự kiện từ Kafka một cách bất đồng bộ

## Cấu trúc API

| Endpoint  | Phương thức | Mô tả                                       |
|-----------|-------------|---------------------------------------------|
| `/health` | GET         | Kiểm tra trạng thái hoạt động của service   |

### Health Check

```
GET /health
Response: { "status": "ok" }
```

## Kafka Topics

| Topic              | Nguồn            | Mô tả                                         |
|--------------------|------------------|-----------------------------------------------|
| `register_success` | Booking Service  | Nhận sự kiện đăng ký tài khoản thành công     |
| `buy_ticket_success` | Order Service  | Nhận sự kiện đặt vé thành công               |

## Kết nối với các Service khác

- **Booking Service**: Nhận `RegistrationEvent` từ topic `register_success` để gửi email chào mừng
- **Order Service**: Nhận `BookingEvent` từ topic `buy_ticket_success` để gửi email xác nhận đặt vé

## Cấu hình

Service chạy trên port **8086** với các cấu hình cơ bản:

```properties
server.port=8086
spring.application.name=notification-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
spring.kafka.consumer.group-id=notification-group
```

### Cấu hình Email (Gmail SMTP)

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your-email>@gmail.com
spring.mail.password=<app-password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Yêu cầu

- Java 17+
- Maven
- Kafka
- Tài khoản Gmail với App Password được bật

### Lệnh chạy

```bash
# Đi đến thư mục Notification Service
cd services/notification-service

# Build project
./mvnw clean package -DskipTests

# Chạy service
java -jar target/notification-service-0.0.1-SNAPSHOT.jar
```

### Sử dụng Docker

```bash
# Build Docker image
docker build -t notification-service .

# Chạy container
docker run -p 8086:8086 notification-service
```
