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

- **Auth Service**: Xác thực người dùng (đăng nhập, JWT token, refresh token).

- **Inventory Service**: Quản lý thông tin địa điểm, sự kiện, số vé sự kiện. Cung cấp API tìm kiếm sự kiện theo địa điểm/tên.

- **Booking Service**: Xử lý yêu cầu đặt vé của người dùng, kiểm tra tồn kho qua Inventory Service, gửi đơn đặt qua Kafka.

- **Order Service**: Nhận đơn đặt vé từ Kafka, lưu vào database. Cung cấp API xem lịch sử đặt vé theo người dùng.

- **API Gateway**: Định tuyến yêu cầu từ bên ngoài đến các microservice thích hợp. Xác thực JWT token cho các route cần bảo vệ.

- **Eureka Server**: Cung cấp khám phá và đăng ký dịch vụ.

- **Frontend (React)**: Giao diện web cho người dùng đặt vé.

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
|       |- auth-service.yaml        # Đặc tả API dịch vụ Auth
|       |- booking-service.yaml     # Đặc tả API dịch vụ Booking
|       |- inventory-service.yaml   # Đặc tả API dịch vụ Inventory
|       |- order-service.yaml   # Đặc tả API dịch vụ Order
├── eureka-server/                  # Máy chủ khám phá dịch vụ
│   ├── Dockerfile                  # File cấu hình Docker cho Eureka Server
│   └── src/                        # Mã nguồn Eureka Server
├── gateway/                        # API Gateway
│   ├── Dockerfile                  # File cấu hình Docker cho API Gateway
│   └── src/                        # Mã nguồn API Gateway
├── services/                       # Các microservice của ứng dụng
│   ├── auth-service/               # Dịch vụ Auth
│   │   ├── Dockerfile              # File cấu hình Docker
│   │   └── src/                    # Mã nguồn dịch vụ
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

- **MySQL** — Cơ sở dữ liệu (user_db, inventory_db, order_db)
- **Eureka Server** — Đăng ký và khám phá dịch vụ
- **API Gateway** — Điểm vào cho tất cả API
- **Auth Service** — Xác thực người dùng
- **Inventory Service** — Quản lý sự kiện và địa điểm
- **Booking Service** — Xử lý đặt vé
- **Order Service** — Lưu và quản lý đơn đặt vé
- **Zookeeper + Kafka** — Message broker
- **Kafka UI** — Giao diện giám sát Kafka
- **Frontend** — Giao diện web React

### 3. Truy cập các dịch vụ

| Dịch vụ | URL |
|---|---|
| **Frontend** | http://localhost:3000 |
| **API Gateway** | http://localhost:8090 |
| **Eureka Server** | http://localhost:8761 |
| **Kafka UI** | http://localhost:8084 |

### 4. Tài khoản test

| Username | Password |
|---|---|
| customer1 | customer123 |
| customer2 | customer123 |
| customer3 | customer123 |
| customer4 | customer123 |
| customer5 | customer123 |

### 5. Kiểm tra logs / restart

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

- **Auth Service:** `./docs/api-specs/auth-service.yaml`

    - Đăng nhập, xác thực token và gia hạn token
    - 
- **Inventory Service:** `./docs/api-specs/inventory-service.yaml`

    - Quản lý vé của sự kiện: xem, cập nhật số lượng vé

- **Booking Service:** `./docs/api-specs/booking-service.yaml`

    - Xử lý yêu cầu đặt vé của người dùng

Để xem tài liệu API được trực quan hóa, bạn có thể sử dụng công cụ Swagger UI hoặc Redoc bằng cách import các file YAML
này.

---

## 💽 Cơ Sở Dữ Liệu

Ứng dụng sử dụng MySQL để lưu trữ dữ liệu. Cơ sở dữ liệu được khởi tạo với script `init-db.sql` khi chạy với Docker
Compose.

### Chi tiết kết nối:

- **Host:** localhost
- **Port:** 3307 (mapped từ 3306 trong container)
- **Username:** root
- **Password:** 123456

Cơ sở dữ liệu được tổ chức theo mô hình microservices, với mỗi dịch vụ quản lý schema riêng:

| Database | Service | Mô tả |
|---|---|---|
| `user_db` | auth-service | Thông tin người dùng |
| `inventory_db` | inventory-service | Địa điểm, sự kiện |
| `order_db` | order-service | Đơn đặt vé |

---

## ⚙️ Yêu Cầu Hệ Thống

### Để chạy (Docker):
- Docker Engine 19.03.0+
- Docker Compose 1.27.0+
- Ít nhất 4GB RAM

### Để phát triển local:
- JDK 21
- Maven
- Node.js 18+
- MySQL 8.0

---

## 👥 Thành Viên Nhóm và Đóng Góp

| Tên              | MSV        | Đóng Góp                                 |
|------------------|------------|------------------------------------------|
| Nguyễn Xuân Hòa  | B22DCCN327 | API gateway, Eureka Server, Auth Service |
| Phan Ngọc Minh   | B22DCCN535 | Booking Service                          |
| Nguyễn Thái Bình | B22DCCN020 | Inventory Service                        |

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
