import { NavLink, Route, Routes } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { api } from "./app/api";

import HomePage from "./pages/HomePage";
import CardsPage from "./pages/CardsPage";
import ListingsPage from "./pages/ListingsPage";
import NewListingPage from "./pages/NewListingPage";
import TradesPage from "./pages/TradesPage";
import MyListingsPage from "./pages/MyListingsPage";

function NavItem({ to, children }) {
  return (
    <NavLink
      to={to}
      style={({ isActive }) => ({
        textDecoration: "none",
        padding: "8px 10px",
        borderRadius: 10,
        color: isActive ? "white" : "#222",
        background: isActive ? "#111" : "transparent",
      })}
    >
      {children}
    </NavLink>
  );
}

export default function App() {
  const [users, setUsers] = useState([]);
  const [user, setUser] = useState(() => localStorage.getItem("cv_user") || "");

  const loadUsers = async () => {
    const { data } = await api.get("/users");
    setUsers(data);
    return data;
  };

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const data = await loadUsers();
        if (!cancelled) {
          if (!user && data?.[0]?.username) setUser(data[0].username);
        }
      } catch {
        // Ignore: backend might be offline while the frontend loads.
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (user) localStorage.setItem("cv_user", user);
  }, [user]);

  const current = useMemo(
    () => users.find((u) => u.username?.toLowerCase() === user.toLowerCase()),
    [users, user]
  );

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
          <nav style={{ display: "flex", gap: 8, alignItems: "center" }}>
            <span style={{ fontWeight: 800, marginRight: 6 }}>CardVault</span>
            <NavItem to="/">Home</NavItem>
            <NavItem to="/listings">Listings</NavItem>
            <NavItem to="/my-listings">My listings</NavItem>
            <NavItem to="/trades">Trades</NavItem>
            <NavItem to="/cards">Cards</NavItem>
          </nav>

          <div style={{ display: "flex", gap: 10, alignItems: "center" }}>
            <div style={{ fontSize: 13, color: "#333" }}>
              Credits: <b>{current?.credits ?? "—"}</b>
            </div>
            <select value={user} onChange={(e) => setUser(e.target.value)}>
              {users.map((u) => (
                <option key={u.id} value={u.username}>
                  {u.username}
                </option>
              ))}
            </select>
          </div>
        </div>
      </header>

      <main style={{ maxWidth: 1100, margin: "22px auto", padding: "0 16px" }}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/cards" element={<CardsPage />} />
          <Route path="/listings" element={<ListingsPage currentUser={user} onUserUpdate={loadUsers} />} />
          <Route path="/my-listings" element={<MyListingsPage currentUser={user} onUserUpdate={loadUsers} />} />
          <Route path="/new-listing" element={<NewListingPage currentUser={user} onUserUpdate={loadUsers} />} />
          <Route path="/trades" element={<TradesPage currentUser={user} onUserUpdate={loadUsers} />} />
        </Routes>
      </main>
    </div>
  );
}
