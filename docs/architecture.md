# System Architecture

> This document is based on the analysis results from [Analysis and Design](./analysis-and-design.md).
> The system automates the **Online Ticket Booking** process in the entertainment domain.
>
> **References:**
> 1. *Service-Oriented Architecture: Analysis and Design for Services and Microservices* — Thomas Erl (2nd Edition)
> 2. *Microservices Patterns: With Examples in Java* — Chris Richardson
> 3. Bài tập — Phát triển phần mềm hướng dịch vụ — Hung Dang

---

## 1. Pattern Selection

| Pattern | Applied | Justification |
|---------|---------|---------------|
| API Gateway | ✅ | Tất cả request từ Frontend đều đi qua Gateway để xác thực JWT (AuthenticationFilter) và định tuyến đến service phù hợp. Che giấu địa chỉ nội bộ các service. |
| Database per Service | ✅ | Auth Service (`user_db`), Inventory Service (`inventory_db`), Order Service (`order_db`) — mỗi service có schema độc lập, có thể scale và migrate riêng. |
| Shared Database | ❌ | Không dùng — vi phạm tính độc lập của microservices; khó scale từng service riêng lẻ. |
| Task Service (Orchestration) | ✅ | Booking Service đóng vai điều phối: gọi Inventory Service kiểm tra/trừ vé, tính tiền, sau đó publish lên Kafka. Không lưu trạng thái nghiệp vụ. |
| Event-driven / Message Queue | ✅ | Kafka được dùng cho luồng Booking → Order. Tách biệt write-path, đảm bảo Availability: Order Service có thể tạm ngừng mà không mất đơn hàng. |
| Saga (Orchestration) | ❌ | MVP dùng synchronous REST cho Booking→Inventory. Luồng bù trừ (tồn kho rollback) chưa cần ở quy mô hiện tại. |
| CQRS | ❌ | Không cần thiết ở quy mô MVP. Order Service đảm nhiệm cả write (Kafka consumer) và read (`GET /orders/customer/{username}`). |
| Circuit Breaker | ❌ | Đã cấu hình sẵn trong Gateway (Resilience4j, commented-out) — có thể kích hoạt khi cần. Chưa triển khai ở MVP. |
| Service Registry / Discovery | ✅ | Eureka Server (port 8761) quản lý đăng ký và tìm kiếm service. Gateway dùng `lb://service-name` để load-balance mà không hardcode địa chỉ. |
| Health Check Endpoint | ✅ | Tất cả service expose `GET /health → { "status": "ok" }` để Docker Compose kiểm tra trạng thái. |

---

## 2. System Components

| Component | Role | Technology | Port |
|-----------|------|-----------|------|
| **Frontend** | Giao diện đặt vé cho người dùng (tìm sự kiện, đặt vé, xem lịch sử) | React + Vite, Nginx | 3000 |
| **API Gateway** | Điểm vào duy nhất; xác thực JWT (AuthenticationFilter); định tuyến request đến service | Spring Cloud Gateway | 8090 |
| **Eureka Server** | Đăng ký và khám phá service; cân bằng tải phía client | Spring Cloud Netflix Eureka | 8761 |
| **Auth Service** | Xác thực người dùng; cấp phát và validate JWT access/refresh token | Spring Boot, Spring Security, MySQL | 8085 |
| **Inventory Service** | Quản lý địa điểm (Venue) và sự kiện (Event); kiểm tra và trừ số vé | Spring Boot, JPA, MySQL | 8082 |
| **Booking Service** | Task Service — điều phối đặt vé; gọi Inventory; publish Kafka | Spring Boot, Kafka Producer | 8081 |
| **Order Service** | Entity Service — consume Kafka, lưu đơn hàng; cung cấp lịch sử đặt vé | Spring Boot, Kafka Consumer, MySQL | 8083 |
| **MySQL** | Cơ sở dữ liệu quan hệ cho Auth, Inventory, Order (3 schema riêng) | MySQL 8.0 | 3307 (host) |
| **Kafka** | Message broker cho luồng Booking → Order | Apache Kafka (KRaft mode) | 9092 |

---

## 3. Communication

### Inter-service Communication Matrix

| From \ To | Auth Service | Inventory Service | Booking Service | Order Service | Kafka |
|-----------|-------------|------------------|----------------|--------------|-------|
| **Frontend** | ✅ REST (login, refresh) via Gateway | ✅ REST (get events) via Gateway | ✅ REST (create booking) via Gateway | ✅ REST (get orders) via Gateway | ❌ |
| **API Gateway** | ✅ REST (validate token for every protected route) | ✅ REST (proxy `/api/v1/inventory/**`) | ✅ REST (proxy `/api/v1/booking/**`) | ✅ REST (proxy `/api/v1/orders/**`) | ❌ |
| **Booking Service** | ❌ | ✅ REST HTTP (check & book tickets) | — | ❌ | ✅ Produce `booking` topic |
| **Order Service** | ❌ | ❌ | ❌ | — | ✅ Consume `booking` topic |

