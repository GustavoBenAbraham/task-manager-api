import { useEffect, useState } from 'react'
import {
  ArrowDownLeft,
  ArrowUpRight,
  Bell,
  ChevronDown,
  CircleHelp,
  CreditCard,
  LayoutDashboard,
  LogOut,
  MoreHorizontal,
  Plus,
  Search,
  Settings2,
  Sparkles,
  Tags,
  WalletCards,
} from 'lucide-react'
import { api } from './api'

function formatMoney(value) {
  if (value == null) return 'R$ 0,00'
  return Number(value).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  })
}

function getMonthRange() {
  const now = new Date()
  const inicio = new Date(now.getFullYear(), now.getMonth(), 1)
  const fim = new Date(now.getFullYear(), now.getMonth() + 1, 0)
  const toIso = (d) => d.toISOString().slice(0, 10)
  return { inicio: toIso(inicio), fim: toIso(fim) }
}

function LoginScreen({ onLoggedIn }) {
  const [mode, setMode] = useState('login') // login | register
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      if (mode === 'register') {
        await api.register({ nome, email, senha })
        await api.login(email, senha)
      } else {
        await api.login(email, senha)
      }
      onLoggedIn()
    } catch (err) {
      setError(err.message || 'Falha na autenticação')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app-shell" style={{ placeItems: 'center', display: 'grid' }}>
      <form
        onSubmit={handleSubmit}
        style={{
          width: 'min(420px, 92vw)',
          background: '#fff',
          borderRadius: 16,
          padding: 28,
          boxShadow: '0 12px 40px rgba(0,0,0,.08)',
          display: 'grid',
          gap: 12,
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
          <div className="brand-mark"><Sparkles size={18} strokeWidth={2.5} /></div>
          <div>
            <strong>DinDin</strong>
            <div style={{ fontSize: 13, opacity: 0.7 }}>de giro</div>
          </div>
        </div>

        <h1 style={{ margin: 0, fontSize: 22 }}>
          {mode === 'login' ? 'Entrar' : 'Criar conta'}
        </h1>

        {mode === 'register' && (
          <input
            required
            placeholder="Seu nome"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            style={inputStyle}
          />
        )}

        <input
          required
          type="email"
          placeholder="E-mail"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          style={inputStyle}
        />

        <input
          required
          type="password"
          minLength={8}
          placeholder="Senha (mín. 8 caracteres)"
          value={senha}
          onChange={(e) => setSenha(e.target.value)}
          style={inputStyle}
        />

        {error && (
          <p style={{ color: '#c0392b', margin: 0, fontSize: 14 }}>{error}</p>
        )}

        <button className="primary-button" disabled={loading} type="submit">
          {loading ? 'Aguarde...' : mode === 'login' ? 'Entrar' : 'Cadastrar'}
        </button>

        <button
          type="button"
          className="text-button"
          onClick={() => {
            setMode(mode === 'login' ? 'register' : 'login')
            setError('')
          }}
        >
          {mode === 'login' ? 'Criar uma conta' : 'Já tenho conta'}
        </button>
      </form>
    </div>
  )
}

const inputStyle = {
  border: '1px solid #e5e7eb',
  borderRadius: 10,
  padding: '12px 14px',
  fontSize: 15,
}

function App() {
  const [loggedIn, setLoggedIn] = useState(api.isLoggedIn())
  const [activeNav, setActiveNav] = useState('Visão geral')
  const [resumo, setResumo] = useState(null)
  const [lancamentos, setLancamentos] = useState([])
  const [contas, setContas] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!loggedIn) return

    const { inicio, fim } = getMonthRange()
    setLoading(true)
    setError('')

    Promise.all([
      api.resumo(inicio, fim).catch(() => null),
      api.lancamentos().catch(() => []),
      api.contas().catch(() => []),
    ])
      .then(([r, l, c]) => {
        setResumo(r)
        setLancamentos(Array.isArray(l) ? l : [])
        setContas(Array.isArray(c) ? c : [])
      })
      .catch((err) => setError(err.message || 'Erro ao carregar dados'))
      .finally(() => setLoading(false))
  }, [loggedIn])

  if (!loggedIn) {
    return <LoginScreen onLoggedIn={() => setLoggedIn(true)} />
  }

  const saldoContas = contas.reduce((acc, c) => acc + Number(c.saldo ?? c.saldoAtual ?? 0), 0)

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-lockup">
          <div className="brand-mark"><Sparkles size={18} strokeWidth={2.5} /></div>
          <div>
            <strong>DinDin</strong>
            <span>de giro</span>
          </div>
        </div>

        <div className="workspace-switcher">
          <div className="avatar">EU</div>
          <div className="workspace-copy">
            <span>Meu espaço</span>
            <strong>Conta logada</strong>
          </div>
          <ChevronDown size={16} />
        </div>

        <nav className="main-nav" aria-label="Navegação principal">
          <p className="nav-label">Workspace</p>
          <NavItem icon={<LayoutDashboard size={18} />} label="Visão geral" active={activeNav === 'Visão geral'} onClick={() => setActiveNav('Visão geral')} />
          <NavItem icon={<ArrowUpRight size={18} />} label="Lançamentos" active={activeNav === 'Lançamentos'} onClick={() => setActiveNav('Lançamentos')} />
          <NavItem icon={<WalletCards size={18} />} label="Contas" active={activeNav === 'Contas'} onClick={() => setActiveNav('Contas')} />
          <NavItem icon={<Tags size={18} />} label="Categorias" active={activeNav === 'Categorias'} onClick={() => setActiveNav('Categorias')} />
          <p className="nav-label second-label">Preferências</p>
          <NavItem icon={<Settings2 size={18} />} label="Configurações" active={activeNav === 'Configurações'} onClick={() => setActiveNav('Configurações')} />
        </nav>

        <div className="sidebar-bottom">
          <div className="pro-card">
            <div className="pro-icon"><Sparkles size={16} /></div>
            <div><strong>Plano essencial</strong><span>Seu controle, sem ruído.</span></div>
          </div>
          <button
            className="profile-row"
            onClick={() => {
              api.logout()
              setLoggedIn(false)
            }}
          >
            <div className="avatar avatar-small">EU</div>
            <span>Sair</span>
            <LogOut size={16} />
          </button>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div className="breadcrumb"><span>Workspace</span><b>/</b><strong>{activeNav}</strong></div>
          <div className="topbar-actions">
            <button className="icon-button" title="Ajuda"><CircleHelp size={18} /></button>
            <button className="icon-button notification" title="Notificações"><Bell size={18} /><i /></button>
            <div className="top-avatar">EU</div>
          </div>
        </header>

        <div className="content-wrap">
          <section className="welcome-row">
            <div>
              <p className="eyebrow">Dashboard ao vivo</p>
              <h1>Seu dinheiro em movimento.</h1>
              <p className="subtitle">
                {loading ? 'Carregando dados da API...' : error ? error : 'Dados vindos da API no Render.'}
              </p>
            </div>
            <button className="primary-button"><Plus size={18} /> Novo lançamento</button>
          </section>

          <section className="metric-grid">
            <MetricCard
              label="Saldo disponível"
              value={formatMoney(resumo?.saldo ?? saldoContas)}
              detail="Resumo do mês"
              tone="green"
              icon={<WalletCards size={19} />}
            />
            <MetricCard
              label="Receitas"
              value={formatMoney(resumo?.totalReceitas)}
              detail="Entradas no período"
              tone="blue"
              icon={<ArrowDownLeft size={19} />}
            />
            <MetricCard
              label="Despesas"
              value={formatMoney(resumo?.totalDespesas)}
              detail="Saídas no período"
              tone="coral"
              icon={<ArrowUpRight size={19} />}
            />
          </section>

          <section className="main-grid">
            <div className="panel chart-panel">
              <div className="panel-heading">
                <div>
                  <p className="panel-kicker">API</p>
                  <h2>Status da conexão</h2>
                </div>
              </div>
              <div style={{ padding: 8, lineHeight: 1.6 }}>
                <p><strong>API:</strong> {import.meta.env.VITE_API_URL || 'https://task-manager-api-wmx2.onrender.com'}</p>
                <p><strong>Lançamentos:</strong> {lancamentos.length}</p>
                <p><strong>Contas:</strong> {contas.length}</p>
              </div>
            </div>

            <div className="panel account-panel">
              <div className="panel-heading">
                <div>
                  <p className="panel-kicker">Patrimônio</p>
                  <h2>Minhas contas</h2>
                </div>
              </div>
              <div className="account-total">
                <span>Saldo total</span>
                <strong>{formatMoney(saldoContas)}</strong>
              </div>
              {contas.length === 0 && <p style={{ opacity: 0.7 }}>Nenhuma conta ainda.</p>}
              {contas.map((conta) => (
                <AccountRow
                  key={conta.id}
                  icon={<WalletCards size={17} />}
                  color="sage"
                  name={conta.nome || conta.descricao || `Conta #${conta.id}`}
                  balance={formatMoney(conta.saldo ?? conta.saldoAtual ?? 0)}
                />
              ))}
            </div>
          </section>

          <section className="panel transactions-panel">
            <div className="panel-heading">
              <div>
                <p className="panel-kicker">Movimentações</p>
                <h2>Últimos lançamentos</h2>
              </div>
              <div className="table-actions">
                <div className="search-box"><Search size={16} /><input placeholder="Buscar lançamento" /></div>
              </div>
            </div>

            <div className="transaction-list">
              {lancamentos.length === 0 && (
                <p style={{ opacity: 0.7, padding: 12 }}>Nenhum lançamento ainda.</p>
              )}
              {lancamentos.slice(0, 10).map((item) => {
                const isIncome = (item.tipo || '').toUpperCase() === 'RECEITA'
                return (
                  <TransactionRow
                    key={item.id}
                    transaction={{
                      title: item.descricao || `Lançamento #${item.id}`,
                      category: item.categoria || item.tipo || '—',
                      date: item.data || '',
                      value: `${isIncome ? '+' : '-'} ${formatMoney(item.valor)}`,
                      type: isIncome ? 'income' : 'expense',
                      color: isIncome ? 'mint' : 'coral',
                    }}
                  />
                )
              })}
            </div>
          </section>
        </div>
      </main>
    </div>
  )
}

