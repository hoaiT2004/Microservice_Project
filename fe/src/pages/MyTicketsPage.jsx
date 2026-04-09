import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axiosInstance'
import { useAuth } from '../context/AuthContext'

export default function MyTicketsPage() {
  const { auth } = useAuth()
  const navigate = useNavigate()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!auth?.username) return

    api
      .get(`/api/v1/orders/customer/${auth.username}`)
      .then(async (res) => {
        const orderList = res.data

        // For each unique eventId, fetch event details from inventory-service
        const uniqueEventIds = [...new Set(orderList.map((o) => o.eventId))]
        const eventMap = {}

        await Promise.all(
          uniqueEventIds.map(async (eventId) => {
            try {
              const eventRes = await api.post(`/api/v1/inventory/event/${eventId}`)
              eventMap[eventId] = eventRes.data
            } catch {
              eventMap[eventId] = null
            }
          }),
        )

        // Merge event info into orders
        const enriched = orderList.map((order) => ({
          ...order,
          eventInfo: eventMap[order.eventId] || null,
        }))

        setOrders(enriched)
      })
      .catch(() => setError('Không thể tải danh sách vé. Vui lòng thử lại sau.'))
      .finally(() => setLoading(false))
  }, [auth?.username])

  return (
    <div className="container">
      <button className="btn-back" onClick={() => navigate('/')}>
        ← Trang chủ
      </button>

      <div className="page-header">
        <h1>Vé của tôi</h1>
        <p className="subtitle">Danh sách các đơn đặt vé của bạn</p>
      </div>

      {loading ? (
        <div className="page-center">
          <div className="spinner"></div>
          <p>Đang tải...</p>
        </div>
      ) : error ? (
        <div className="alert alert-error">{error}</div>
      ) : orders.length === 0 ? (
        <div className="empty-state">
          <p>📋 Bạn chưa đặt vé nào.</p>
          <button className="btn-primary" onClick={() => navigate('/events')} style={{ marginTop: '1rem' }}>
            Đặt vé ngay
          </button>
        </div>
      ) : (
        <div className="tickets-list">
          {orders.map((order) => (
            <div className="ticket-card" key={order.id}>
              <div className="ticket-card-header">
                <h3>{order.eventInfo?.event || `Sự kiện #${order.eventId}`}</h3>
                <span className="ticket-id">Đơn #{order.id}</span>
              </div>
              <div className="ticket-card-body">
                <div className="ticket-info-grid">
                  <div className="ticket-info-item">
                    <span className="ticket-label">📍 Địa điểm</span>
                    <span className="ticket-value">{order.eventInfo?.venue?.name || '—'}</span>
                  </div>
                  <div className="ticket-info-item">
                    <span className="ticket-label">🏠 Địa chỉ</span>
                    <span className="ticket-value">{order.eventInfo?.venue?.address || '—'}</span>
                  </div>
                  <div className="ticket-info-item">
                    <span className="ticket-label">🎫 Số vé</span>
                    <span className="ticket-value">{order.ticketCount}</span>
                  </div>
                  <div className="ticket-info-item">
                    <span className="ticket-label">💰 Tổng tiền</span>
                    <span className="ticket-value price-large">
                      {Number(order.totalPrice).toLocaleString('vi-VN')} ₫
                    </span>
                  </div>
                  <div className="ticket-info-item">
                    <span className="ticket-label">🕐 Thời gian đặt</span>
                    <span className="ticket-value">
                      {order.placedAt
                        ? new Date(order.placedAt).toLocaleString('vi-VN')
                        : '—'}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

