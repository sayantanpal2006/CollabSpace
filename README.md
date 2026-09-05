# CollabSpace (Discord-style + AI MVP)

CollabSpace is a full-stack collaboration app inspired by Discord, with workspace/channel chat, roles/permissions, real-time messaging, and integrated AI assistant utilities.

## Architecture overview

- **Frontend:** React + Vite web client (`/frontend`)
- **Backend:** Spring Boot API + WebSocket gateway (`/backend`)
- **Data:** PostgreSQL via Spring Data JPA entities/repositories
- **Realtime:** STOMP over WebSocket (`/ws`)
- **Infra:** Redis for presence/rate limiting

## Feature checklist

- [x] JWT auth (register/login/me), profile/settings pages
- [x] Workspace/server model with members + roles (`OWNER/ADMIN/MEMBER`)
- [x] Channel categories + text channels
- [x] Invite flow (create/list/revoke/accept invite token)
- [x] Real-time channel chat
- [x] Typing indicators and presence updates
- [x] Message pagination/history + reactions + thread replies (`replyTo`)
- [x] Mentions/reply notifications in-app
- [x] Message search
- [x] AI assistant panel:
  - [x] Summarize recent channel messages
  - [x] Draft reply to selected thread message
  - [x] Extract action items
- [x] AI moderation helper with soft moderator alerts
- [x] AI provider abstraction with graceful fallback when key is missing
- [x] Seed data for demo workspace/users/channels/messages
- [x] Focused service tests for moderation + AI fallback

## Local setup

### 1) Configure environment

Copy env files:

```bash
cp .env.example .env
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env
```

Optional AI config in `.env` / `backend/.env`:

- `AI_API_KEY` (leave empty to use fallback mode)
- `AI_BASE_URL` (default `https://api.openai.com`)
- `AI_MODEL` (default `gpt-4o-mini`)

### 2) Run with Docker (single command sequence)

```bash
docker compose up --build
```

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

### 3) Demo credentials (seed)

When `SEED_DATA=true` on first run:

- `admin@example.com` / `Admin@12345`
- `user@example.com` / `User@12345`

## Scripts

### Backend

```bash
cd backend
mvn spring-boot:run
mvn test
```

### Frontend

```bash
cd frontend
npm install
npm run dev
npm run build
```

## AI behavior

AI endpoints:

- `GET /api/ai/channels/{channelId}/summarize`
- `POST /api/ai/channels/{channelId}/draft-reply`
- `GET /api/ai/channels/{channelId}/action-items`

If `AI_API_KEY` is missing (or provider call fails), the app automatically returns deterministic fallback outputs so the assistant panel remains usable.

## Future work (beyond MVP)

- Voice/video parity (WebRTC rooms, SFU integration, moderation controls)
- Rich permission matrix per channel/action
- Advanced thread UI and notification preferences
