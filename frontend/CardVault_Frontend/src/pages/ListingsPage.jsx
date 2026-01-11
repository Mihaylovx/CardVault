import { Link } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { api } from "../app/api";

function money(n) {
  if (n == null) return "—";
  return Number(n).toFixed(2);
}

export default function ListingsPage({ currentUser, onUserUpdate }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState(null);

  const [q, setQ] = useState("");
  const [status, setStatus] = useState("ACTIVE");

  const params = useMemo(() => {
    const p = {};
    if (q.trim()) p.q = q.trim();
    if (status) p.status = status;
    return p;
  }, [q, status]);

  const load = async () => {
    const { data } = await api.get("/listings", { params });
    setItems(data);
  };

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      setErr(null);
      try {
        await load();
      } catch (e) {
        if (!cancelled) setErr(e.response?.data?.message || e.message);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [params]);

  const buy = async (listingId) => {
    try {
      await api.post("/purchases", null, {
        params: { listingId, buyer: currentUser?.username, quantity: 1 },
      });
      await load();
      await onUserUpdate?.();
    } catch (e) {
      alert(e.response?.data?.message || e.message);
    }
  };

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", gap: 12, flexWrap: "wrap" }}>
        <h1 style={{ marginTop: 0 }}>Listings</h1>
        <Link to="/new-listing">+ New listing</Link>
      </div>

      <div style={{ display: "flex", gap: 12, alignItems: "center", margin: "12px 0 18px" }}>
        <input
          placeholder="Search by card name…"
          value={q}
          onChange={(e) => setQ(e.target.value)}
          style={{ padding: "8px 10px", flex: "1 1 320px" }}
        />
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="ACTIVE">Active</option>
          <option value="SOLD">Sold</option>
        </select>
      </div>

      {loading && <p>Loading…</p>}
      {err && <p style={{ color: "crimson" }}>Error: {err}</p>}

      {!loading && !err && (
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))", gap: 14 }}>
          {items.map((l) => (
            <article key={l.id} style={{ border: "1px solid #eee", borderRadius: 12, padding: 14 }}>
              <h3 style={{ margin: 0 }}>{l.card?.name || "—"}</h3>
              <p style={{ margin: "6px 0 0", color: "#666" }}>
                Seller: <b>{l.sellerName}</b>
              </p>
              <p style={{ margin: "6px 0 0", color: "#666" }}>
                Price: <b>{money(l.price)}</b> credits • Qty: <b>{l.quantity}</b> • Condition: {l.condition || "—"}
              </p>

              <div style={{ display: "flex", gap: 10, marginTop: 12, flexWrap: "wrap" }}>
                <button
                  onClick={() => buy(l.id)}
                  disabled={l.status !== "ACTIVE" || l.quantity < 1 || l.sellerName?.toLowerCase() === currentUser?.username?.toLowerCase()}
                >
                  Buy (1)
                </button>
                <Link to={`/trades?target=${l.id}`}>Offer trade</Link>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
