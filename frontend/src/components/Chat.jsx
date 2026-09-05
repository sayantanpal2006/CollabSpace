import { useEffect, useRef, useState } from 'react';
import api from '../services/api';
import { connect, disconnect, getClient } from '../websocket/stompClient';
import MessageList from './MessageList';
import { useAuth } from '../context/AuthContext';

export default function Chat({ channel }) {
  const { user } = useAuth();
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState('');
  const [connected, setConnected] = useState(false);
  const [replyTarget, setReplyTarget] = useState(null);
  const [typingUsers, setTypingUsers] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [aiOutput, setAiOutput] = useState('');
  const [fallbackMode, setFallbackMode] = useState(false);
  const sub = useRef();
  const typingSub = useRef();
  const typingTimeout = useRef();

  useEffect(() => {
    if (!channel) return;
    let alive = true;
    setReplyTarget(null);
    setAiOutput('');
    api.get(`/channels/${channel.id}/messages?size=100`).then((r) => {
      if (alive) setMessages([...r.data.content].reverse());
    });
    connect((client) => {
      setConnected(true);
      sub.current = client.subscribe(`/topic/channel/${channel.id}`, (frame) => {
        setMessages((m) => [...m, JSON.parse(frame.body)]);
      });
      typingSub.current = client.subscribe(`/topic/channel/${channel.id}/typing`, (frame) => {
        const event = JSON.parse(frame.body);
        if (!event.username || event.username === user?.username) return;
        setTypingUsers((prev) => [...new Set([...prev, event.username])]);
      });
    }, () => setConnected(false));

    return () => {
      alive = false;
      sub.current?.unsubscribe();
      typingSub.current?.unsubscribe();
      disconnect();
    };
  }, [channel?.id]);

  const sendTypingEvent = () => {
    if (!getClient()?.connected || !channel) return;
    getClient().publish({ destination: '/app/typing', headers: { channelId: channel.id }, body: JSON.stringify({ typing: true }) });
    clearTimeout(typingTimeout.current);
    typingTimeout.current = setTimeout(() => setTypingUsers([]), 2500);
  };

  const send = () => {
    if (!text.trim() || !getClient()?.connected) return;
    getClient().publish({
      destination: '/app/chat.send',
      headers: { channelId: channel.id },
      body: JSON.stringify({ content: text, replyToId: replyTarget?.id || null })
    });
    setText('');
    setReplyTarget(null);
  };

  const editMessage = async (message) => {
    const updated = window.prompt('Edit message', message.content);
    if (!updated?.trim()) return;
    const res = await api.put(`/messages/${message.id}`, { content: updated.trim(), replyToId: message.replyToId || null });
    setMessages((prev) => prev.map((m) => (m.id === message.id ? res.data : m)));
  };

  const deleteMessage = async (message) => {
    await api.delete(`/messages/${message.id}`);
    setMessages((prev) => prev.map((m) => (m.id === message.id ? { ...m, content: 'This message was deleted', deleted: true } : m)));
  };

  const addReaction = async (message) => {
    await api.post(`/messages/${message.id}/reactions`, { emoji: '👍' });
  };

  const runSearch = async () => {
    if (!searchQuery.trim()) return;
    const res = await api.get('/messages/search', { params: { workspaceId: channel.workspaceId, q: searchQuery.trim(), size: 50 } });
    setMessages([...res.data.content].reverse());
  };

  const runAi = async (type) => {
    if (!channel) return;
    if (type === 'draft' && !replyTarget) {
      setAiOutput('Select a message with Reply first to draft a thread response.');
      return;
    }
    const endpoint = type === 'summary'
      ? api.get(`/ai/channels/${channel.id}/summarize`)
      : type === 'actions'
        ? api.get(`/ai/channels/${channel.id}/action-items`)
        : api.post(`/ai/channels/${channel.id}/draft-reply`, { messageId: replyTarget.id, tone: 'friendly' });
    const res = await endpoint;
    setAiOutput(res.data.output);
    setFallbackMode(res.data.fallbackUsed);
  };

  if (!channel) return <div className="empty"><div className="empty-icon">#</div><h2>Pick a channel</h2><p>Select a channel from the sidebar to start collaborating.</p></div>;

  return (
    <div className="chat">
      <header className="chat-header">
        <div>
          <h2># {channel.name}</h2>
          <p>{channel.description || 'Team conversation'}</p>
        </div>
        <span className={connected ? 'status online' : 'status'}>{connected ? 'Live' : 'Connecting'}</span>
      </header>
      <div className="search-row">
        <input placeholder="Search messages" value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} />
        <button onClick={runSearch}>Search</button>
        <button onClick={() => api.get(`/channels/${channel.id}/messages?size=100`).then((r) => setMessages([...r.data.content].reverse()))}>Reset</button>
      </div>
      <MessageList
        messages={messages}
        currentUserId={user?.id}
        onReply={setReplyTarget}
        onEdit={editMessage}
        onDelete={deleteMessage}
        onReact={addReaction}
      />
      {typingUsers.length > 0 && <div className="typing-hint">{typingUsers.join(', ')} typing...</div>}
      {replyTarget && <div className="reply-bar">Replying to: {replyTarget.senderUsername} — {replyTarget.content.slice(0, 80)} <button onClick={() => setReplyTarget(null)}>Clear</button></div>}
      <div className="composer">
        <input value={text} onChange={(e) => { setText(e.target.value); sendTypingEvent(); }} onKeyDown={(e) => e.key === 'Enter' && send()} placeholder="Message this channel... Use @username to mention" />
        <button onClick={send}>Send</button>
      </div>
      <div className="ai-panel">
        <div className="section-title">AI Assistant {fallbackMode ? '(fallback mode)' : ''}</div>
        <div className="ai-actions">
          <button onClick={() => runAi('summary')}>Summarize recent</button>
          <button onClick={() => runAi('draft')}>Draft reply</button>
          <button onClick={() => runAi('actions')}>Extract action items</button>
        </div>
        <pre>{aiOutput || 'Run an AI utility for this channel.'}</pre>
      </div>
    </div>
  );
}
