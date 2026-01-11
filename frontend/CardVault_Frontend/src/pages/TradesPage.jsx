import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { api } from "../app/api";

export default function TradesPage({ currentUser, onUserUpdate }) {
  const [searchParams] = useSearchParams();
  const targetFromUrl = searchParams.get("target");

  const [availableTargets, setAvailableTargets] = useState([]);
  const [myListings, setMyListings] = useState([]);
  const [targetListingId, setTargetListingId] = useState(targetFromUrl || "");
  const [selectedOffered, setSelectedOffered] = useState([]);
  const [inbox, setInbox] = useState([]);
  const [sent, setSent] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadAll = async () => {
    const [targetsRes, mineRes, inboxRes, sentRes] = await Promise.all([
      api.get("/listings", { params: { status: "ACTIVE" } }),
      api.get("/listings", { params: { seller: currentUser?.username } }),
      api.get("/trades/inbox", { params: { userId: currentUser?.id } }),
      api.get("/trades/sent", { params: { userId: currentUser?.id } }),
    ]);

    const allActive = targetsRes.data || [];
    setAvailableTargets(allActive.filter((l) => l.sellerName?.toLowerCase() !== currentUser?.username?.toLowerCase()));
    setMyListings((mineRes.data || []).filter((l) => l.status === "ACTIVE" && l.quantity > 0));
    setInbox(inboxRes.data || []);
    setSent(sentRes.data || []);
  };

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      try {
        await loadAll();
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [currentUser]);

  useEffect(() => {
    if (targetFromUrl) setTargetListingId(targetFromUrl);
  }, [targetFromUrl]);

  const canSubmit = useMemo(
    () => targetListingId && selectedOffered.length > 0,
    [targetListingId, selectedOffered]
  );

  const toggleOffered = (id) => {
    setSelectedOffered((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const submit = async (e) => {
    e.preventDefault();

    const payload = {
      fromUserId: currentUser.id,
      targetListingId: Number(targetListingId),
      offeredListingIds: selectedOffered.map((id) => Number(id)),
    };

    console.log("POST /api/trades payload:", payload);

    try {
      await api.post("/trades", payload);
      setSelectedOffered([]);
      await loadAll();
      alert("Trade offer sent!");
    } catch (err) {
      console.error("Trade error:", err.response?.data || err);
      alert(JSON.stringify(err.response?.data));
    }
  };

  const accept = async (id) => {
    try {
      await api.put(`/trades/${id}/accept`, null, { params: { userId: currentUser?.id } });
      await loadAll();
      await onUserUpdate?.();
    } catch (err) {
      alert(err.response?.data?.message || err.message);
    }
  };

  const reject = async (id) => {
    try {
      await api.put(`/trades/${id}/reject`, null, { params: { userId: currentUser?.id } });
      await loadAll();
    } catch (err) {
      alert(err.response?.data?.message || err.message);
    }
  };

  return (
    <div>
      <h1 style={{ marginTop: 0 }}>Trades</h1>

      {loading ? (
        <p>Loading…</p>
      ) : (
        <>
          <section style={{ border: "1px solid #eee", borderRadius: 12, padding: 14, marginBottom: 18 }}>
            <h2 style={{ marginTop: 0, fontSize: 18 }}>Create a trade offer</h2>

            <form onSubmit={submit} style={{ display: "grid", gap: 12 }}>
              <label>
                Target listing (someone else)
                <select
                  value={targetListingId}
                  onChange={(e) => setTargetListingId(e.target.value)}
                  style={{ width: "100%", padding: 8 }}
                >
                  <option value="">Select a listing…</option>
                  {availableTargets.map((l) => (
                    <option key={l.id} value={l.id}>
                      #{l.id} — {l.card?.name} (seller: {l.sellerName})
                    </option>
                  ))}
                </select>
              </label>

              <div>
                <div style={{ marginBottom: 6 }}>Offer from your listings</div>
                {myListings.length === 0 ? (
                  <div style={{ color: "#666" }}>You have no active listings to offer.</div>
                ) : (
                  <div style={{ display: "grid", gap: 6 }}>
                    {myListings.map((l) => (
                      <label key={l.id} style={{ display: "flex", gap: 8, alignItems: "center" }}>
                        <input
                          type="checkbox"
                          checked={selectedOffered.includes(l.id)}
                          onChange={() => toggleOffered(l.id)}
                        />
                        #{l.id} — {l.card?.name} (qty: {l.quantity})
                      </label>
                    ))}
                  </div>
                )}
              </div>

              <button type="submit" disabled={!canSubmit}>
                Send trade offer
              </button>
            </form>
          </section>

          <section style={{ marginBottom: 22 }}>
            <h2 style={{ fontSize: 18 }}>Inbox (offers to you)</h2>
            {inbox.length === 0 ? (
              <p style={{ color: "#666" }}>No offers yet.</p>
            ) : (
              <div style={{ display: "grid", gap: 10 }}>
                {inbox.map((t) => (
                  <article key={t.id} style={{ border: "1px solid #eee", borderRadius: 12, padding: 14 }}>
                    <div style={{ display: "flex", justifyContent: "space-between", gap: 12, flexWrap: "wrap" }}>
                      <div>
                        <b>#{t.id}</b> — from <b>{t.fromUser?.username}</b> — status <b>{t.status}</b>
                      </div>
                      {t.status === "PENDING" && (
                        <div style={{ display: "flex", gap: 8 }}>
                          <button onClick={() => accept(t.id)}>Accept</button>
                          <button onClick={() => reject(t.id)}>Reject</button>
                        </div>
                      )}
                    </div>

                    <div style={{ marginTop: 8, color: "#555" }}>
                      They want: <b>#{t.targetListing?.id}</b> — {t.targetListing?.card?.name}
                    </div>
                    <div style={{ marginTop: 6, color: "#555" }}>
                      They offer:
                      <ul style={{ margin: "6px 0 0 18px" }}>
                        {t.offeredListings?.map((l) => (
                          <li key={l.id}>
                            #{l.id} — {l.card?.name}
                          </li>
                        ))}
                      </ul>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </section>

          <section>
            <h2 style={{ fontSize: 18 }}>Sent</h2>
            {sent.length === 0 ? (
              <p style={{ color: "#666" }}>No sent trades yet.</p>
            ) : (
              <div style={{ display: "grid", gap: 10 }}>
                {sent.map((t) => (
                  <article key={t.id} style={{ border: "1px solid #eee", borderRadius: 12, padding: 14 }}>
                    <div>
                      <b>#{t.id}</b> — to <b>{t.toUser?.username}</b> — status <b>{t.status}</b>
                    </div>
                    <div style={{ marginTop: 8, color: "#555" }}>
                      You want: <b>#{t.targetListing?.id}</b> — {t.targetListing?.card?.name}
                    </div>
                    <div style={{ marginTop: 6, color: "#555" }}>
                      You offered:
                      <ul style={{ margin: "6px 0 0 18px" }}>
                        {t.offeredListings?.map((l) => (
                          <li key={l.id}>
                            #{l.id} — {l.card?.name}
                          </li>
                        ))}
                      </ul>
                    </div>
                    {t.status === "PENDING" && (
                      <div style={{ marginTop: 10 }}>
                        <button onClick={() => reject(t.id)}>Cancel (reject)</button>
                      </div>
                    )}
                  </article>
                ))}
              </div>
            )}
          </section>
        </>
      )}
    </div>
  );
}
