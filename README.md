# 🧩 Ứng Dụng Đặt Vé Microservices

Một nền tảng đặt vé dựa trên kiến trúc microservices mạnh mẽ, có khả năng mở rộng cao, cho phép người dùng tạo, quản lý
và đặt vé tham gia các sự kiện.

---

## 📋 Tổng Quan Hệ Thống

Ứng dụng đặt vé này được xây dựng bằng kiến trúc microservices, cung cấp một nền tảng linh hoạt để tạo các sự kiện, địa
điểm, quản lý thông tin vé, người dùng và yêu cầu đặt vé của người dùng. Hệ thống triển khai khám phá dịch vụ, mẫu API
gateway và containerization để đảm bảo khả năng mở rộng và bảo trì hiệu quả.

---

## 🏗️ Kiến Trúc Hệ Thống

Hệ thống bao gồm các microservices sau:

- **Inventory Service**: Quản lý thông tin địa điểm, sự kiện, số vé sự kiện. Dịch vụ này cung cấp API cho người dùng có
  thể xem, tạo và cập nhật thông tin về sự kiện.

- **Booking Service**: Xử lý yêu cầu đặt vé của người dùng, xem yêu cầu có hợp lệ không và quản lý thông tin khách hàng.
  Dịch vụ này cung cấp API chp người dùng có thể đăng nhập, đăng ký và tạo yêu cầu đặt vé.

- **Order Service**: Xử lý và lưu các yêu cầu đặt vé đã hợp lệ của người dùng, đồng thời thực hiện cập nhật lại lượng vé
  của sự kiện với mỗi yêu cầu đặt vé.

- **API Gateway**: Định tuyến yêu cầu từ bên ngoài đến các microservice thích hợp. Đóng vai trò là điểm vào duy nhất cho
  tất cả các yêu cầu của client. Cung cấp các tính năng như định tuyến, cân bằng tải, giới hạn tốc độ và giám sát.

- **Eureka Server**: Cung cấp khám phá và đăng ký dịch vụ. Giúp các dịch vụ định vị và giao tiếp với nhau một cách động.
  Cho phép hệ thống tự phục hồi khi các dịch vụ khởi động hoặc tắt.

Các dịch vụ giao tiếp chủ yếu thông qua REST API sử dụng các mẫu sau:

- Giao tiếp bên ngoài thông qua API Gateway
- Giao tiếp nội bộ thông qua Rest API, cho phép các lệnh gọi REST được khai báo theo kiểu khai báo
- Khám phá dịch vụ thông qua Eureka Server

## 📁 Cấu Trúc Thư Mục

```
ticket-application/
├── README.md                       # File hướng dẫn này
├── docker-compose.yml              # Cấu hình Docker Compose để chạy tất cả dịch vụ
├── docs/                           # Thư mục tài liệu
│   ├── architecture.md             # Tài liệu thiết kế hệ thống
│   ├── analysis-and-design.md      # Chi tiết phân tích và thiết kế hệ thống
│   ├── asset/                      # Tài nguyên hình ảnh cho tài liệu
│   └── api-specs/                  # Đặc tả API theo chuẩn OpenAPI (YAML)
|       |- booking-service.yaml     # Đặc tả API dịch vụ Booking
|       |- inventory-service.yaml   # Đặc tả API dịch vụ Inventory
├── eureka-server/                  # Máy chủ khám phá dịch vụ
│   ├── Dockerfile                  # File cấu hình Docker cho Eureka Server
│   └── src/                        # Mã nguồn Eureka Server
├── gateway/                        # API Gateway
│   ├── Dockerfile                  # File cấu hình Docker cho API Gateway
│   └── src/                        # Mã nguồn API Gateway
├── services/                       # Các microservice của ứng dụng
│   ├── inventory-service/          # Dịch vụ Inventory
│   │   ├── Dockerfile              # File cấu hình Docker
│   │   └── src/                    # Mã nguồn dịch vụ
│   ├── booking-service/            # Dịch vụ Booking
│   │   ├── Dockerfile              # File cấu hình Docker
│   │   └── src/                    # Mã nguồn dịch vụ
│   ├── order-service/              # Dịch vụ Order
│   │   ├── Dockerfile              # File cấu hình Docker
│   │   └── src/                    # Mã nguồn dịch vụ
```

---

## 🚀 Bắt Đầu Sử Dụng

### 1. Clone repository này

