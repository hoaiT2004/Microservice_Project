# Kiến trúc hệ thống

## Tổng quan
- **Mục Đích**: Hệ thống microservices cung cấp nền tảng đặt vé, cho phép người dùng tạo, quản lý và đặt vé tham gia các sự kiện.
- **Thành Phần Chính**: Hệ thống bao gồm 5 thành phần chính: API Gateway, Eureka Server, Inventory Service, Booking Service và Order Service.

## Thành Phần Hệ Thống
- **Inventory Service**: Quản lý thông tin sự kiện và địa điểm tổ chức sự kiện.
- **Booking Service**: Quản lý thông tin người dùng và xử lý yêu cầu đặt vé.
- **Order Service**: Xử lý yêu cầu đặt vé và cập nhật số lượng vé sự kiện.
- **API Gateway**: Điểm vào duy nhất cho tất cả các request từ client, xử lý định tuyến và xác thực.
- **Eureka Server**: Cung cấp dịch vụ khám phá và đăng ký service, giúp các service tìm thấy nhau trong mạng.

## Giao Tiếp
- **REST APIs**: Các service giao tiếp với nhau chủ yếu thông qua REST APIs.
- **Clients**: Các service sử dụng Client để tạo các REST call tới các service khác.
- **Mạng Nội Bộ**: Các service giao tiếp qua Docker network trong môi trường containerized.

## Luồng Dữ Liệu
1. Người dùng gửi yêu cầu đặt vé tới hệ thống thông qua API: `/api/v1/booking`.
2. API gateway điều hướng tới Booking Service.
3. Booking Service kiểm tra người dùng hợp lệ. 
4. Sau khi đã kiểm tra người dùng hợp lệ, Booking Service gọi tới Inventory Service để kiểm tra thông tin vé và số lượng vé còn lại có đủ cho người dùng đặt không.
5. Sau khi kiểm tra số lượng vé và người dùng, Booking Service gửi yêu cầu đặt vé tới Kafka Message Broker qua topic `booking`.
6. Order Service nhận và xử lý lần lượt các order message trong Kafka.
7. Order Service lưu các order đã xử lý vào DB.
8. Order Service gửi yêu cầu tới Inventory Service để cập nhật số lượng vé.

## Diagram
![Sơ đồ kiến trúc hệ thống](./assets/architecture_diagram.png).

## Khả Năng Mở Rộng Và Chịu Lỗi 
- **Khả Năng Mở Rộng Ngang**: Mỗi service có thể được mở rộng độc lập bằng cách triển khai nhiều instance.
- **Khám Phá Dịch Vụ**: Eureka Server tự động phát hiện các instance mới của service.
- **Cân Bằng Tải**: API Gateway và Eureka Client thực hiện cân bằng tải giữa các instance service.
- **Khả Năng Chịu Lỗi**: Nếu một service gặp sự cố, các service khác vẫn có thể hoạt động với chức năng hạn chế.
- **Tự Phục Hồi**: Các service tự động đăng ký lại với Eureka Server khi khởi động lại sau lỗi.