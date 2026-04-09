# 📊 Ứng dụng Đặt vé Microservices - Phân tích và thiết kế

Tài liệu này phác thảo quá trình **phân tích** và **thiết kế** cho hệ thống Ứng Dụng Đặt vé dựa trên kiến trúc
Microservices. Nó giải thích các quyết định về kiến trúc và các thành phần hệ thống.

---

## 1. 🎯 Phát Biểu Vấn Đề

Ứng Dụng Đặt vé là một nền tảng được thiết kế để quản lý vé, sự kiện và cho phép người dùng đặt vé tham gia các sự kiện.

- **Đối tượng người dùng là ai?**
    - Người dùng muốn đặt vé tham gia sự kiện
    - Quản trị viên quản lý nền tảng

- **Mục tiêu chính là gì?**
    - Cho phép quản trị viên tạo và quản lý vé cho các sự kiện cũng như nơi tổ chức sự kiện
    - Theo dõi hiệu suất và tiến trình của người dùng
    - Giúp người dùng đặt vé cho sự kiện
    - Đảm bảo khả năng mở rộng và độ tin cậy của nền tảng

- **Loại dữ liệu nào được xử lý?**
    - Thông tin người dùng
    - Thông tin địa điểm và sự kiện tổ chức
    - Thông tin về việc đặt vé của người dùng

---

## 2. 🧩 Các Microservices Đã Xác Định

List the microservices in your system and their responsibilities.

| Tên dịch vụ           | Trách nhiệm                                                         | Stack công nghệ                    |
|------------------------|---------------------------------------------------------------------|------------------------------------|
| Inventory Service      | Quản lý thông tin địa điểm tổ chức và sự kiện                       | Spring Boot, MySQL                 |
| Booking Service        | Xử lý yêu cầu đặt vé                                                | Spring Boot, MySQL, Kafka          |
| Order Service          | Xử lý các yêu cầu đặt vé từ Booking service và cập nhật số lượng vé | Spring Boot, MySQL, Kafka          |
| Auth Service           | Xác thực người dùng, cấp phát và xác thực JWT token                 | Spring Boot, MySQL, Spring Security|
| Eureka Server          | Khám phá và đăng ký dịch vụ                                         | Spring Cloud Netflix Eureka Server |
| Gateway                | Định tuyến yêu cầu và đóng vai trò như điểm vào hệ thống            | Spring Cloud Gateway               |

---

## 3. 🗂️ Thiết Kế Dữ Liệu

Mỗi dịch vụ duy trì cơ sở dữ liệu riêng:

- **Cơ Sở Dữ Liệu Inventory Service**:
  - `venue`: Thông tin địa điểm tổ chức sự kiện
  - `event`: Thông tin về sự kiện cũng như lượng vé của sự kiện đó
    ![![Cơ sở dữ liệu của inventory service](./assets/inventory_db.png)](./assets/inventory_db.png)

- **Cơ Sở Dữ Liệu Auth Service**:
  - `customer`: Thông tin về người dùng (khách hàng)
    ![![Cơ sở dữ liệu của booking service](./assets/auth_db.png)](./assets/auth_db.png)

- **Cơ Sở Dữ Liệu Order Service**:
  - `order`: Thông tin về yêu cầu đặt vé của người dùng
  ![![Cơ sở dữ liệu của order service](./assets/order_db.png)](./assets/order_db.png)

---

## 4. 🔐 Cân Nhắc Bảo Mật

- **Xác Thực Đầu Vào**:
  Xác thực yêu cầu ở cả phía client và server

- **Giao Tiếp An Toàn**:
  - HTTPS cho tất cả các giao tiếp API bên ngoài
  - Các cuộc gọi dịch vụ nội bộ được bảo mật trong mạng Docker

- **Xử Lý Lỗi**:
  - Trình xử lý ngoại lệ tùy chỉnh ngăn chặn việc rò rỉ thông tin nhạy cảm
  - Phản hồi lỗi được chuẩn hóa trên các dịch vụ

---

## 5. 📦 Kế Hoạch Triển Khai

- **Container hóa với Docker**:
  - Mỗi dịch vụ có Dockerfile riêng
  - Cơ sở dữ liệu MySQL được container hóa với các script khởi tạo
  - Biến môi trường cho cấu hình dịch vụ

- **Điều Phối Container**:
  - Docker Compose cho môi trường phát triển cục bộ
  - Các dịch vụ được cấu hình để khởi động theo thứ tự thích hợp với kiểm tra sức khỏe
  - Có thể mở rộng cho sản xuất sử dụng Kubernetes (tương lai)

- **Quản Lý Cấu Hình**:
  - Cấu hình bên ngoài thông qua application.properties
  - Cài đặt dành riêng cho môi trường thông qua biến môi trường Docker
  - Chỉ định cổng nhất quán qua các môi trường

---

## ✅ Tổng Kết

Kiến trúc microservices cho Ứng dụng Quiz cung cấp một số lợi thế:

1. **Phát Triển & Triển Khai Độc Lập**: Mỗi dịch vụ có thể được phát triển, kiểm tra và triển khai độc lập, cho phép cung cấp tính năng nhanh hơn và tự chủ cho nhóm.

2. **Khả Năng Mở Rộng**: Các dịch vụ có thể được mở rộng riêng lẻ dựa trên nhu cầu.

3. **Linh Hoạt Công Nghệ**: Mặc dù hiện đang được chuẩn hóa trên Spring Boot, mỗi dịch vụ có thể tiềm năng sử dụng các công nghệ khác nhau nếu cần cho các yêu cầu cụ thể.

4. **Khả Năng Phục Hồi**: Lỗi trong một dịch vụ không làm sập toàn bộ hệ thống, và các dịch vụ có thể được thiết kế với cơ chế dự phòng.

5. **Khả Năng Bảo Trì**: Codebase nhỏ hơn, tập trung dễ hiểu và bảo trì hơn so với ứng dụng monolithic.

6. **Tối Ưu Hiệu Suất**: Tài nguyên có thể được phân bổ dựa trên nhu cầu dịch vụ cụ thể thay vì mở rộng toàn bộ ứng dụng.

Kiến trúc này hỗ trợ các yêu cầu của nền tảng đặt vé tương tác đồng thời cung cấp nền tảng cho sự phát triển và mở rộng tính năng trong tương lai.

## Tác Giả

- Nhóm 9
