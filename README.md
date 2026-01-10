# CardVault – One Piece TCG Marketplace (Simple Demo)

This project is a **simple marketplace demo** for One Piece TCG cards:

- Browse **listings**
- Create a new listing
- **Buy** a listing using fake credits (no real money)
- **Trade**: create trade offers, view inbox/sent trades, accept/reject

The backend is **Spring Boot + JPA + PostgreSQL** and the frontend is **React + Vite**.

## Run (recommended)

### 1) Start Postgres

```bash
docker compose up -d
```

It will start Postgres with:

- DB: `cardvault`
- User: `cardvault`
- Password: `cardvault`

### 2) Start backend (Spring Boot)

```bash
cd backend/api
./gradlew bootRun
```

Backend runs on `http://localhost:8081`.

### 3) Start frontend (React)

```bash
cd frontend/CardVault_Frontend
npm install
npm run dev
```

Frontend runs on the Vite URL it prints (usually `http://localhost:5173`).

## Notes

- The app seeds a few sample users and One Piece cards/listings on first run.
- The "current user" is selected in the UI (no authentication; intentionally simple).
