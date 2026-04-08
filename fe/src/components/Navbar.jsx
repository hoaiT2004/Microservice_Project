import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { auth, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/events">🎟 TicketBooking</Link>
      </div>
      <div className="navbar-menu">
        {auth ? (
          <>
            <Link to="/events" className="nav-link">Sự kiện</Link>
            <span className="navbar-user">Xin chào, <strong>{auth.username}</strong></span>
            <button className="btn-logout" onClick={handleLogout}>Đăng xuất</button>
          </>
        ) : (
          <>
            <Link to="/login" className="nav-link">Đăng nhập</Link>
            <Link to="/register" className="nav-link btn-register">Đăng ký</Link>
          </>
        )}
      </div>
    </nav>
  )
}