```bash
git clone https://github.com/jnp2018/mid-project-535055020.git ticket-application
cd ticket-application
```

### 2. Chạy với Docker Compose

```bash
docker compose up -d
```

Lệnh này sẽ tạo và chạy tất cả các container dịch vụ đã được định nghĩa trong file docker-compose.yml, bao gồm:

- Cơ sở dữ liệu MySQL
- Eureka Server để đăng ký và khám phá dịch vụ
- API Gateway
- Các microservices (Inventory, Booking, Order)
- Zookeeper giữ theo dõi trạng thái của các broker trong cụm Kafka
- Kafka Broker để chạy server message broker Apache Kafka
- Kafka UI để xem các message gửi qua Kafka

### 3. Truy cập các dịch vụ

Sau khi các dịch vụ đã được khởi động thành công, bạn có thể truy cập chúng tại:

- **Eureka Server:** http://localhost:8761

    - Giao diện đăng ký và giám sát dịch vụ
    - Xem các dịch vụ đã đăng ký và trạng thái của chúng

- **API Gateway:** http://localhost:8090

    - Điểm vào chính cho tất cả các yêu cầu từ client
    - Tất cả các yêu cầu API nên được định tuyến thông qua endpoint này

- **Kafka UI:** http://localhost:8084/ui/clusters

    - Giao diện theo dõi các message gửi qua Kafka
    - Có thể xem topic cũng như consumer

### 4. Khởi động lại một dịch vụ riêng lẻ (nếu cần)

```bash
docker-compose restart <tên-dịch-vụ>
```

Ví dụ: `docker-compose restart inventory-service`

### 5. Kiểm tra logs của dịch vụ

```bash
docker-compose logs -f <tên-dịch-vụ>
```

Ví dụ: `docker-compose logs -f inventory-service`

### 6. Dừng hệ thống

```bash
docker-compose down
```

---

## 🌐 Tài Liệu API

Đặc tả API cho mỗi dịch vụ được cung cấp dưới định dạng OpenAPI và có sẵn tại:

- **Inventory Service:** `./docs/api-specs/inventory-service.yaml`

    - Quản lý địa điểm: xem, tạo thông tin địa điểm
    - Quản lý sự kiện: xem, tạo sự kiện, cập nhật số lượng vé

- **Booking Service:** `./docs/api-specs/booking-service.yaml`

    - Xử lý yêu cầu đặt vé của người dùng
    - Đăng nhập, đăng ký khách hàng mới

Để xem tài liệu API được trực quan hóa, bạn có thể sử dụng công cụ Swagger UI hoặc Redoc bằng cách import các file YAML
này.

---

## 💽 Cơ Sở Dữ Liệu

Ứng dụng sử dụng MySQL để lưu trữ dữ liệu. Cơ sở dữ liệu được khởi tạo với script `init-db.sql` khi chạy với Docker
Compose.

### Chi tiết kết nối:

- **Host:** localhost
- **Port:** 3307 (được ánh xạ từ port 3306 trong container)
- **Tên người dùng:** root
- **Mật khẩu:** 123456

### Cấu trúc cơ sở dữ liệu:

Cơ sở dữ liệu được tổ chức theo mô hình microservices, với mỗi dịch vụ quản lý schema riêng:

- `inventory_db`: Lưu trữ thông tin địa điểm, sự kiện
- `auth_db`: Lưu trữ thông tin người dùng
- `order_db`: Lưu trữ yêu cầu đặt vé hợp lệ

Script `init-db.sql` tạo các database và bảng cần thiết, đồng thời thiết lập các mối quan hệ và chèn dữ liệu mẫu để kiểm
thử.

---

## ⚙️ Yêu Cầu Hệ Thống

### Để chạy ứng dụng:

- **Docker và Docker Compose:**
    - Docker Engine phiên bản 19.03.0 trở lên
    - Docker Compose phiên bản 1.27.0 trở lên

### Để phát triển cục bộ:

- **JDK 11 trở lên:** Cần thiết để biên dịch và chạy các dịch vụ Java
- **Maven:** Quản lý phụ thuộc và xây dựng dự án
- **IDE đề xuất:**Visual Code
- **Postman hoặc Insomnia:** Để kiểm thử API

### Các yêu cầu bổ sung:

- **Git:** Để quản lý phiên bản mã nguồn
- **MySQL Workbench:** Để quản lý và theo dõi cơ sở dữ liệu (tùy chọn)
- **Ít nhất 4GB RAM:** Để chạy tất cả các dịch vụ cùng lúc