**Protocol:** All inter-service calls use HTTP/REST with JSON. Booking → Order uses Kafka async messaging.

### Kafka Topics

| Topic | Producer | Consumer | Payload |
|-------|----------|---------|---------|
| `booking` | Booking Service | Order Service | `{ username, eventId, ticketCount, totalPrice }` |

---

## 4. Architecture Diagram

![System Architecture Diagram](./assets/architecture_diagram.png)

```
                    ┌─────────────────────────────────────────────────┐
                    │                   Docker Network                │
                    │                  (ticket-network)               │
                    │                                                 │
  ┌──────────┐      │  ┌──────────────┐      ┌───────────────────┐   │
  │ Browser  │──────┼─►│ API Gateway  │─────►│   Auth Service    │   │
  │ (React)  │      │  │   :8090      │      │      :8085        │   │
  │  :3000   │      │  │              │      │   (user_db)       │   │
  └──────────┘      │  └──────┬───────┘      └───────────────────┘   │
                    │         │                                       │
                    │         ├────────────►  Inventory Service       │
                    │         │              :8082 (inventory_db)     │
                    │         │                     ▲                 │
                    │         ├────────────►  Booking Service         │
                    │         │              :8081 ─┤                 │
                    │         │                     │ Kafka publish   │
                    │         │                     ▼                 │
                    │         │          ┌──────────────────┐         │
                    │         └────────► │   Order Service  │         │
                    │         (orders)   │      :8083       │         │
                    │                   │   (order_db)     │         │
                    │                   └──────────────────┘         │
                    │                                                 │
                    │  ┌──────────────┐   ┌───────┐   ┌──────────┐  │
                    │  │Eureka Server │   │ Kafka │   │  MySQL   │  │
                    │  │    :8761     │   │ :9092 │   │  :3307   │  │
                    │  └──────────────┘   └───────┘   └──────────┘  │
                    └─────────────────────────────────────────────────┘
```

---

## 5. Data Flow — Ticket Booking

Step-by-step request flow for the primary use case (đặt vé):

1. Người dùng gửi `POST /api/v1/booking` kèm Bearer JWT token.
2. **API Gateway** nhận request → gọi `GET /api/v1/auth/validate` (Auth Service) để xác thực token.
3. Auth Service xác minh token hợp lệ → trả 200.
4. Gateway route request đến **Booking Service**.
5. Booking Service gọi `POST /api/v1/inventory/event/{eventId}?ticketsToBook=N` → **Inventory Service** kiểm tra capacity.
   - Nếu không đủ vé → Inventory trả 400 → Booking trả lỗi cho client.
   - Nếu đủ vé → Inventory trừ N vé, trả `EventInventoryResponse` (bao gồm `ticketPrice`).
6. Booking Service tính `totalPrice = ticketCount × ticketPrice`.
7. Booking Service publish message `{ username, eventId, ticketCount, totalPrice }` lên Kafka topic `booking`.
8. Booking Service trả `BookingResponse` thành công cho client.
9. **Order Service** (Kafka consumer) nhận message, lưu `Order` vào `order_db`.
10. Người dùng có thể xem lịch sử qua `GET /api/v1/orders/customer/{username}`.

---

## 6. Deployment

- Tất cả service được container hóa bằng **Docker**.
- Điều phối bằng **Docker Compose** (`docker-compose.yml` + `docker-compose-kafka.yml`).
- Khởi động toàn bộ hệ thống:

```bash
# Start MySQL + all Spring Boot services + Kafka
docker compose -f docker-compose-kafka.yml -f docker-compose.yml up --build
```

### Environment Configuration

- Tất cả biến cấu hình (DB URL, Kafka bootstrap, JWT secret, ports) được truyền qua **environment variables** trong `docker-compose.yml`.
- Không hardcode thông tin nhạy cảm trong source code.

### Service Startup Order

```
MySQL (healthy) ──► Eureka Server (healthy) ──► Auth / Inventory / Order Services
Kafka (started) ──► Booking Service
```

### Health Check

Tất cả service expose:

```
GET /health → { "status": "ok" }
```

Docker Compose dùng health check để đảm bảo service phụ thuộc chỉ khởi động sau khi service yêu cầu đã healthy.

---

## 7. Scalability & Fault Tolerance

| Concern | Implementation |
|---------|----------------|
| **Horizontal Scaling** | Mỗi service có thể chạy nhiều instance; Eureka tự động phát hiện và Gateway load-balance. |
| **Service Discovery** | Eureka Server — tất cả service đăng ký khi khởi động và deregister khi tắt. |
| **Load Balancing** | Spring Cloud LoadBalancer (client-side) tích hợp trong Gateway và Feign clients. |
| **Async Decoupling** | Kafka tách rời Booking Service và Order Service — Order Service có thể tạm ngừng mà không mất message. |
| **Circuit Breaker** | Resilience4j đã tích hợp vào Gateway (cấu hình sẵn, có thể kích hoạt per-route). |
| **Self-healing** | Service tự đăng ký lại với Eureka sau khi khởi động lại. |
