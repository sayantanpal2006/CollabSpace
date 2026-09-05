import { useEffect, useRef } from 'react';

export default function MessageList({ messages, currentUserId, onReply, onEdit, onDelete, onReact }) {
  const ref = useRef();
  useEffect(() => ref.current?.scrollIntoView({ behavior: 'smooth' }), [messages]);

  return (
    <div className="messages">
      {messages.map((m) => (
        <div className="message" key={m.id}>
          <div className="avatar">{m.senderUsername?.slice(0, 1).toUpperCase()}</div>
          <div className="message-body">
            <div>
              <b>{m.senderUsername}</b>
              <span className="time">{new Date(m.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
              {m.edited && <span className="edited">edited</span>}
            </div>
            {m.replyToId && <div className="reply-chip">↪ thread reply</div>}
            <div className="content">{m.content}</div>
            {m.moderationWarning && <div className="moderation-warning">⚠ {m.moderationWarning}</div>}
            <div className="message-actions">
              <button onClick={() => onReply(m)}>Reply</button>
              <button onClick={() => onReact(m)}>👍</button>
              {m.senderId === currentUserId && (
                <>
                  <button onClick={() => onEdit(m)}>Edit</button>
                  <button onClick={() => onDelete(m)}>Delete</button>
                </>
              )}
            </div>
          </div>
        </div>
      ))}
      <div ref={ref} />
    </div>
  );
}
