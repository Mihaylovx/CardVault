import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { api } from "../app/api";

function money(n) {
  if (n == null) return "—";
  return Number(n).toFixed(2);
}

export default function MyListingsPage({ currentUser, onUserUpdate }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState(null);

  const load = async () => {
    const { data } = await api.get("/listings", { params: { seller: currentUser?.username?.toLowerCase() } });
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
  }, [currentUser]);

  const remove = async (id) => {
    if (!confirm("Delete this listing?")) return;
    try {
      await api.delete(`/listings/${id}`);
      await load();
      await onUserUpdate?.();
    } catch (e) {
      alert(e.response?.data?.message || e.message);
    }
  };

  const markSold = async (id) => {
    try {
      await api.put(`/listings/${id}/status`, null, { params: { status: "SOLD" } });
      await load();
    } catch (e) {
      alert(e.response?.data?.message || e.message);
    }
  };

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", gap: 12, flexWrap: "wrap" }}>
        <h1 style={{ marginTop: 0 }}>My listings</h1>
        <Link to="/new-listing">+ New listing</Link>
      </div>

      {loading && <p>Loading…</p>}
      {err && <p style={{ color: "crimson" }}>Error: {err}</p>}

      {!loading && !err && items.length === 0 && <p>You have no listings yet.</p>}

      {!loading && !err && items.length > 0 && (
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))", gap: 14 }}>
          {items.map((l) => (
            <article key={l.id} style={{ border: "1px solid #eee", borderRadius: 12, padding: 14 }}>
              <h3 style={{ margin: 0 }}>{l.card?.name || "—"}</h3>
              <p style={{ margin: "6px 0 0", color: "#666" }}>
                Price: <b>{money(l.price)}</b> • Qty: <b>{l.quantity}</b> • Status: <b>{l.status}</b>
              </p>
              <p style={{ margin: "6px 0 0", color: "#666" }}>Condition: {l.condition || "—"}</p>

              <div style={{ display: "flex", gap: 10, marginTop: 12, flexWrap: "wrap" }}>
                <button onClick={() => markSold(l.id)} disabled={l.status !== "ACTIVE"}>
                  Mark sold
                </button>
                <button onClick={() => remove(l.id)}>Delete</button>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
