import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => {
    const accessToken = localStorage.getItem('accessToken')
    const username = localStorage.getItem('username')
    const customerId = localStorage.getItem('customerId')
    return accessToken ? { accessToken, username, customerId } : null
  })

  const login = (accessToken, refreshToken, username, customerId) => {
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('refreshToken', refreshToken)
    localStorage.setItem('username', username)
    localStorage.setItem('customerId', String(customerId))
    setAuth({ accessToken, username, customerId: String(customerId) })
  }

  const logout = () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('username')
    localStorage.removeItem('customerId')
    setAuth(null)
  }

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
