# Order Service

## Tổng quan

Order Service nhận, xử lý và lưu các yêu cầu đặt vé hợp lệ của người dùng thông qua Kafka message broker.

## Chức năng chính

- Lắng nghe sự kiện đặt vé từ Kafka topic `booking`
- Lưu và xử lý các yêu cầu đặt vé hợp lệ
- Gọi Inventory Service để cập nhật số lượng vé sau khi lưu đơn thành công

## Cấu trúc API

| Endpoint  | Phương thức | Mô tả                                       |
|-----------|-------------|---------------------------------------------|
| `/health` | GET         | Kiểm tra trạng thái hoạt động của service   |

### Health Check

```
GET /health
Response: { "status": "ok" }
```

## Mô hình dữ liệu

Service sử dụng cơ sở dữ liệu MySQL với các bảng chính:

- `order`: Lưu yêu cầu đặt vé hợp lệ của người dùng (khách hàng)

## Kết nối với các Service khác

Order Service kết nối với các service:

- **Booking Service**: Nhận yêu cầu đặt vé từ Kafka topic `booking`
- **Inventory Service**: Gọi để cập nhật số lượng vé sau khi lưu đơn hàng thành công

## Cấu hình

Service chạy trên port **8083** với các cấu hình cơ bản:

```properties
server.port=8083
spring.application.name=order-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Yêu cầu

- Java 21+
- Maven
- MySQL
- Kafka

### Cấu hình cơ sở dữ liệu

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/order_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=24052004
```

### Lệnh chạy

```bash
# Đi đến thư mục Order Service
cd services/order-service

# Build project
./mvnw clean package -DskipTests

# Chạy service
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

### Sử dụng Docker

```bash
# Build Docker image
docker build -t order-service .

# Chạy container
docker run -p 8083:8083 order-service
```


## Mô hình dữ liệu

Service sử dụng cơ sở dữ liệu MySQL với các bảng chính:

- `order`: Lưu yêu cầu đặt vé hợp lệ của người dùng (khách hàng)

## Kết nối với các Service khác

Order Service kết nối với các service:

- **Booking Service**: Nhận các yêu cầu đặt vé đã được xử lý từ Kafka message broker thông qua topic `booking`

- **Inventory Service**: Gọi tới Inventory Service để cập nhật số lượng vé của sự kiện sau khi đã lưu các yêu cầu đặt vé hợp lệ

## Cấu hình

Service chạy trên port 8082 với các cấu hình cơ bản:

```properties
server.port=8082
spring.application.name=order-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Yêu cầu

- Java 21+
- Maven
- MySQL

### Cấu hình cơ sở dữ liệu

```properties
sspring.datasource.url=jdbc:mysql://localhost:3306/order_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=123456
```

### Lệnh chạy

```bash
# Đi đến thư mục Order Service
cd order-service

# Build project
./mvnw clean package

# Chạy service
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

### Sử dụng Docker

```bash
# Build Docker image
docker build -t order-service .

# Chạy container
docker run -p 8082:8082 order-service
```
