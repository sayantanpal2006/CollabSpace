# CollabSpace

A production-style real-time collaboration platform inspired by Slack, Discord and Microsoft Teams. Built as a modular monolith for a Java full-stack portfolio.

## Stack

- Java 21 + Spring Boot 3.5
- Spring Web, Spring Security, Spring Data JPA
- JWT + BCrypt
- WebSocket + STOMP + SockJS
- PostgreSQL
- Redis
- React + Vite + JavaScript
- Axios + React Router + STOMP.js
- Docker Compose
- OpenAPI / Swagger

## Features implemented

- JWT registration/login/current-user flow
- BCrypt password hashing
- Workspace creation, membership and role authorization
- Public/private channels and channel membership
- Persistent channel messages with pagination
- Real-time STOMP channel messaging
- Message edit/delete and reactions
- Direct-message conversation/history APIs
- Online/offline presence stored in PostgreSQL and Redis
- Typing-event WebSocket foundation
- Notifications API
- Secure local file upload/download with size/type validation
- PostgreSQL message search
- Redis-backed authentication rate limiting
- Global API error handling
- Swagger/OpenAPI
- Responsive React UI
- Docker Compose for PostgreSQL, Redis, backend and frontend
- Optional development seed data

## Run with Docker

1. Copy `.env.example` to `.env`.
2. Replace `JWT_SECRET` with a long random value.
3. Run:

```bash
docker compose up --build
```

Open `http://localhost:5173`.

Swagger: `http://localhost:8080/swagger-ui.html`

To seed development data, keep `SEED_DATA=true` on the first startup. Development-only credentials:

- `admin@example.com` / `Admin@12345`
- `user@example.com` / `User@12345`

Change/remove these credentials before any non-development deployment.

## Local development

### Backend

Requirements: Java 21, Maven, PostgreSQL and Redis.

```bash
cd backend
mvn spring-boot:run
```

### Frontend

Requirements: Node.js 20+.

```bash
cd frontend
npm install
npm run dev
```

## API

Authentication:

- POST `/api/auth/register`
- POST `/api/auth/login`
- POST `/api/auth/logout`
- GET `/api/auth/me`

Users:

- GET `/api/users/me`
- PUT `/api/users/me`

Workspaces:

- GET `/api/workspaces`
- POST `/api/workspaces`
- GET `/api/workspaces/{id}`
- PUT `/api/workspaces/{id}`
- DELETE `/api/workspaces/{id}`
- POST `/api/workspaces/{id}/members`
- DELETE `/api/workspaces/{id}/members/{userId}`

Channels:

- GET `/api/workspaces/{workspaceId}/channels`
- POST `/api/workspaces/{workspaceId}/channels`
- PUT `/api/channels/{id}`
- DELETE `/api/channels/{id}`
- POST `/api/channels/{id}/join`
- POST `/api/channels/{id}/leave`

Messages:

- GET `/api/channels/{channelId}/messages`
- PUT `/api/messages/{id}`
- DELETE `/api/messages/{id}`
- POST `/api/messages/{id}/reactions`
- GET `/api/messages/search?workspaceId=...&q=...`

Direct messages:

- GET `/api/conversations`
- POST `/api/conversations/{userId}/messages`
- GET `/api/conversations/{userId}/messages`

Notifications:

- GET `/api/notifications`
- GET `/api/notifications/unread-count`
- PUT `/api/notifications/{id}/read`

Files:

- POST `/api/files`
- GET `/api/files/{id}`

## WebSocket

STOMP endpoint:

`/ws`

Channel send:

`/app/chat.send`

Required native STOMP header:

`channelId: <channel UUID>`

Channel subscription:

`/topic/channel/{channelId}`

Typing:

`/app/typing`

Typing subscription:

`/topic/channel/{channelId}/typing`

Direct-message send:

`/app/dm.send`

Direct-message user queue:

`/user/queue/messages`

JWT is sent in the STOMP CONNECT `Authorization: Bearer <token>` header.

## Database

Main tables/entities:

- users
- workspaces
- workspace_members
- channels
- channel_members
- messages
- direct_conversations
- direct_messages
- message_reactions
- notifications
- file_attachments

Indexes are focused on high-frequency membership, channel-message history, sender and notification lookups.

## Security

- Passwords are never returned by DTOs.
- JWT secret and database credentials come from environment variables.
- Workspace and channel membership is checked server-side.
- Users cannot edit/delete another user's message.
- Private channels require membership.
- Uploaded files are limited by size and MIME type and use generated storage names.
- Global exception handling avoids returning stack traces.
- Authentication endpoints have Redis-backed request limiting.

## Scaling path

The first version is intentionally a modular monolith. Redis is already used for presence and rate limiting. For horizontal scaling, WebSocket events can be moved from the in-memory simple broker to a shared broker/Redis Pub/Sub strategy while keeping the domain modules in one deployable application.

## Portfolio talking points

This project demonstrates REST API design, Spring Security/JWT, JPA data modelling, authorization/IDOR prevention, WebSockets/STOMP, Redis, PostgreSQL indexing, pagination, file security, Docker and a React client consuming both REST and real-time APIs.
