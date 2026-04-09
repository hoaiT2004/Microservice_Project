import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function HomePage() {
  const { auth } = useAuth()
  const navigate = useNavigate()

  return (
    <div className="home-wrapper">
      <div className="home-card">
        <div className="home-icon">🎟</div>
        <h1>Chào mừng, {auth?.username}!</h1>
        <p className="home-subtitle">Hệ thống đặt vé sự kiện trực tuyến</p>
        <div className="home-actions">
          <button className="btn-home btn-home-primary" onClick={() => navigate('/events')}>
            🎫 Đặt vé
          </button>
          <button className="btn-home btn-home-secondary" onClick={() => navigate('/my-tickets')}>
            📋 Vé của tôi
          </button>
        </div>
      </div>
    </div>
  )
}

