// src/pages/CardsPage.jsx
import { useEffect, useMemo, useState } from "react";
import { api } from "../app/api";

export default function CardsPage() {
  const [cards, setCards] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState(null);

  const [q, setQ] = useState("");
  const params = useMemo(() => {
    const p = {};
    if (q.trim()) p.q = q.trim();
    return p;
  }, [q]);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      setErr(null);
      try {
        const { data } = await api.get("/cards", { params });
        if (cancelled) return;
        setCards(Array.isArray(data) ? data : (data.content ?? []));
      } catch (e) {
        if (!cancelled) setErr(e.response?.data?.message || e.message);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [params]);
8
  return (
    <div style={{ maxWidth: 1100, margin: "32px auto", padding: "0 16px", fontFamily: "system-ui, sans-serif" }}>
      <h1>Cards</h1>

      <div style={{ display: "flex", gap: 12, alignItems: "center", marginBottom: 16 }}>
        <input
          placeholder="Search by name…"
          value={q}
          onChange={(e) => setQ(e.target.value)}
          style={{ padding: "8px 10px", flex: "1 1 300px" }}
        />
      </div>

      {loading && <p>Loading…</p>}
      {err && <p style={{ color: "crimson" }}>Error: {err}</p>}

      {!loading && !err && (
        <>
          <p style={{ color: "#555" }}>{cards.length} result{cards.length === 1 ? "" : "s"}</p>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))", gap: 16 }}>
            {cards.map(c => (
              <article key={c.id} style={{ border: "1px solid #eee", borderRadius: 12, padding: 14 }}>
                <h3 style={{ margin: 0 }}>{c.name}</h3>
                <p style={{ margin: "6px 0 0 0", color: "#666" }}>
                  {(c.setName || "—")} • {(c.rarity || "—")} • Year: {c.releaseYear}
                </p>
              </article>
            ))}
          </div>

        </>
      )}
    </div>
  );
}
