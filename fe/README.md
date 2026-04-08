# Frontend - TicketBooking UI

Giao diện người dùng cho hệ thống đặt vé, được xây dựng bằng **React + Vite**.

## Tính năng

- Đăng ký / Đăng nhập tài khoản
- Xem danh sách sự kiện đang mở bán vé
- Xem chi tiết sự kiện (địa điểm, giá vé, số vé còn lại)
- Đặt vé và xem kết quả xác nhận
- Tự động làm mới JWT access token khi hết hạn

## Cấu trúc thư mục

```
fe/
├── index.html
├── vite.config.js
├── package.json
└── src/
    ├── main.jsx              # Entry point
    ├── App.jsx               # Routes
    ├── index.css             # Global styles
    ├── api/
    │   └── axiosInstance.js  # Axios với interceptor tự động gắn Bearer token
    ├── context/
    │   └── AuthContext.jsx   # Quản lý trạng thái xác thực
    ├── components/
    │   ├── Navbar.jsx        # Thanh điều hướng
    │   └── ProtectedRoute.jsx
    └── pages/
        ├── LoginPage.jsx
        ├── RegisterPage.jsx
        ├── EventsPage.jsx
        └── EventDetailPage.jsx
```

## Luồng xác thực

1. **Đăng ký**: gọi auth-service (`POST /api/v1/auth/register` qua gateway) và booking-service (`POST /bs/api/v1/auth/register` trực tiếp) để tạo tài khoản ở cả hai service.
2. **Đăng nhập**: lấy JWT từ auth-service và `customerId` từ booking-service.
3. **Các request sau**: gắn `Authorization: Bearer <token>` vào header.
4. **Token hết hạn**: interceptor tự động gọi `/api/v1/auth/refresh` và thử lại request.

## API Endpoints sử dụng

| Endpoint | Method | Service | Mô tả |
|----------|--------|---------|-------|
| `/api/v1/auth/register` | POST | auth-service (qua gateway) | Đăng ký tài khoản |
| `/api/v1/auth/login` | POST | auth-service (qua gateway) | Đăng nhập lấy JWT |
| `/api/v1/auth/refresh` | POST | auth-service (qua gateway) | Làm mới access token |
| `/bs/api/v1/auth/register` | POST | booking-service (trực tiếp) | Tạo customer |
| `/bs/api/v1/auth/login` | POST | booking-service (trực tiếp) | Lấy customerId |
| `/api/v1/inventory/events` | GET | inventory-service (qua gateway) | Danh sách sự kiện |
| `/api/v1/inventory/event/:id` | POST | inventory-service (qua gateway) | Chi tiết sự kiện |
| `/api/v1/booking` | POST | booking-service (qua gateway) | Đặt vé |

## Yêu cầu

- Node.js 18+
- npm 9+
- Các backend service đang chạy (xem [docker-compose.yml](../docker-compose.yml))

## Chạy ứng dụng

```bash
cd fe

# Cài đặt dependencies
npm install

# Chạy ở môi trường development (port 3000)
npm run dev

# Build production
npm run build
```

Truy cập: [http://localhost:3000](http://localhost:3000)