function NavItem({ icon, label, active, onClick }) {
  return (
    <button className={`nav-item ${active ? 'active' : ''}`} onClick={onClick}>
      {icon}<span>{label}</span>{active && <i />}
    </button>
  )
}

function MetricCard({ label, value, detail, tone, icon }) {
  return (
    <article className="metric-card">
      <div className={`metric-icon ${tone}`}>{icon}</div>
      <div className="metric-copy">
        <span>{label}</span>
        <strong>{value}</strong>
        <small className={tone === 'green' ? 'positive' : ''}>{detail}</small>
      </div>
    </article>
  )
}

function AccountRow({ icon, color, name, balance, negative }) {
  return (
    <div className="account-row">
      <div className={`account-icon ${color}`}>{icon}</div>
      <div className="account-name">
        <strong>{name}</strong>
        <span>{negative ? 'Fatura em aberto' : 'Disponível'}</span>
      </div>
      <b className={negative ? 'negative' : ''}>{balance}</b>
    </div>
  )
}

function TransactionRow({ transaction }) {
  return (
    <div className="transaction-row">
      <div className={`transaction-icon ${transaction.color}`}>
        {transaction.type === 'income' ? <ArrowDownLeft size={17} /> : <ArrowUpRight size={17} />}
      </div>
      <div className="transaction-name">
        <strong>{transaction.title}</strong>
        <span>{transaction.category}</span>
      </div>
      <time>{transaction.date}</time>
      <b className={transaction.type === 'income' ? 'income-text' : 'expense-text'}>{transaction.value}</b>
      <button className="more-button" title="Mais opções"><MoreHorizontal size={18} /></button>
    </div>
  )
}

export default App