const API_URL = import.meta.env.VITE_API_URL || 'https://task-manager-api-wmx2.onrender.com'

function getToken() {
  return localStorage.getItem('token')
}

async function request(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  }

  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const res = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `Erro ${res.status}`)
  }

  // 204 No Content ou 201/200 com body vazio → retorna null sem tentar parsear
  if (res.status === 204) return null
  const contentLength = res.headers.get('content-length')
  const contentType = res.headers.get('content-type') || ''
  if (contentLength === '0' || !contentType.includes('application/json')) return null
  return res.json()
}

export const api = {
  register: (data) =>
    request('/api/v1/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  login: async (email, senha) => {
    const data = await request('/api/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha }),
    })
    localStorage.setItem('token', data.token)
    return data
  },

  logout: () => localStorage.removeItem('token'),

  isLoggedIn: () => !!getToken(),

  resumo: (inicio, fim) =>
    request(`/api/v1/dashboard/resumo?inicio=${inicio}&fim=${fim}`),

  lancamentos: () => request('/api/v1/lancamentos'),

  contas: () => request('/api/v1/contas'),
}