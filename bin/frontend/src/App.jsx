import { useState } from 'react'
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

const periods = ['Este mês', 'Últimos 30 dias', 'Este ano']

const transactions = [
  { title: 'Mercado Central', category: 'Alimentação', date: 'Hoje, 09:42', value: '- R$ 186,40', type: 'expense', color: 'coral' },
  { title: 'Freela · Projeto Atlas', category: 'Renda extra', date: 'Ontem, 16:20', value: '+ R$ 1.250,00', type: 'income', color: 'mint' },
  { title: 'Aluguel', category: 'Moradia', date: '18 set, 08:10', value: '- R$ 1.800,00', type: 'expense', color: 'blue' },
  { title: 'Café Aurora', category: 'Lazer', date: '17 set, 14:05', value: '- R$ 28,90', type: 'expense', color: 'yellow' },
]

const bars = [28, 42, 36, 58, 51, 68, 54, 76, 62, 86, 71, 91]

function App() {
  const [period, setPeriod] = useState(periods[0])
  const [activeNav, setActiveNav] = useState('Visão geral')

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
          <div className="avatar">GM</div>
          <div className="workspace-copy"><span>Meu espaço</span><strong>Gustavo Martins</strong></div>
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
          <button className="profile-row"><div className="avatar avatar-small">GM</div><span>Gustavo Martins</span><LogOut size={16} /></button>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div className="breadcrumb"><span>Workspace</span><b>/</b><strong>{activeNav}</strong></div>
          <div className="topbar-actions">
            <button className="icon-button" title="Ajuda"><CircleHelp size={18} /></button>
            <button className="icon-button notification" title="Notificações"><Bell size={18} /><i /></button>
            <div className="top-avatar">GM</div>
          </div>
        </header>

        <div className="content-wrap">
          <section className="welcome-row">
            <div>
              <p className="eyebrow">Domingo, 20 de setembro de 2026</p>
              <h1>Seu dinheiro em movimento.</h1>
              <p className="subtitle">Uma visão clara para cada decisão do seu dia.</p>
            </div>
            <button className="primary-button"><Plus size={18} /> Novo lançamento</button>
          </section>

          <section className="metric-grid">
            <MetricCard label="Saldo disponível" value="R$ 12.480,35" detail="+ 8,4% este mês" tone="green" icon={<WalletCards size={19} />} />
            <MetricCard label="Receitas" value="R$ 6.850,00" detail="Entradas no período" tone="blue" icon={<ArrowDownLeft size={19} />} />
            <MetricCard label="Despesas" value="R$ 3.214,55" detail="- 4,2% vs. mês anterior" tone="coral" icon={<ArrowUpRight size={19} />} />
          </section>

          <section className="main-grid">
            <div className="panel chart-panel">
              <div className="panel-heading">
                <div><p className="panel-kicker">Fluxo mensal</p><h2>Receitas x despesas</h2></div>
                <div className="period-select"><select value={period} onChange={(event) => setPeriod(event.target.value)}>{periods.map((item) => <option key={item}>{item}</option>)}</select><ChevronDown size={15} /></div>
              </div>
              <div className="chart-legend"><span><i className="legend-dot income-dot" /> Receitas</span><span><i className="legend-dot expense-dot" /> Despesas</span></div>
              <div className="chart-area">
                <div className="axis-labels"><span>8k</span><span>6k</span><span>4k</span><span>2k</span><span>0</span></div>
                <div className="bar-grid">{bars.map((height, index) => <div className="bar-group" key={index}><div className="bar-pair"><span className="bar income-bar" style={{ height: `${height}%` }} /><span className="bar expense-bar" style={{ height: `${Math.max(height - 28, 12)}%` }} /></div><small>{['out', 'nov', 'dez', 'jan', 'fev', 'mar', 'abr', 'mai', 'jun', 'jul', 'ago', 'set'][index]}</small></div>)}</div>
              </div>
            </div>

            <div className="panel account-panel">
              <div className="panel-heading"><div><p className="panel-kicker">Patrimônio</p><h2>Minhas contas</h2></div><button className="more-button" title="Mais opções"><MoreHorizontal size={19} /></button></div>
              <div className="account-total"><span>Saldo total</span><strong>R$ 18.920,35</strong></div>
              <AccountRow icon={<WalletCards size={17} />} color="sage" name="Nubank · Conta principal" balance="R$ 12.480,35" />
              <AccountRow icon={<CreditCard size={17} />} color="navy" name="Cartão pessoal" balance="R$ 4.290,00" negative />
              <AccountRow icon={<WalletCards size={17} />} color="ochre" name="Carteira" balance="R$ 2.150,00" />
              <button className="text-button"><Plus size={16} /> Adicionar conta</button>
            </div>
          </section>

          <section className="panel transactions-panel">
            <div className="panel-heading"><div><p className="panel-kicker">Movimentações recentes</p><h2>Últimos lançamentos</h2></div><div className="table-actions"><div className="search-box"><Search size={16} /><input placeholder="Buscar lançamento" /></div><button className="filter-button">Filtrar <ChevronDown size={14} /></button></div></div>
            <div className="transaction-list">{transactions.map((transaction) => <TransactionRow key={transaction.title} transaction={transaction} />)}</div>
            <button className="view-all">Ver todos os lançamentos <ArrowUpRight size={15} /></button>
          </section>
        </div>
      </main>
    </div>
  )
}

function NavItem({ icon, label, active, onClick }) {
  return <button className={`nav-item ${active ? 'active' : ''}`} onClick={onClick}>{icon}<span>{label}</span>{active && <i />}</button>
}

function MetricCard({ label, value, detail, tone, icon }) {
  return <article className="metric-card"><div className={`metric-icon ${tone}`}>{icon}</div><div className="metric-copy"><span>{label}</span><strong>{value}</strong><small className={tone === 'green' ? 'positive' : ''}>{detail}</small></div></article>
}

function AccountRow({ icon, color, name, balance, negative }) {
  return <div className="account-row"><div className={`account-icon ${color}`}>{icon}</div><div className="account-name"><strong>{name}</strong><span>{negative ? 'Fatura em aberto' : 'Disponível'}</span></div><b className={negative ? 'negative' : ''}>{balance}</b></div>
}

function TransactionRow({ transaction }) {
  return <div className="transaction-row"><div className={`transaction-icon ${transaction.color}`}>{transaction.type === 'income' ? <ArrowDownLeft size={17} /> : <ArrowUpRight size={17} />}</div><div className="transaction-name"><strong>{transaction.title}</strong><span>{transaction.category}</span></div><time>{transaction.date}</time><b className={transaction.type === 'income' ? 'income-text' : 'expense-text'}>{transaction.value}</b><button className="more-button" title="Mais opções"><MoreHorizontal size={18} /></button></div>
}

export default App
