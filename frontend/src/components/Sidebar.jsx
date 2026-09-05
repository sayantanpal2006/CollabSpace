import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

export default function Sidebar({ activeChannel, onChannel, onWorkspaceChange, initialWorkspaceId }) {
  const [workspaces, setWorkspaces] = useState([]);
  const [channels, setChannels] = useState([]);
  const [categories, setCategories] = useState([]);
  const [activeWorkspace, setActiveWorkspace] = useState(null);
  const [name, setName] = useState('');
  const nav = useNavigate();

  useEffect(() => {
    api.get('/workspaces').then((r) => {
      setWorkspaces(r.data);
      const selected = r.data.find((w) => w.id === initialWorkspaceId) || r.data[0] || null;
      if (selected) {
        setActiveWorkspace(selected);
      }
    });
  }, [initialWorkspaceId]);

  useEffect(() => {
    if (!activeWorkspace) return;
    onWorkspaceChange?.(activeWorkspace);
    nav(`/workspace/${activeWorkspace.id}`, { replace: true });
    Promise.all([
      api.get(`/workspaces/${activeWorkspace.id}/channels`),
      api.get(`/workspaces/${activeWorkspace.id}/categories`)
    ]).then(([channelRes, categoryRes]) => {
      setChannels(channelRes.data);
      setCategories(categoryRes.data);
      if (!activeChannel || !channelRes.data.some((c) => c.id === activeChannel.id)) {
        onChannel(channelRes.data[0] || null);
      }
    });
  }, [activeWorkspace?.id]);

  const groupedChannels = useMemo(() => {
    const byKey = new Map();
    categories.forEach((category) => byKey.set(category.id, { label: category.name, channels: [] }));
    channels.forEach((channel) => {
      const key = channel.categoryId || 'ungrouped';
      if (!byKey.has(key)) {
        byKey.set(key, { label: channel.categoryName || 'General', channels: [] });
      }
      byKey.get(key).channels.push(channel);
    });
    return [...byKey.values()];
  }, [channels, categories]);

  const createWorkspace = async () => {
    if (!name.trim()) return;
    const r = await api.post('/workspaces', { name, description: '' });
    setWorkspaces((x) => [...x, r.data]);
    setActiveWorkspace(r.data);
    setName('');
  };

  const createCategory = async () => {
    if (!activeWorkspace) return;
    const categoryName = window.prompt('Category name');
    if (!categoryName?.trim()) return;
    await api.post(`/workspaces/${activeWorkspace.id}/categories`, { name: categoryName.trim(), position: categories.length });
    const r = await api.get(`/workspaces/${activeWorkspace.id}/categories`);
    setCategories(r.data);
  };

  const createChannel = async () => {
    if (!activeWorkspace) return;
    const channelName = window.prompt('Channel name (without #)');
    if (!channelName?.trim()) return;
    const categoryId = categories[0]?.id || null;
    const r = await api.post(`/workspaces/${activeWorkspace.id}/channels`, {
      name: channelName.trim(),
      description: '',
      privateChannel: false,
      categoryId
    });
    setChannels((prev) => [...prev, r.data]);
  };

  const inviteMember = async () => {
    if (!activeWorkspace) return;
    const email = window.prompt('Invite user email');
    if (!email?.trim()) return;
    const res = await api.post(`/workspaces/${activeWorkspace.id}/invites`, { email: email.trim(), expiresInHours: 72 });
    window.alert(`Invite created. Token: ${res.data.token}`);
  };

  return (
    <aside className="sidebar">
      <div className="brand"><span className="brandmark">C</span><span>CollabSpace</span></div>
      <div className="section">
        <div className="section-title">Workspaces <button className="icon-btn" onClick={createWorkspace}>+</button></div>
        <div className="workspace-list">
          {workspaces.map((w) => (
            <button key={w.id} className={`workspace-pill ${activeWorkspace?.id === w.id ? 'selected' : ''}`} onClick={() => setActiveWorkspace(w)}>
              {w.name.slice(0, 2).toUpperCase()}
            </button>
          ))}
        </div>
        <input placeholder="New workspace" value={name} onChange={(e) => setName(e.target.value)} />
      </div>
      {activeWorkspace && (
        <div className="section grow">
          <div className="workspace-name">{activeWorkspace.name}</div>
          <div className="section-title">Channels <span><button className="icon-btn" onClick={createCategory}>+C</button> <button className="icon-btn" onClick={createChannel}>+</button></span></div>
          {groupedChannels.map((group) => (
            <div key={group.label}>
              <div className="category-label">{group.label}</div>
              {group.channels.map((c) => (
                <button key={c.id} className={`channel ${activeChannel?.id === c.id ? 'active' : ''}`} onClick={() => onChannel(c)}># {c.name}</button>
              ))}
            </div>
          ))}
          {channels.length === 0 && <div className="muted">No channels yet</div>}
        </div>
      )}
      <div className="sidebar-bottom">
        <button className="ghost" onClick={inviteMember}>Invite</button>
        <button className="ghost" onClick={() => nav('/profile')}>Profile</button>
        <button className="ghost" onClick={() => nav('/settings')}>Settings</button>
      </div>
    </aside>
  );
}
