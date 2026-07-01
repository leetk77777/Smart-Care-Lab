import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  Activity,
  AlertTriangle,
  Bell,
  DoorOpen,
  FileText,
  HeartPulse,
  Home,
  PlayCircle,
  Radar,
  Send,
  ShieldCheck,
  Thermometer,
  UserPlus,
  UserRound,
} from 'lucide-react';
import { Area, AreaChart, CartesianGrid, Line, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import './styles.css';

const API_BASE = import.meta.env.VITE_API_BASE || '/api';
const statusMeta = {
  NORMAL: { label: '정상', className: 'normal', color: '#16a34a' },
  CAUTION: { label: '주의', className: 'caution', color: '#d97706' },
  DANGER: { label: '위험', className: 'danger', color: '#dc2626' },
};

function formatTime(value) {
  if (!value) return '-';
  return new Intl.DateTimeFormat('ko-KR', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value));
}

function nowLocalInput() {
  const date = new Date();
  date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
  return date.toISOString().slice(0, 16);
}

function App() {
  const [activePage, setActivePage] = useState('dashboard');
  const [dashboard, setDashboard] = useState(null);
  const [seniors, setSeniors] = useState([]);
  const [selectedSeniorId, setSelectedSeniorId] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const loadSeniors = async () => {
    const response = await fetch(`${API_BASE}/seniors`);
    if (!response.ok) throw new Error('독거노인 목록을 불러오지 못했습니다.');
    const data = await response.json();
    setSeniors(data);
    if (data.length > 0 && !data.some((senior) => senior.id === Number(selectedSeniorId))) {
      setSelectedSeniorId(data[0].id);
    }
    return data;
  };

  const loadDashboard = async (seniorId = selectedSeniorId) => {
    try {
      const response = await fetch(`${API_BASE}/dashboard/${seniorId}`);
      if (!response.ok) throw new Error('대시보드 데이터를 불러오지 못했습니다.');
      setDashboard(await response.json());
      setError('');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const refreshAll = async () => {
    try {
      await loadSeniors();
      await loadDashboard(selectedSeniorId);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  const runDemo = async (scenario) => {
    setNotice('');
    await fetch(`${API_BASE}/demo/${selectedSeniorId}/${scenario}`, { method: 'POST' });
    await loadDashboard(selectedSeniorId);
    setNotice(`${scenarioLabel(scenario)} 시나리오가 대시보드에 반영되었습니다.`);
    setActivePage('dashboard');
  };

  useEffect(() => {
    refreshAll();
    const timer = setInterval(() => loadDashboard(selectedSeniorId), 5000);
    return () => clearInterval(timer);
  }, [selectedSeniorId]);

  const risk = dashboard?.risk ?? { score: 0, status: 'NORMAL', reasons: [] };
  const meta = statusMeta[risk.status] ?? statusMeta.NORMAL;
  const latest = dashboard?.latestSensor;
  const chartData = useMemo(() => dashboard?.activityChart ?? [], [dashboard]);

  if (loading) {
    return <main className="center">대시보드를 준비하고 있습니다.</main>;
  }

  return (
    <main className="app">
      <header className="top-nav">
        <div>
          <strong>Senior Safety AI</strong>
          <span>비영상 생활패턴 안전 모니터링</span>
        </div>
        <nav>
          <button className={activePage === 'dashboard' ? 'active' : ''} onClick={() => setActivePage('dashboard')}>대시보드</button>
          <button className={activePage === 'seniors' ? 'active' : ''} onClick={() => setActivePage('seniors')}>대상자 등록</button>
          <button className={activePage === 'simulator' ? 'active' : ''} onClick={() => setActivePage('simulator')}>센서 시뮬레이터</button>
          <button className={activePage === 'scenario' ? 'active' : ''} onClick={() => setActivePage('scenario')}>발표 시나리오</button>
        </nav>
      </header>

      <SeniorSelector seniors={seniors} selectedSeniorId={selectedSeniorId} onChange={setSelectedSeniorId} />

      {error && <div className="error">{error}</div>}
      {notice && <div className="notice">{notice}</div>}

      {activePage === 'dashboard' && (
        <DashboardPage
          dashboard={dashboard}
          risk={risk}
          meta={meta}
          latest={latest}
          chartData={chartData}
          runDemo={runDemo}
        />
      )}
      {activePage === 'seniors' && (
        <SeniorPage
          seniors={seniors}
          onCreated={async () => {
            setNotice('대상자가 등록되었습니다.');
            await refreshAll();
          }}
        />
      )}
      {activePage === 'simulator' && (
        <SimulatorPage
          seniorId={selectedSeniorId}
          onSent={async () => {
            setNotice('가상 센서 이벤트가 저장되고 위험도 분석이 실행되었습니다.');
            await loadDashboard(selectedSeniorId);
            setActivePage('dashboard');
          }}
        />
      )}
      {activePage === 'scenario' && <ScenarioPage runDemo={runDemo} />}
    </main>
  );
}

function SeniorSelector({ seniors, selectedSeniorId, onChange }) {
  return (
    <section className="selector-bar">
      <span>모니터링 대상</span>
      <select value={selectedSeniorId} onChange={(event) => onChange(Number(event.target.value))}>
        {seniors.map((senior) => (
          <option key={senior.id} value={senior.id}>
            {senior.name} / {senior.age}세 / 보호자 {senior.guardianName}
          </option>
        ))}
      </select>
    </section>
  );
}

function DashboardPage({ dashboard, risk, meta, latest, chartData, runDemo }) {
  return (
    <>
      <section className={`hero ${meta.className}`}>
        <div>
          <p className="eyebrow">카메라 없는 프라이버시 보호형 생활패턴 모니터링</p>
          <h1>AI 기반 독거노인 안전 모니터링</h1>
          <p className="hero-copy">
            움직임, 문 열림, 온습도, 조도만으로 위험 징후를 분석해 고독사 예방과 조기 안부 확인을 돕습니다.
          </p>
        </div>
        <div className="risk-panel">
          <span className={`status-pill ${meta.className}`}>{meta.label}</span>
          <strong>{risk.score}</strong>
          <span>위험 점수 / 100</span>
        </div>
      </section>

      <section className="demo-bar">
        <div>
          <strong>심사용 데모 시나리오</strong>
          <span>정상 → 주의 → 위험 상태 변화를 즉시 시연합니다.</span>
        </div>
        <button onClick={() => runDemo('normal')}>정상 데모</button>
        <button onClick={() => runDemo('caution')}>주의 데모</button>
        <button className="danger-btn" onClick={() => runDemo('danger')}>위험 데모</button>
      </section>

      <section className="grid overview">
        <InfoCard icon={<UserRound />} label="대상자" value={dashboard?.senior?.name ?? '-'} sub={`${dashboard?.senior?.age ?? '-'}세 · ${dashboard?.senior?.address ?? '-'}`} />
        <InfoCard icon={<Activity />} label="움직임" value={latest?.motionDetected ? '감지됨' : '미감지'} sub={`최근 활동 ${formatTime(latest?.eventTime)}`} />
        <InfoCard icon={<DoorOpen />} label="문 열림" value={latest?.doorOpened ? '열림' : '닫힘'} sub="새벽 문 열림은 이상 행동으로 판단" />
        <InfoCard icon={<Thermometer />} label="실내 환경" value={`${latest?.temperature ?? '-'}°C`} sub={`습도 ${latest?.humidity ?? '-'}% · 조도 ${latest?.illuminance ?? '-'}`} />
      </section>

      <section className="content-grid">
        <div className="panel risk-detail">
          <div className="panel-title">
            <HeartPulse />
            <h2>위험도 분석 결과</h2>
          </div>
          <div className="score-meter">
            <div style={{ width: `${risk.score}%`, background: meta.color }} />
          </div>
          <ul className="reasons">
            {risk.reasons.map((reason) => <li key={reason}>{reason}</li>)}
          </ul>
        </div>

        <div className="panel">
          <div className="panel-title">
            <ShieldCheck />
            <h2>생활패턴 차트</h2>
          </div>
          <ResponsiveContainer width="100%" height={260}>
            <AreaChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="label" />
              <YAxis />
              <Tooltip />
              <Area type="monotone" dataKey="activity" name="활동" stroke="#2563eb" fill="#bfdbfe" />
              <Line type="monotone" dataKey="temperature" name="온도" stroke="#dc2626" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section className="content-grid">
        <EventPanel events={dashboard?.recentEvents ?? []} />
        <AlertPanel alerts={dashboard?.alerts ?? []} />
      </section>
    </>
  );
}

function SeniorPage({ seniors, onCreated }) {
  const [form, setForm] = useState({
    name: '',
    age: 78,
    address: '',
    guardianName: '',
    guardianPhone: '',
    baselineActiveStartHour: 7,
    baselineActiveEndHour: 22,
  });

  const submit = async (event) => {
    event.preventDefault();
    await fetch(`${API_BASE}/seniors`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form),
    });
    setForm({ ...form, name: '', address: '', guardianName: '', guardianPhone: '' });
    await onCreated();
  };

  return (
    <section className="content-grid">
      <form className="panel form-panel" onSubmit={submit}>
        <div className="panel-title">
          <UserPlus />
          <h2>독거노인 등록</h2>
        </div>
        <Field label="이름" value={form.name} onChange={(value) => setForm({ ...form, name: value })} />
        <Field label="나이" type="number" value={form.age} onChange={(value) => setForm({ ...form, age: Number(value) })} />
        <Field label="주소" value={form.address} onChange={(value) => setForm({ ...form, address: value })} />
        <Field label="보호자 이름" value={form.guardianName} onChange={(value) => setForm({ ...form, guardianName: value })} />
        <Field label="보호자 연락처" value={form.guardianPhone} onChange={(value) => setForm({ ...form, guardianPhone: value })} />
        <button type="submit">등록하기</button>
      </form>

      <div className="panel">
        <div className="panel-title">
          <UserRound />
          <h2>등록된 대상자</h2>
        </div>
        <div className="event-list">
          {seniors.map((senior) => (
            <div className="person-row" key={senior.id}>
              <strong>{senior.name}</strong>
              <span>{senior.age}세 · {senior.address}</span>
              <span>보호자 {senior.guardianName} / {senior.guardianPhone}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

function SimulatorPage({ seniorId, onSent }) {
  const [form, setForm] = useState({
    motionDetected: true,
    doorOpened: false,
    temperature: 24,
    humidity: 45,
    illuminance: 320,
    eventTime: nowLocalInput(),
  });

  const send = async (event) => {
    event.preventDefault();
    await fetch(`${API_BASE}/sensor-events`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        seniorId,
        ...form,
        temperature: Number(form.temperature),
        humidity: Number(form.humidity),
        illuminance: Number(form.illuminance),
      }),
    });
    await onSent();
  };

  return (
    <section className="content-grid">
      <form className="panel form-panel" onSubmit={send}>
        <div className="panel-title">
          <Radar />
          <h2>가상 센서 수동 전송</h2>
        </div>
        <label className="check-field">
          <input type="checkbox" checked={form.motionDetected} onChange={(event) => setForm({ ...form, motionDetected: event.target.checked })} />
          움직임 감지
        </label>
        <label className="check-field">
          <input type="checkbox" checked={form.doorOpened} onChange={(event) => setForm({ ...form, doorOpened: event.target.checked })} />
          문 열림
        </label>
        <Field label="온도" type="number" value={form.temperature} onChange={(value) => setForm({ ...form, temperature: value })} />
        <Field label="습도" type="number" value={form.humidity} onChange={(value) => setForm({ ...form, humidity: value })} />
        <Field label="조도" type="number" value={form.illuminance} onChange={(value) => setForm({ ...form, illuminance: value })} />
        <Field label="발생 시간" type="datetime-local" value={form.eventTime} onChange={(value) => setForm({ ...form, eventTime: value })} />
        <button type="submit"><Send size={18} /> 센서 데이터 전송</button>
      </form>

      <div className="panel guide-panel">
        <div className="panel-title">
          <FileText />
          <h2>시연 포인트</h2>
        </div>
        <p>이 화면은 실제 센서 없이 심사장에서 바로 데이터를 넣어 위험도 분석 결과를 보여주기 위한 화면입니다.</p>
        <ul className="reasons">
          <li>움직임 없음 + 고온 + 새벽 문 열림을 만들면 위험 점수가 올라갑니다.</li>
          <li>센서 이벤트를 전송하면 백엔드 저장 후 AI 분석 서버가 즉시 점수를 계산합니다.</li>
          <li>향후 ESP32도 같은 API로 데이터를 전송하면 됩니다.</li>
        </ul>
      </div>
    </section>
  );
}

function ScenarioPage({ runDemo }) {
  const items = [
    { key: 'normal', title: '1단계 정상', text: '주기적 움직임과 적정 온습도가 감지되어 정상 상태를 보여줍니다.' },
    { key: 'caution', title: '2단계 주의', text: '활동 부족과 평소와 다른 생활패턴으로 보호자 확인이 필요한 상태를 보여줍니다.' },
    { key: 'danger', title: '3단계 위험', text: '장시간 움직임 없음, 위험 온도, 새벽 문 열림으로 긴급 확인 상태를 보여줍니다.' },
  ];

  return (
    <section className="panel scenario-panel">
      <div className="panel-title">
        <PlayCircle />
        <h2>발표용 클릭 시나리오</h2>
      </div>
      <div className="scenario-list">
        {items.map((item) => (
          <div className="scenario-card" key={item.key}>
            <strong>{item.title}</strong>
            <p>{item.text}</p>
            <button className={item.key === 'danger' ? 'danger-btn' : ''} onClick={() => runDemo(item.key)}>
              이 상태로 전환
            </button>
          </div>
        ))}
      </div>
    </section>
  );
}

function EventPanel({ events }) {
  return (
    <div className="panel">
      <div className="panel-title">
        <Home />
        <h2>최근 센서 이벤트</h2>
      </div>
      <div className="event-list">
        {events.map((event) => (
          <div className="event-row" key={event.id}>
            <span>{formatTime(event.eventTime)}</span>
            <strong>{event.motionDetected ? '움직임 감지' : '움직임 없음'}</strong>
            <span>{event.doorOpened ? '문 열림' : '문 닫힘'}</span>
            <span>{event.temperature}°C / {event.humidity}%</span>
          </div>
        ))}
      </div>
    </div>
  );
}

function AlertPanel({ alerts }) {
  return (
    <div className="panel">
      <div className="panel-title">
        <Bell />
        <h2>보호자 알림 이력</h2>
      </div>
      <div className="alert-list">
        {alerts.length === 0 && <p className="empty">아직 알림 이력이 없습니다.</p>}
        {alerts.map((alert) => (
          <div className={`alert-row ${alert.level.toLowerCase()}`} key={alert.id}>
            <AlertTriangle />
            <div>
              <strong>{alert.level}</strong>
              <p>{alert.message}</p>
              <span>{alert.receiver} · {formatTime(alert.createdAt)}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

function Field({ label, type = 'text', value, onChange }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type={type} value={value} onChange={(event) => onChange(event.target.value)} required />
    </label>
  );
}

function InfoCard({ icon, label, value, sub }) {
  return (
    <div className="info-card">
      <div className="icon">{icon}</div>
      <span>{label}</span>
      <strong>{value}</strong>
      <p>{sub}</p>
    </div>
  );
}

function scenarioLabel(scenario) {
  return { normal: '정상', caution: '주의', danger: '위험' }[scenario] ?? scenario;
}

createRoot(document.getElementById('root')).render(<App />);
