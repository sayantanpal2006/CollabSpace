import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../services/api';
import Sidebar from '../components/Sidebar';
import Chat from '../components/Chat';

export default function Workspace() {
  const { workspaceId } = useParams();
  const [channel, setChannel] = useState(null);
  const [workspace, setWorkspace] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [unread, setUnread] = useState(0);

  useEffect(() => {
    api.get('/notifications?size=6').then((r) => setNotifications(r.data.content || []));
    api.get('/notifications/unread-count').then((r) => setUnread(r.data));
  }, [workspace?.id, channel?.id]);

  return (
    <div className="app-shell">
      <Sidebar
        activeChannel={channel}
        onChannel={setChannel}
        onWorkspaceChange={setWorkspace}
        initialWorkspaceId={workspaceId}
      />
      <main className="main"><Chat channel={channel} /></main>
      <aside className="members">
        <div className="members-title">Workspace</div>
        <div className="member-card">
          <div className="avatar">W</div>
          <div><b>{workspace?.name || 'Select workspace'}</b><span>Unread notifications: {unread}</span></div>
        </div>
        <div className="section-title" style={{ marginTop: 18 }}>Mentions & replies</div>
        <div className="notifications-list">
          {notifications.length === 0 && <div className="hint">No notifications yet.</div>}
          {notifications.map((notification) => (
            <button
              key={notification.id}
              className={`notification-item ${notification.read ? '' : 'unread'}`}
              onClick={async () => {
                await api.put(`/notifications/${notification.id}/read`);
                setNotifications((prev) => prev.map((x) => x.id === notification.id ? { ...x, read: true } : x));
                const c = await api.get('/notifications/unread-count');
                setUnread(c.data);
              }}
            >
              <div>{notification.message}</div>
              <small>{new Date(notification.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</small>
            </button>
          ))}
        </div>
      </aside>
    </div>
  );
}
