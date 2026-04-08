import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import api from '../api/axiosInstance'
import { useAuth } from '../context/AuthContext'

export default function EventDetailPage() {
  const { id } = useParams()
  const { auth } = useAuth()
  const navigate = useNavigate()

  const [event, setEvent] = useState(null)
  const [ticketCount, setTicketCount] = useState(1)
  const [loadingEvent, setLoadingEvent] = useState(true)
  const [booking, setBooking] = useState(false)
  const [fetchError, setFetchError] = useState('')
  const [bookError, setBookError] = useState('')
  const [bookingResult, setBookingResult] = useState(null)

  useEffect(() => {
    // POST /api/v1/inventory/event/{id} with no params returns event info
    api
      .post(`/api/v1/inventory/event/${id}`)
      .then((res) => setEvent(res.data))
      .catch(() => setFetchError('Không thể tải thông tin sự kiện.'))
      .finally(() => setLoadingEvent(false))
  }, [id])

  const maxTickets = event ? Math.min(event.capacity, 10) : 1

  const handleBook = async (e) => {
    e.preventDefault()
    setBookError('')
    setBooking(true)
    try {
      const res = await api.post('/api/v1/booking', {
        userId: Number(auth.customerId),
        eventId: Number(id),
        ticketCount: Number(ticketCount),
      })
      setBookingResult(res.data)
    } catch (err) {
      setBookError(
        err.response?.data?.message ||
          err.response?.data ||
          'Đặt vé thất bại. Vui lòng thử lại.',
      )
    } finally {
      setBooking(false)
    }
  }

  if (loadingEvent) {
    return (
      <div className="page-center">
        <div className="spinner"></div>
        <p>Đang tải thông tin sự kiện...</p>
      </div>
    )
  }

  if (fetchError) {
    return (
      <div className="container">
        <div className="alert alert-error">{fetchError}</div>
        <button className="btn-back mt-1" onClick={() => navigate('/events')}>
          ← Quay lại danh sách sự kiện
        </button>
      </div>
    )
  }

  return (
    <div className="container">
      <button className="btn-back" onClick={() => navigate('/events')}>
        ← Danh sách sự kiện
      </button>

      {event && (
        <div className="detail-layout">
          {/* Event Info */}
          <div className="detail-info-card">
            <div className="detail-header">
              <h1>{event.event}</h1>
              <span
                className={
                  event.capacity > 0 ? 'badge badge-available' : 'badge badge-sold-out'
                }
              >
                {event.capacity > 0 ? 'Còn vé' : 'Hết vé'}
              </span>
            </div>

            <div className="info-list">
              <div className="info-item">
                <span className="info-label">📍 Địa điểm</span>
                <span className="info-value">{event.venue?.name}</span>
              </div>
              <div className="info-item">
                <span className="info-label">🏠 Địa chỉ</span>
                <span className="info-value">{event.venue?.address}</span>
              </div>
              <div className="info-item">
                <span className="info-label">🏟 Sức chứa</span>
                <span className="info-value">
                  {event.venue?.totalCapacity?.toLocaleString('vi-VN')} chỗ
                </span>
              </div>
              <div className="info-item">
                <span className="info-label">🎫 Vé còn lại</span>
                <span className="info-value highlight">{event.capacity}</span>
              </div>
              <div className="info-item">
                <span className="info-label">💰 Giá vé</span>
                <span className="info-value price-large">
                  {Number(event.ticketPrice).toLocaleString('vi-VN')} ₫
                </span>
              </div>
            </div>
          </div>

          {/* Booking Panel */}
          <div className="detail-booking-card">
            {bookingResult ? (
              <div className="booking-success-panel">
                <div className="success-icon">🎉</div>
                <h3>Đặt vé thành công!</h3>
                <div className="success-details">
                  <div className="success-row">
                    <span>Sự kiện</span>
                    <strong>{event.event}</strong>
                  </div>
                  <div className="success-row">
                    <span>Số vé</span>
                    <strong>{bookingResult.ticketCount}</strong>
                  </div>
                  <div className="success-row total-row">
                    <span>Tổng tiền</span>
                    <strong className="total-price">
                      {Number(bookingResult.totalPrice).toLocaleString('vi-VN')} ₫
                    </strong>
                  </div>
                </div>
                <p className="success-note">
                  ✅ Đơn đặt vé của bạn đang được xử lý.
                </p>
                <button className="btn-primary full-width" onClick={() => navigate('/events')}>
                  Về trang sự kiện
                </button>
              </div>
            ) : event.capacity > 0 ? (
              <>
                <h3>Đặt vé</h3>
                {bookError && <div className="alert alert-error">{bookError}</div>}
                <form onSubmit={handleBook}>
                  <div className="form-group">
                    <label>Số lượng vé</label>
                    <div className="ticket-counter">
                      <button
                        type="button"
                        className="counter-btn"
                        onClick={() => setTicketCount((v) => Math.max(1, Number(v) - 1))}
                      >
                        −
                      </button>
                      <input
                        type="number"
                        min="1"
                        max={maxTickets}
                        value={ticketCount}
                        onChange={(e) => setTicketCount(e.target.value)}
                        required
                      />
                      <button
                        type="button"
                        className="counter-btn"
                        onClick={() =>
                          setTicketCount((v) => Math.min(maxTickets, Number(v) + 1))
                        }
                      >
                        +
                      </button>
                    </div>
                    <small>Tối đa {maxTickets} vé / lần đặt</small>
                  </div>

                  <div className="booking-summary">
                    <div className="summary-row">
                      <span>Đơn giá</span>
                      <span>{Number(event.ticketPrice).toLocaleString('vi-VN')} ₫</span>
                    </div>
                    <div className="summary-row summary-total">
                      <span>Tổng cộng</span>
                      <strong>
                        {(
                          Number(ticketCount) * Number(event.ticketPrice)
                        ).toLocaleString('vi-VN')}{' '}
                        ₫
                      </strong>
                    </div>
                  </div>

                  <button type="submit" className="btn-primary full-width" disabled={booking}>
                    {booking ? 'Đang xử lý...' : '🎫 Xác nhận đặt vé'}
                  </button>
                </form>
              </>
            ) : (
              <div className="sold-out-panel">
                <div className="sold-out-icon">😔</div>
                <h3>Sự kiện đã hết vé</h3>
                <p>Vui lòng xem các sự kiện khác.</p>
                <button className="btn-primary full-width" onClick={() => navigate('/events')}>
                  Tìm sự kiện khác
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
