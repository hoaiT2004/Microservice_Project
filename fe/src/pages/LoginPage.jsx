import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await axios.post("/api/v1/auth/login", {
        username: form.username,
        password: form.password,
      });
      const { accessToken, refreshToken, username } = res.data;
      login(accessToken, refreshToken, username);
      navigate("/");
    } catch (err) {
      console.error("Login error:", err.response?.status, err.response?.data, err.message);
      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          err.message ||
          "Đăng nhập thất bại. Vui lòng kiểm tra lại tên đăng nhập và mật khẩu.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="auth-icon">🎟</div>
        <h2>Đăng nhập</h2>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Tên đăng nhập</label>
            <input
              type="text"
              name="username"
              value={form.username}
              onChange={handleChange}
              placeholder="Nhập tên đăng nhập"
              autoComplete="username"
              required
            />
          </div>
          <div className="form-group">
            <label>Mật khẩu</label>
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="Nhập mật khẩu"
              autoComplete="current-password"
              required
            />
          </div>
          <button
            type="submit"
            className="btn-primary full-width"
            disabled={loading}
          >
            {loading ? "Đang đăng nhập..." : "Đăng nhập"}
          </button>
        </form>
      </div>
    </div>
  );
}