1. **Clients** gửi yêu cầu đến **API Gateway**
2. **API Gateway** định tuyến yêu cầu đến dịch vụ thích hợp, được xác định thông qua **Eureka Server**

## 🔌 Tích Hợp Swagger và Mô Tả API

Hệ thống này tích hợp Swagger/OpenAPI để cung cấp tài liệu API trực quan cho tất cả các microservices. Mỗi service có
tài liệu API riêng có thể truy cập thông qua giao diện Swagger UI.

### Truy cập Swagger UI

Sau khi khởi động các services, bạn có thể truy cập tài liệu API tại:

- **Inventory Service:** http://localhost:8080/swagger-ui.html
- **Booking Service:** http://localhost:8081/swagger-ui.html
- **API Gateway:** http://localhost:8090/swagger-ui.html

### Cấu Hình OpenAPI

Mỗi service đều sử dụng thư viện SpringDoc OpenAPI để tạo tài liệu API:

```properties
# Cấu hình Swagger/OpenAPI (ví dụ từ application.properties)
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

### Mô Tả API Chi Tiết

#### 1. Inventory Service API

| Endpoint                                                | Phương thức | Mô tả                              | Tham số chính                                     |
|---------------------------------------------------------|-------------|------------------------------------|---------------------------------------------------|
| `/api/v1/inventory/venue`                               | POST        | Tạo địa điểm mới                   | `name`, `address`, `totalCapacity`                |
| `/api/v1/inventory/event`                               | POST        | Tạo sự kiện mới                    | `name`, `totalCapacity`, `ticketPrice`, `venueId` |
| `/api/v1/inventory/venues`                              | GET         | Lấy thông tin tất cả địa điểm      |                                                   |
| `/api/v1/inventory/venue/{venueId}`                     | GET         | Lấy thông tin 1 địa điểm           |                                                   |
| `/api/v1/inventory/events`                              | GET         | Lấy thông tin tất cả sự kiện       |                                                   |
| `/api/v1/inventory/event/{eventId}`                     | GET         | Lấy thông tin 1 sự kiện            |                                                   |
| `/api/v1/inventory/event/{eventId}/capacity/{capacity}` | PUT         | Cập nhật số lượng vé của 1 sự kiện |                                                   |

#### 2. Booking Service API

| Endpoint                | Phương thức | Mô tả                  | Tham số chính                                      |
|-------------------------|-------------|------------------------|----------------------------------------------------|
| `/api/v1/auth/login`    | POST        | Đăng nhập              | `username`, `password`                             |
| `/api/v1/auth/register` | POST        | Đăng ký khách hàng mới | `name`, `email`, `address`, `username`, `password` |
| `/api/v1/booking/{id}`  | POST        | Tạo yêu cầu đặt vé mới | `userId`, `eventId`, `ticketCount`                 |

---

## 👥 Thành Viên Nhóm và Đóng Góp

| Tên              | MSV        | Đóng Góp                                  |
|------------------|------------|-------------------------------------------|
| Bùi Huy Hoàng    | B21DCCN055 | API gateway, Eureka Server, Order Service |
| Phan Ngọc Minh   | B21DCCN535 | Booking Service                           |
| Nguyễn Thái Bình | B21DCCN020 | Inventory Service                         |

## 📚 Ghi Chú Phát Triển

### Kiến trúc và Thiết kế:

- Các dịch vụ đăng ký với Eureka để khám phá dịch vụ, cho phép chúng tự động tìm và giao tiếp với nhau
- Mỗi dịch vụ có schema cơ sở dữ liệu riêng, tuân thủ nguyên tắc của kiến trúc microservices
- API Gateway định tuyến tất cả các yêu cầu từ client đến các dịch vụ thích hợp, cung cấp một điểm vào duy nhất

### Các thực hành tốt nhất:

- Triển khai Circuit Breakers để ngăn lỗi dây chuyền giữa các dịch vụ
- Sử dụng centralized logging để theo dõi và gỡ lỗi trong môi trường phân tán
- Triển khai health checks cho mỗi dịch vụ để giám sát tình trạng hệ thống

### Hướng phát triển tương lai:

- Thêm Payment Service để xử lý việc thanh toán trước khi lưu order và cập nhật lượng vé
- Mở rộng khả năng quốc tế hóa với hỗ trợ đa ngôn ngữ

---
