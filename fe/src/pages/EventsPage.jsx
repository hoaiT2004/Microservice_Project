import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axiosInstance'

export default function EventsPage() {
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [venueSearch, setVenueSearch] = useState('')
  const [eventSearch, setEventSearch] = useState('')
  const navigate = useNavigate()

  const fetchEvents = useCallback(() => {
    setLoading(true)
    setError('')
    const params = new URLSearchParams()
    if (venueSearch.trim()) params.append('venue', venueSearch.trim())
    if (eventSearch.trim()) params.append('event', eventSearch.trim())

    api
      .get(`/api/v1/inventory/venues?${params.toString()}`)
      .then((res) => {
        // Flatten: each venue contains a list of events
        const flat = []
        res.data.forEach((venue) => {
          ;(venue.events || []).forEach((ev) => {
            flat.push({
              eventId: ev.eventId,
              eventName: ev.event,
              capacity: ev.capacity,
              ticketPrice: ev.ticketPrice,
              venueName: venue.venueName,
              venueAddress: venue.venueAddress,
              venueTotalCapacity: venue.totalCapacity,
            })
          })
        })
        setEvents(flat)
      })
      .catch(() => setError('Không thể tải danh sách sự kiện. Vui lòng thử lại sau.'))
      .finally(() => setLoading(false))
  }, [venueSearch, eventSearch])

  useEffect(() => {
    fetchEvents()
  }, [])

  const handleSearch = (e) => {
    e.preventDefault()
    fetchEvents()
  }

  return (
    <div className="container">
      <button className="btn-back" onClick={() => navigate('/')}>
        ← Trang chủ
      </button>

      <div className="page-header">
        <h1>Danh sách sự kiện</h1>
        <p className="subtitle">Tìm kiếm và chọn sự kiện bạn muốn tham gia</p>
      </div>

      {/* Search bar */}
      <form className="search-bar" onSubmit={handleSearch}>
        <div className="search-field">
          <label>📍 Địa điểm</label>
          <input
            type="text"
            placeholder="Tìm theo tên địa điểm..."
            value={venueSearch}
            onChange={(e) => setVenueSearch(e.target.value)}
          />
        </div>
        <div className="search-field">
          <label>🎭 Sự kiện</label>
          <input
            type="text"
            placeholder="Tìm theo tên sự kiện..."
            value={eventSearch}
            onChange={(e) => setEventSearch(e.target.value)}
          />
        </div>
        <button type="submit" className="btn-primary btn-search">
          Tìm kiếm
        </button>
      </form>

      {loading ? (
        <div className="page-center">
          <div className="spinner"></div>
          <p>Đang tải sự kiện...</p>
        </div>
      ) : error ? (
        <div className="alert alert-error">{error}</div>
      ) : events.length === 0 ? (
        <div className="empty-state">
          <p>🎭 Không tìm thấy sự kiện nào phù hợp.</p>
        </div>
      ) : (
        <div className="event-table">
          <div className="event-table-header">
            <span className="col-event">Sự kiện</span>
            <span className="col-venue">Địa điểm</span>
            <span className="col-address">Địa chỉ</span>
            <span className="col-capacity">Sức chứa</span>
            <span className="col-action"></span>
          </div>
          {events.map((ev) => (
            <div className="event-table-row" key={ev.eventId}>
              <span className="col-event">{ev.eventName}</span>
              <span className="col-venue">{ev.venueName}</span>
              <span className="col-address">{ev.venueAddress}</span>
              <span className="col-capacity">
                {ev.venueTotalCapacity?.toLocaleString('vi-VN')}
              </span>
              <span className="col-action">
                <button
                  className="btn-detail"
                  onClick={() => navigate(`/events/${ev.eventId}`)}
                >
                  Xem chi tiết
                </button>
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
