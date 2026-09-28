import { useEffect, useRef, useState } from 'react'
import {
  ArrowDownLeft,
  ArrowUpRight,
  ChevronDown,
  LayoutDashboard,
  LogOut,
  Pencil,
  Plus,
  Search,
  Sparkles,
  Tags,
  Trash2,
  UsersRound,
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

function LoginScreen({ onLoggedIn, invitePending }) {
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
        {invitePending && <p className="form-hint">Entre ou crie sua conta usando o e-mail que recebeu o convite. Depois do login, o acesso ao negócio será ativado.</p>}

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
  const [categorias, setCategorias] = useState([])
  const [acessos, setAcessos] = useState([])
  const [espacos, setEspacos] = useState([])
  const [activeSpaceId, setActiveSpaceId] = useState(null)
  const [dialog, setDialog] = useState(null)
  const [query, setQuery] = useState('')
  const [reloadKey, setReloadKey] = useState(0)
  const [spacesReloadKey, setSpacesReloadKey] = useState(0)
  const [inviteEmail, setInviteEmail] = useState('')
  const [inviteLink, setInviteLink] = useState('')
  const [inviteNotice, setInviteNotice] = useState('')
  const [inviteCopied, setInviteCopied] = useState(false)
  const [pendingInvitationToken] = useState(() => {
    const queryToken = new URLSearchParams(window.location.search).get('convite')
    const hashToken = new URLSearchParams(window.location.hash.replace(/^#/, '')).get('convite')
    return hashToken || queryToken
  })
  const invitationHandled = useRef(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const activeSpace = espacos.find((space) => String(space.id) === activeSpaceId)
  const hasOwnedBusinessSpace = espacos.some((space) => space.tipo === 'NEGOCIO' && space.papel === 'PROPRIETARIO')

  useEffect(() => {
    if (!loggedIn) return

    let cancelled = false
    api.espacos()
      .then((items) => {
        if (cancelled) return
        const list = Array.isArray(items) ? items : []
        setEspacos(list)
        const savedId = api.getActiveSpaceId()
        const selected = list.find((space) => String(space.id) === savedId)
          || list.find((space) => space.tipo === 'PESSOAL')
          || list[0]
        if (selected) {
          api.setActiveSpaceId(selected.id)
          setActiveSpaceId(String(selected.id))
        } else {
          setError('Nenhum espaço financeiro está disponível para esta conta.')
        }
      })
      .catch((err) => { if (!cancelled) setError(err.message || 'Não foi possível carregar seus espaços.') })
    return () => { cancelled = true }
  }, [loggedIn, spacesReloadKey])

  useEffect(() => {
    if (!loggedIn || !pendingInvitationToken || invitationHandled.current) return
    invitationHandled.current = true
    api.aceitarConvite(pendingInvitationToken)
      .then((space) => {
        api.setActiveSpaceId(space.id)
        setActiveSpaceId(String(space.id))
        setSpacesReloadKey((key) => key + 1)
        setInviteNotice(`Convite aceito. Agora você tem acesso ao espaço “${space.nome}”.`)
        setError('')
      })
      .catch((err) => setError(err.message || 'Não foi possível aceitar o convite.'))
      .finally(() => {
        const url = new URL(window.location.href)
        url.searchParams.delete('convite')
        url.hash = ''
        window.history.replaceState({}, '', url)
      })
  }, [loggedIn, pendingInvitationToken])

  useEffect(() => {
    if (!loggedIn || !activeSpaceId) return

    let cancelled = false
    const { inicio, fim } = getMonthRange()
    setLoading(true)
    setError('')

    Promise.all([
      api.resumo(inicio, fim).catch(() => null),
      api.lancamentos().catch(() => []),
      api.contas().catch(() => []),
      api.categorias().catch(() => []),
      activeSpace?.tipo === 'NEGOCIO' ? api.acessosEspaco(activeSpaceId).catch(() => []) : Promise.resolve([]),
    ])
      .then(([r, l, c, cats, accessList]) => {
        if (cancelled) return
        setResumo(r)
        setLancamentos(Array.isArray(l) ? l : [])
        setContas(Array.isArray(c) ? c : [])
        setCategorias(Array.isArray(cats) ? cats : [])
        setAcessos(Array.isArray(accessList) ? accessList : [])
      })
      .catch((err) => { if (!cancelled) setError(err.message || 'Erro ao carregar dados') })
      .finally(() => { if (!cancelled) setLoading(false) })
    return () => { cancelled = true }
  }, [loggedIn, activeSpaceId, activeSpace?.tipo, reloadKey])

  if (!loggedIn) {
    return <LoginScreen onLoggedIn={() => setLoggedIn(true)} invitePending={Boolean(pendingInvitationToken)} />
  }

  const saldoContas = contas.reduce((acc, c) => acc + Number(c.saldo ?? c.saldoAtual ?? 0), 0)
  const lancamentosFiltrados = lancamentos.filter((item) =>
    `${item.descricao || ''} ${item.categoriaNome || item.categoria || ''} ${item.contaNome || ''}`
      .toLocaleLowerCase('pt-BR').includes(query.toLocaleLowerCase('pt-BR')),
  )

  async function salvarCadastro(data) {
    setLoading(true)
    setError('')
    try {
      if (dialog.type === 'transaction') {
        if (dialog.item) await api.atualizarLancamento(dialog.item.id, data)
        else await api.criarLancamento(data)
      } else if (dialog.type === 'account') {
        if (dialog.item) await api.atualizarConta(dialog.item.id, data)
        else await api.criarConta(data)
      } else if (dialog.type === 'category') {
        if (dialog.item) await api.atualizarCategoria(dialog.item.id, data)
        else await api.criarCategoria(data)
      } else if (dialog.type === 'space') {
        const createdSpace = await api.criarEspaco({ ...data, tipo: 'NEGOCIO' })
        api.setActiveSpaceId(createdSpace.id)
        setActiveSpaceId(String(createdSpace.id))
        setSpacesReloadKey((key) => key + 1)
      }
      setDialog(null)
      setReloadKey((key) => key + 1)
    } catch (err) {
      setError(err.message || 'Não foi possível salvar os dados.')
      throw err
    } finally {
      setLoading(false)
    }
  }

  async function excluirLancamento(id) {
    if (!window.confirm('Excluir este lançamento? Esta ação não pode ser desfeita.')) return
    setError('')
    try {
      await api.excluirLancamento(id)
      setReloadKey((key) => key + 1)
    } catch (err) {
      setError(err.message || 'Não foi possível excluir o lançamento.')
    }
  }

  async function desativarRegistro(type, item) {
    const label = type === 'account' ? 'conta' : 'categoria'
    if (!window.confirm(`Desativar ${label} “${item.nome}”?`)) return
    try {
      if (type === 'account') await api.desativarConta(item.id)
      else await api.desativarCategoria(item.id)
      setReloadKey((key) => key + 1)
    } catch (err) {
      setError(err.message || `Não foi possível desativar ${label}.`)
    }
  }

  function trocarEspaco(event) {
    const id = event.target.value
    api.setActiveSpaceId(id)
    setActiveSpaceId(id)
    setInviteLink('')
    setInviteNotice('')
  }

  async function enviarConvite(event) {
    event.preventDefault()
    if (!activeSpace) return
    setError('')
    try {
      const invite = await api.convidarGestora(activeSpace.id, inviteEmail)
      const inviteUrl = new URL(window.location.origin)
      inviteUrl.hash = new URLSearchParams({ convite: invite.token }).toString()
      setInviteLink(inviteUrl.toString())
      setInviteNotice(`Convite criado para ${invite.emailConvidado}. Ele vale por 7 dias.`)
      setInviteEmail('')
      setReloadKey((key) => key + 1)
    } catch (err) {
      setError(err.message || 'Não foi possível criar o convite.')
    }
  }

  async function copiarConvite() {
    try {
      await navigator.clipboard.writeText(inviteLink)
      setInviteCopied(true)
    } catch {
      setError('Selecione e copie o link do convite manualmente.')
    }
  }

  async function removerAcesso(usuarioId, nome) {
    if (!window.confirm(`Remover o acesso de ${nome} a este negócio?`)) return
    try {
      await api.removerAcessoEspaco(activeSpace.id, usuarioId)
      setReloadKey((key) => key + 1)
      setInviteNotice(`O acesso de ${nome} foi removido.`)
    } catch (err) {
      setError(err.message || 'Não foi possível remover o acesso.')
    }
  }

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

        <label className="workspace-switcher">
          <div className="avatar">{activeSpace?.tipo === 'NEGOCIO' ? 'N' : 'P'}</div>
          <div className="workspace-copy">
            <span>Espaço financeiro</span>
            <select aria-label="Selecionar espaço financeiro" value={activeSpaceId || ''} onChange={trocarEspaco}>
              {espacos.map((space) => <option key={space.id} value={space.id}>{space.tipo === 'PESSOAL' ? 'Pessoal' : 'Negócio'} · {space.nome}</option>)}
            </select>
          </div>
          <ChevronDown size={16} />
        </label>

        <nav className="main-nav" aria-label="Navegação principal">
          <p className="nav-label">Workspace</p>
          <NavItem icon={<LayoutDashboard size={18} />} label="Visão geral" active={activeNav === 'Visão geral'} onClick={() => setActiveNav('Visão geral')} />
          <NavItem icon={<ArrowUpRight size={18} />} label="Lançamentos" active={activeNav === 'Lançamentos'} onClick={() => setActiveNav('Lançamentos')} />
          <NavItem icon={<WalletCards size={18} />} label="Contas" active={activeNav === 'Contas'} onClick={() => setActiveNav('Contas')} />
          <NavItem icon={<Tags size={18} />} label="Categorias" active={activeNav === 'Categorias'} onClick={() => setActiveNav('Categorias')} />
          {activeSpace?.tipo === 'NEGOCIO' && <NavItem icon={<UsersRound size={18} />} label="Gestora financeira" active={activeNav === 'Gestora financeira'} onClick={() => setActiveNav('Gestora financeira')} />}
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
              setEspacos([])
              setActiveSpaceId(null)
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
            <div className="welcome-actions">
              {!hasBusinessSpace && <button className="secondary-button" onClick={() => setDialog({ type: 'space' })}><Plus size={16} /> Criar espaço do negócio</button>}
              {(activeNav === 'Visão geral' || activeNav === 'Lançamentos') && <button className="primary-button" onClick={() => { setError(''); setDialog({ type: 'transaction' }) }} disabled={contas.every((conta) => !conta.ativo)} title={contas.every((conta) => !conta.ativo) ? 'Cadastre uma conta primeiro' : undefined}><Plus size={18} /> Novo lançamento</button>}
            </div>
          </section>

          {error && <p role="alert" className="app-error">{error}</p>}

          <section className="metric-grid" hidden={activeNav !== 'Visão geral'}>
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

          <section className="main-grid" hidden={activeNav !== 'Visão geral'}>
            <div className="panel chart-panel">
              <div className="panel-heading">
                <div>
                  <p className="panel-kicker">API</p>
                  <h2>Status da conexão</h2>
                </div>
              </div>
              <div style={{ padding: 8, lineHeight: 1.6 }}>
                <p><strong>API:</strong> {import.meta.env.VITE_API_URL || window.location.origin}</p>
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
                <button className="secondary-button" onClick={() => { setError(''); setDialog({ type: 'account' }) }}><Plus size={15} /> Nova conta</button>
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
                  onEdit={() => setDialog({ type: 'account', item: conta })}
                  onDeactivate={() => desativarRegistro('account', conta)}
                />
              ))}
            </div>
          </section>

          <section className="panel management-panel" hidden={activeNav !== 'Contas'}>
            <div className="panel-heading"><div><p className="panel-kicker">Patrimônio</p><h2>Gerenciar contas</h2></div><button className="secondary-button" onClick={() => setDialog({ type: 'account' })}><Plus size={15} /> Nova conta</button></div>
            <div className="management-list">
              {contas.map((conta) => <AccountRow key={conta.id} icon={<WalletCards size={17} />} color="sage" name={conta.nome} balance={formatMoney(conta.saldoAtual)} inactive={!conta.ativo} onEdit={() => setDialog({ type: 'account', item: conta })} onDeactivate={() => desativarRegistro('account', conta)} />)}
              {contas.length === 0 && <p className="empty-state">Você ainda não cadastrou contas.</p>}
            </div>
          </section>

          <section className="panel management-panel" hidden={activeNav !== 'Categorias'}>
            <div className="panel-heading"><div><p className="panel-kicker">Organização</p><h2>Gerenciar categorias</h2></div><button className="secondary-button" onClick={() => setDialog({ type: 'category' })}><Plus size={15} /> Nova categoria</button></div>
            <div className="management-list">
              {categorias.map((categoria) => <div className={`management-row ${categoria.ativa ? '' : 'is-inactive'}`} key={categoria.id}><div className="account-icon sage"><Tags size={17} /></div><div className="account-name"><strong>{categoria.nome}</strong><span>{categoria.ativa ? 'Ativa' : 'Inativa'}</span></div><div className="row-actions"><button className="more-button" title="Editar categoria" aria-label={`Editar ${categoria.nome}`} onClick={() => setDialog({ type: 'category', item: categoria })}><Pencil size={15} /></button>{categoria.ativa && <button className="more-button danger-action" title="Desativar categoria" aria-label={`Desativar ${categoria.nome}`} onClick={() => desativarRegistro('category', categoria)}><Trash2 size={15} /></button>}</div></div>)}
              {categorias.length === 0 && <p className="empty-state">Você ainda não cadastrou categorias.</p>}
            </div>
          </section>

          <section className="panel management-panel" hidden={activeNav !== 'Gestora financeira'}>
            <div className="panel-heading"><div><p className="panel-kicker">Acesso compartilhado</p><h2>Gestora financeira</h2></div></div>
            <p className="management-description">O cliente e a gestora trabalham sobre as mesmas contas e movimentações. A gestora não recebe acesso às finanças pessoais do cliente.</p>
            {activeSpace?.papel === 'PROPRIETARIO' && <form className="invite-form" onSubmit={enviarConvite}><label>E-mail da contadora ou gestora<input type="email" required maxLength="180" value={inviteEmail} onChange={(event) => setInviteEmail(event.target.value)} placeholder="contadora@exemplo.com" /></label><button className="primary-button" type="submit"><Plus size={16} /> Criar convite</button></form>}
            {inviteNotice && <p className="invite-notice" role="status">{inviteNotice}</p>}
            {inviteLink && <div className="invite-link-box"><label>Link para compartilhar<input readOnly value={inviteLink} onFocus={(event) => event.target.select()} /></label><button className="secondary-button" onClick={copiarConvite}>{inviteCopied ? 'Copiado' : 'Copiar link'}</button></div>}
            <h3 className="access-list-title">Pessoas com acesso</h3>
            <div className="management-list">
              {acessos.map((access) => <div className="management-row" key={access.usuarioId}><div className="avatar access-avatar">{access.nome?.slice(0, 1)?.toUpperCase() || '?'}</div><div className="account-name"><strong>{access.nome}</strong><span>{access.email} · {access.papel === 'PROPRIETARIO' ? 'Proprietário' : 'Gestora financeira'}</span></div>{activeSpace?.papel === 'PROPRIETARIO' && access.papel === 'GESTORA_FINANCEIRA' && <button className="secondary-button revoke-button" onClick={() => removerAcesso(access.usuarioId, access.nome)}>Remover acesso</button>}</div>)}
              {acessos.length === 0 && <p className="empty-state">Nenhuma pessoa adicional tem acesso a este negócio.</p>}
            </div>
          </section>

          <section className="panel transactions-panel" hidden={activeNav !== 'Visão geral' && activeNav !== 'Lançamentos'}>
            <div className="panel-heading">
              <div>
                <p className="panel-kicker">Movimentações</p>
                <h2>Últimos lançamentos</h2>
              </div>
              <div className="table-actions">
                <div className="search-box"><Search size={16} /><input placeholder="Buscar lançamento" value={query} onChange={(event) => setQuery(event.target.value)} /></div>
              </div>
            </div>

            <div className="transaction-list">
              {lancamentosFiltrados.length === 0 && (
                <p style={{ opacity: 0.7, padding: 12 }}>Nenhum lançamento ainda.</p>
              )}
              {lancamentosFiltrados.slice(0, activeNav === 'Lançamentos' ? lancamentosFiltrados.length : 10).map((item) => {
                const isIncome = (item.tipo || '').toUpperCase() === 'RECEITA'
                return (
                  <TransactionRow
                    key={item.id}
                    onEdit={() => setDialog({ type: 'transaction', item })}
                    onDelete={() => excluirLancamento(item.id)}
                    transaction={{
                      title: item.descricao || `Lançamento #${item.id}`,
                      category: [item.categoriaNome || item.categoria || item.tipo || '—', item.contaNome].filter(Boolean).join(' · '),
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
      {dialog && <CadastroDialog key={`${dialog.type}-${dialog.item?.id || 'new'}`} type={dialog.type} item={dialog.item} contas={contas} categorias={categorias} onClose={() => setDialog(null)} onSave={salvarCadastro} loading={loading} />}
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

function AccountRow({ icon, color, name, balance, negative, inactive, onEdit, onDeactivate }) {
  return (
    <div className={`account-row ${inactive ? 'is-inactive' : ''}`}>
      <div className={`account-icon ${color}`}>{icon}</div>
      <div className="account-name">
        <strong>{name}</strong>
        <span>{inactive ? 'Inativa' : negative ? 'Fatura em aberto' : 'Disponível'}</span>
      </div>
      <b className={negative ? 'negative' : ''}>{balance}</b>
      {onEdit && <div className="row-actions"><button className="more-button" title="Editar conta" aria-label={`Editar ${name}`} onClick={onEdit}><Pencil size={15} /></button>{onDeactivate && !inactive && <button className="more-button danger-action" title="Desativar conta" aria-label={`Desativar ${name}`} onClick={onDeactivate}><Trash2 size={15} /></button>}</div>}
    </div>
  )
}

function TransactionRow({ transaction, onEdit, onDelete }) {
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
      <div className="row-actions"><button className="more-button" title="Editar lançamento" aria-label={`Editar ${transaction.title}`} onClick={onEdit}><Pencil size={15} /></button><button className="more-button danger-action" title="Excluir lançamento" aria-label={`Excluir ${transaction.title}`} onClick={onDelete}><Trash2 size={15} /></button></div>
    </div>
  )
}

function CadastroDialog({ type, item, contas, categorias, onClose, onSave, loading }) {
  const isTransaction = type === 'transaction'
  const isAccount = type === 'account'
  const isSpace = type === 'space'
  const [form, setForm] = useState({
    descricao: item?.descricao || '', valor: item?.valor ?? '', tipo: item?.tipo || 'DESPESA', data: item?.data || new Date().toLocaleDateString('sv-SE'),
    categoria: item?.categoriaNome || item?.categoria || '', contaId: item?.contaId || contas.find((conta) => conta.ativo)?.id || '', observacao: item?.observacao || '', nome: item?.nome || '',
    tipoConta: item?.tipo || 'CONTA_CORRENTE', saldoInicial: item?.saldoInicial ?? '0', cnpj: '',
  })
  const [formError, setFormError] = useState('')

  function update(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  }

  async function submit(event) {
    event.preventDefault()
    setFormError('')
    try {
      if (isTransaction) {
        const selectedCategory = categorias.find((category) => category.nome.toLocaleLowerCase('pt-BR') === form.categoria.trim().toLocaleLowerCase('pt-BR'))
        await onSave({
          descricao: form.descricao.trim(), valor: Number(form.valor), tipo: form.tipo,
          data: form.data, categoria: form.categoria.trim(), categoriaId: selectedCategory?.id || null, contaId: Number(form.contaId),
          observacao: form.observacao.trim() || null,
        })
      } else if (isAccount) {
        await onSave({ nome: form.nome.trim(), tipo: form.tipoConta, saldoInicial: Number(form.saldoInicial) })
      } else if (isSpace) {
        await onSave({ nome: form.nome.trim(), cnpj: form.cnpj.trim() || null })
      } else {
        await onSave({ nome: form.nome.trim() })
      }
    } catch (error) {
      setFormError(error.message || 'Não foi possível salvar.')
    }
  }

  return (
    <div className="dialog-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose() }}>
      <section className="dialog-card" role="dialog" aria-modal="true" aria-labelledby="dialog-title">
        <div className="dialog-heading">
          <div><p className="panel-kicker">DinDin de giro</p><h2 id="dialog-title">{isTransaction ? item ? 'Editar lançamento' : 'Novo lançamento' : isAccount ? item ? 'Editar conta' : 'Nova conta' : isSpace ? 'Criar espaço do negócio' : item ? 'Editar categoria' : 'Nova categoria'}</h2></div>
          <button type="button" className="dialog-close" onClick={onClose} aria-label="Fechar">×</button>
        </div>
        <form className="dialog-form" onSubmit={submit}>
          {isTransaction ? <>
            <label>Descrição<input name="descricao" required maxLength="120" value={form.descricao} onChange={update} autoFocus /></label>
            <div className="form-columns">
              <label>Valor (R$)<input name="valor" type="number" required min="0.01" step="0.01" value={form.valor} onChange={update} /></label>
              <label>Tipo<select name="tipo" value={form.tipo} onChange={update}><option value="DESPESA">Despesa</option><option value="RECEITA">Receita</option></select></label>
            </div>
            <div className="form-columns">
              <label>Data<input name="data" type="date" required value={form.data} onChange={update} /></label>
              <label>Conta<select name="contaId" required value={form.contaId} onChange={update}><option value="">Selecione</option>{contas.filter((conta) => conta.ativo).map((conta) => <option key={conta.id} value={conta.id}>{conta.nome}</option>)}</select></label>
            </div>
            <label>Categoria<input name="categoria" list="categorias-disponiveis" required maxLength="60" value={form.categoria} onChange={update} placeholder="Ex.: Alimentação" /><datalist id="categorias-disponiveis">{categorias.filter((categoria) => categoria.ativa).map((categoria) => <option key={categoria.id} value={categoria.nome} />)}</datalist></label>
            <label>Observação (opcional)<textarea name="observacao" maxLength="500" rows="3" value={form.observacao} onChange={update} /></label>
          </> : isAccount ? <>
            <label>Nome da conta<input name="nome" required maxLength="100" value={form.nome} onChange={update} autoFocus /></label>
            <label>Tipo<select name="tipoConta" value={form.tipoConta} onChange={update}><option value="CONTA_CORRENTE">Conta corrente</option><option value="POUPANCA">Poupança</option><option value="CARTEIRA">Carteira</option><option value="CARTAO_CREDITO">Cartão de crédito</option><option value="OUTRA">Outra</option></select></label>
            <label>Saldo inicial (R$)<input name="saldoInicial" type="number" min="0" step="0.01" required value={form.saldoInicial} onChange={update} /></label>
          </> : isSpace ? <>
            <p className="form-hint">Use este espaço para separar o dinheiro do seu negócio ou atividade profissional das suas finanças pessoais. Não é necessário ter CNPJ.</p>
            <label>Nome do negócio ou atividade<input name="nome" required maxLength="100" value={form.nome} onChange={update} autoFocus placeholder="Ex.: Doces da Ana" /></label>
            <label>CNPJ (opcional)<input name="cnpj" maxLength="18" value={form.cnpj} onChange={update} placeholder="00.000.000/0000-00" inputMode="numeric" /></label>
          </> : <>
            <label>Nome da categoria<input name="nome" required maxLength="60" value={form.nome} onChange={update} autoFocus /></label>
          </>}
          {formError && <p role="alert" className="app-error">{formError}</p>}
          <div className="dialog-actions"><button className="secondary-button" type="button" onClick={onClose}>Cancelar</button><button className="primary-button" type="submit" disabled={loading}>{loading ? 'Salvando...' : 'Salvar'}</button></div>
        </form>
      </section>
    </div>
  )
}

export default App
