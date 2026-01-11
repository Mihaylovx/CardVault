import { NavLink, Route, Routes, Navigate, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { api } from "./app/api";

import HomePage from "./pages/HomePage";
import CardsPage from "./pages/CardsPage";
import ListingsPage from "./pages/ListingsPage";
import NewListingPage from "./pages/NewListingPage";
import TradesPage from "./pages/TradesPage";
import MyListingsPage from "./pages/MyListingsPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";

function NavItem({ to, children }) {
  return (
    <NavLink
      to={to}
      style={({ isActive }) => ({
        color: isActive ? "#111" : "#555",
        textDecoration: "none",
        padding: "6px 10px",
        borderRadius: 8,
        background: isActive ? "#f1f1f1" : "transparent",
        fontWeight: 600,
      })}
    >
      {children}
    </NavLink>
  );
}

function RequireAuth({ auth, children }) {
  if (!auth) return <Navigate to="/login" replace />;
  return children;
}

export default function App() {
  const [users, setUsers] = useState([]);
  const [auth, setAuth] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem("cv_auth"));
    } catch {
      return null;
    }
  });

  const loadUsers = async () => {
    const { data } = await api.get("/users");
    setUsers(data);

    if (auth?.username) {
      const updated = data.find((u) => u.username?.toLowerCase() === auth.username.toLowerCase());
      if (updated) {
        const next = { ...auth, credits: updated.credits };
        setAuth(next);
        localStorage.setItem("cv_auth", JSON.stringify(next));
      }
    }
    return data;
  };

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        if (!cancelled) await loadUsers();
      } catch {}
    })();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const current = useMemo(() => {
    if (!auth?.username) return null;
    return users.find((u) => u.username?.toLowerCase() === auth.username.toLowerCase()) || auth;
  }, [users, auth]);

  const logout = () => {
    localStorage.removeItem("cv_auth");
    setAuth(null);
  };

  return (
    <div style={{ fontFamily: "system-ui, sans-serif" }}>
      <header
        style={{
          borderBottom: "1px solid #eee",
          padding: "14px 16px",
          position: "sticky",
          top: 0,
          background: "white",
        }}
      >
        <div
          style={{
            maxWidth: 1100,
            margin: "0 auto",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            gap: 12,
          }}
        >
          <nav style={{ display: "flex", gap: 8, alignItems: "center", flexWrap: "wrap" }}>
            <span style={{ fontWeight: 800, marginRight: 6 }}>CardVault</span>
            <NavItem to="/">Home</NavItem>
            {auth ? (
              <>
                <NavItem to="/listings">Listings</NavItem>
                <NavItem to="/my-listings">My listings</NavItem>
                <NavItem to="/trades">Trades</NavItem>
                <NavItem to="/cards">Cards</NavItem>
              </>
            ) : null}
          </nav>

          <div style={{ display: "flex", alignItems: "center", gap: 10, flexWrap: "wrap" }}>
            {auth ? (
              <>
                <span style={{ color: "#444" }}>
                  Logged in as <b>{auth.username}</b> · {current?.credits != null ? Number(current.credits).toFixed(2) : "—"} credits
                </span>
                <button onClick={logout} style={{ padding: "7px 10px", borderRadius: 8, border: "1px solid #ddd", background: "white" }}>
                  Logout
                </button>
              </>
            ) : (
              <>
                <NavItem to="/login">Login</NavItem>
                <NavItem to="/register">Register</NavItem>
              </>
            )}
          </div>
        </div>
      </header>

      <main style={{ maxWidth: 1100, margin: "22px auto", padding: "0 16px" }}>
        <Routes>
          <Route path="/" element={<HomePage />} />

          <Route path="/login" element={<LoginPage onAuth={setAuth} />} />
          <Route path="/register" element={<RegisterPage onAuth={setAuth} />} />

          <Route
            path="/cards"
            element={
              <RequireAuth auth={auth}>
                <CardsPage />
              </RequireAuth>
            }
          />
          <Route
            path="/listings"
            element={
              <RequireAuth auth={auth}>
                <ListingsPage currentUser={current} onUserUpdate={loadUsers} />
              </RequireAuth>
            }
          />
          <Route
            path="/my-listings"
            element={
              <RequireAuth auth={auth}>
                <MyListingsPage currentUser={current} onUserUpdate={loadUsers} />
              </RequireAuth>
            }
          />
          <Route
            path="/new-listing"
            element={
              <RequireAuth auth={auth}>
                <NewListingPage currentUser={current} onUserUpdate={loadUsers} />
              </RequireAuth>
            }
          />
          <Route
            path="/trades"
            element={
              <RequireAuth auth={auth}>
                <TradesPage currentUser={current} onUserUpdate={loadUsers} />
              </RequireAuth>
            }
          />

          <Route path="*" element={<Navigate to={auth ? "/listings" : "/login"} replace />} />
        </Routes>
      </main>
    </div>
  );
}
