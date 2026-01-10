import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../app/api";

export default function NewListingPage({ currentUser, onUserUpdate }) {
  const nav = useNavigate();
  const [cards, setCards] = useState([]);
  const [cardId, setCardId] = useState("");
  const [price, setPrice] = useState("20.00");
  const [quantity, setQuantity] = useState(1);
  const [condition, setCondition] = useState("NM");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const { data } = await api.get("/cards");
        if (!cancelled) {
          setCards(data);
          if (!cardId && data?.[0]?.id) setCardId(String(data[0].id));
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    try {
      await api.post("/listings", {
        cardId: Number(cardId),
        sellerUsername: currentUser,
        price: Number(price),
        quantity: Number(quantity),
        condition,
      });
      await onUserUpdate?.();
      nav("/my-listings");
    } catch (err) {
      alert(err.response?.data?.message || err.message);
    }
  };

  return (
    <div style={{ maxWidth: 640 }}>
      <h1 style={{ marginTop: 0 }}>New listing</h1>
      {loading ? (
        <p>Loading…</p>
      ) : (
        <form onSubmit={submit} style={{ display: "grid", gap: 12 }}>
          <label>
            Card
            <select value={cardId} onChange={(e) => setCardId(e.target.value)} style={{ width: "100%", padding: 8 }}>
              {cards.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} ({c.setName})
                </option>
              ))}
            </select>
          </label>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            <label>
              Price (credits)
              <input value={price} onChange={(e) => setPrice(e.target.value)} style={{ width: "100%", padding: 8 }} />
            </label>
            <label>
              Quantity
              <input
                type="number"
                min={1}
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
                style={{ width: "100%", padding: 8 }}
              />
            </label>
          </div>

          <label>
            Condition
            <input value={condition} onChange={(e) => setCondition(e.target.value)} style={{ width: "100%", padding: 8 }} />
          </label>

          <button type="submit">Create listing</button>
        </form>
      )}
    </div>
  );
}
