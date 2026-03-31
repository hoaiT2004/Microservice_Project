# Inventory Service

## Tổng quan

Inventory Service là service quản lý thông tin về địa điểm tổ chức sự kiện và sự kiện diễn ra.

## Chức năng chính

- Thêm thông tin về địa điểm tổ chức mới
- Thêm thông tin về sự kiện tổ chức
- Xem, cập nhật thông tin về sự kiện, địa điểm
- Cập nhật số lượng vé của sự kiện

## Cấu trúc API

| Endpoint                                      | Phương thức | Mô tả                                      |
|-----------------------------------------------|-------------|--------------------------------------------|
| `/api/v1/venue`                               | POST        | Tạo địa điểm tổ chức mới                   |
| `/api/v1/event`                               | POST        | Tạo sự kiện mới                            |
| `/api/v1/venues`                              | GET         | Lấy danh sách các địa điểm tổ chức sự kiện |
| `/api/v1/events`                              | GET         | Lấy danh sách các sự kiện đang mở bán vé   |
| `/api/v1/venue/{venueId}`                     | GET         | Lấy thông tin về 1 địa điểm tổ chức        |
| `/api/v1/event/{eventId}`                     | GET         | Lấy thông tin về 1 sự kiện                 |
| `/api/v1/event/{eventId}/capacity/{capacity}` | PUT         | Cập nhật lại số lượng vé của sự kiện       |

## Mô hình dữ liệu

Service sử dụng cơ sở dữ liệu MySQL với các bảng chính:

- `event`: Lưu thông tin các sự kiện đang tổ chức cùng số lượng vé
- `venue`: Lưu thông tin các địa điểm tổ chức sự kiện

## Kết nối với các Service khác

Inventory Service cung cấp API cho các service khác kết nối:

- **Booking Service**: Xử lý yêu cầu đặt vé và thông tin người dùng
- **Order Service**: Xử lý việc đặt vé và cập nhật số lượng vé trong kho (inventory) thông qua API cung cấp bởi Inventory Service

## Cấu hình

Service chạy trên port 8081 với các cấu hình cơ bản:

```properties
server.port=8080
spring.application.name=inventory-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Yêu cầu

- Java 21+
- Maven
- MySQL

### Cấu hình cơ sở dữ liệu

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=123456
```

### Lệnh chạy

```bash
# Đi đến thư mục Inventory Service
cd inventory-service

# Build project
./mvnw clean package

# Chạy service
java -jar target/inventory-service-0.0.1-SNAPSHOT.jar
```

### Sử dụng Docker

```bash
# Build Docker image
docker build -t inventory-service .

# Chạy container
docker run -p 8080:8080 inventory-service
```