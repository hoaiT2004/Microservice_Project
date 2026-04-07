import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axiosInstance'

export default function EventsPage() {
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    api
      .get('/api/v1/inventory/events')
      .then((res) => setEvents(res.data))
      .catch(() => setError('Không thể tải danh sách sự kiện. Vui lòng thử lại sau.'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="page-center">
        <div className="spinner"></div>
        <p>Đang tải sự kiện...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="container">
        <div className="alert alert-error">{error}</div>
      </div>
    )
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>Sự kiện sắp diễn ra</h1>
        <p className="subtitle">Chọn sự kiện yêu thích và đặt vé ngay hôm nay</p>
      </div>

      {events.length === 0 ? (
        <div className="empty-state">
          <p>🎭 Hiện chưa có sự kiện nào. Vui lòng quay lại sau.</p>
        </div>
      ) : (
        <div className="events-grid">
          {events.map((event) => (
            <Link to={`/events/${event.eventId}`} key={event.eventId} className="event-card">
              <div className="event-card-badge">
                <span
                  className={
                    event.capacity > 0 ? 'badge badge-available' : 'badge badge-sold-out'
                  }
                >
                  {event.capacity > 0 ? `Còn ${event.capacity} vé` : 'Hết vé'}
                </span>
              </div>
              <div className="event-card-body">
                <h3 className="event-name">{event.event}</h3>
                <div className="event-meta">
                  <p>
                    <span className="meta-icon">📍</span>
                    {event.venue?.name}
                  </p>
                  <p>
                    <span className="meta-icon">🏠</span>
                    {event.venue?.address}
                  </p>
                </div>
              </div>
              <div className="event-card-footer">
                <span className="event-price">
                  {Number(event.ticketPrice).toLocaleString('vi-VN')} ₫
                  <span className="per-ticket">/vé</span>
                </span>
                <span className="view-detail">Xem chi tiết →</span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
