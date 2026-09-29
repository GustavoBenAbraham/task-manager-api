// Remove barra final se houver, para evitar URLs com barra dupla (ex: base/ + /path)
const API_URL = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '')

function getToken() {
  return localStorage.getItem('token')
}

function getActiveSpaceId() {
  return localStorage.getItem('espacoFinanceiroId')
}

async function request(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  }

  const token = getToken()
  const spaceId = getActiveSpaceId()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }
  if (spaceId) {
    headers['X-Espaco-Financeiro-Id'] = spaceId
  }

  const res = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
  })

  if (!res.ok) {
    const text = await res.text()
    let message = text
    try {
      message = JSON.parse(text).message || text
    } catch {
      // Resposta sem JSON: mantém o texto original.
    }
    throw new Error(message || `Erro ${res.status}`)
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

  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('espacoFinanceiroId')
  },

  isLoggedIn: () => !!getToken(),

  getActiveSpaceId,

  setActiveSpaceId: (id) => localStorage.setItem('espacoFinanceiroId', String(id)),

  espacos: () => request('/api/v1/espacos'),

  criarEspaco: (data) =>
    request('/api/v1/espacos', { method: 'POST', body: JSON.stringify(data) }),

  acessosEspaco: (id) => request(`/api/v1/espacos/${id}/acessos`),

  convidarGestora: (id, email) =>
    request(`/api/v1/espacos/${id}/convites`, { method: 'POST', body: JSON.stringify({ email }) }),

  removerAcessoEspaco: (id, usuarioId) =>
    request(`/api/v1/espacos/${id}/acessos/${usuarioId}`, { method: 'DELETE' }),

  aceitarConvite: (token) =>
    request('/api/v1/convites/aceitar', { method: 'POST', body: JSON.stringify({ token }) }),

  resumo: (inicio, fim) =>
    request(`/api/v1/dashboard/resumo?inicio=${inicio}&fim=${fim}`),

  lancamentos: () => request('/api/v1/lancamentos'),

  criarLancamento: (data) =>
    request('/api/v1/lancamentos', { method: 'POST', body: JSON.stringify(data) }),

  atualizarLancamento: (id, data) =>
    request(`/api/v1/lancamentos/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  excluirLancamento: (id) =>
    request(`/api/v1/lancamentos/${id}`, { method: 'DELETE' }),

  titulosFinanceiros: () => request('/api/v1/contas-previstas'),

  criarTituloFinanceiro: (data) =>
    request('/api/v1/contas-previstas', { method: 'POST', body: JSON.stringify(data) }),

  atualizarTituloFinanceiro: (id, data) =>
    request(`/api/v1/contas-previstas/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  liquidarTituloFinanceiro: (id, data) =>
    request(`/api/v1/contas-previstas/${id}/liquidar`, { method: 'POST', body: JSON.stringify(data) }),

  cancelarTituloFinanceiro: (id) =>
    request(`/api/v1/contas-previstas/${id}/cancelar`, { method: 'PATCH' }),

  contas: () => request('/api/v1/contas'),

  criarConta: (data) =>
    request('/api/v1/contas', { method: 'POST', body: JSON.stringify(data) }),

  atualizarConta: (id, data) =>
    request(`/api/v1/contas/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  desativarConta: (id) =>
    request(`/api/v1/contas/${id}/desativar`, { method: 'PATCH' }),

  categorias: () => request('/api/v1/categorias'),

  criarCategoria: (data) =>
    request('/api/v1/categorias', { method: 'POST', body: JSON.stringify(data) }),

  atualizarCategoria: (id, data) =>
    request(`/api/v1/categorias/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  desativarCategoria: (id) =>
    request(`/api/v1/categorias/${id}/desativar`, { method: 'PATCH' }),
}
