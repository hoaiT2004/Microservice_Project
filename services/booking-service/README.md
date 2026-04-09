# Booking Service

## Tổng quan

Booking Service là service xử lý yêu cầu đặt vé của người dùng.

## Chức năng chính

- Xử lý yêu cầu đặt vé từ người dùng

## Cấu trúc API

| Endpoint                | Phương thức | Mô tả                                        |
|-------------------------|-------------|----------------------------------------------|
| `/health`               | GET         | Kiểm tra trạng thái hoạt động của service    |
| `/api/v1/booking`       | POST        | Tạo yêu cầu đặt vé cho sự kiện               |

### Health Check

```
GET /health
Response: { "status": "ok" }
```

## Mô hình dữ liệu

Không có

## Kết nối với các Service khác

Booking Service kết nối với các service:

- **Inventory Service**: Gọi tới Inventory Service để lấy thông tin về sự kiện và kiểm tra số lượng vé còn lại
- **Order Service**: Gửi yêu cầu đặt vé đã xử lý tới Order Service qua Kafka topic `booking`

## Cấu hình

Service chạy trên port **8081** với các cấu hình cơ bản:

```properties
server.port=8081
spring.application.name=booking-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Yêu cầu

- Java 21+
- Maven
- MySQL
- Kafka

### Cấu hình cơ sở dữ liệu

```properties
Không có
```

### Lệnh chạy

```bash
# Đi đến thư mục Booking Service
cd services/booking-service

# Build project
./mvnw clean package -DskipTests

# Chạy service
java -jar target/booking-service-0.0.1-SNAPSHOT.jar
```

### Sử dụng Docker

```bash
# Build Docker image
docker build -t booking-service .

# Chạy container
docker run -p 8081:8081 booking-service
```


## Mô hình dữ liệu

Không có

## Kết nối với các Service khác

Booking Service kết nối với các service:

- **Inventory Service**: Gọi tới Inventory Service để lấy thông tin về sự kiện yêu cầu đặt vé cũng như số lượng vé còn lại
- **Order Service**: Gửi các yêu cầu đặt vé đã xử lý tới Order Service thông qua Kafka message broker, topic là `booking`

## Cấu hình

Service chạy trên port 8080 với các cấu hình cơ bản:

```properties
server.port=8081
spring.application.name=booking-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Yêu cầu

- Java 21+
- Maven
- MySQL

### Cấu hình cơ sở dữ liệu

```properties
Không có
```

### Lệnh chạy

```bash
# Đi đến thư mục Inventory Service
cd booking-service

# Build project
./mvnw clean package

# Chạy service
java -jar target/booking-service-0.0.1-SNAPSHOT.jar
```

### Sử dụng Docker

```bash
# Build Docker image
docker build -t booking-service .

# Chạy container
docker run -p 8081:8081 booking-service
```