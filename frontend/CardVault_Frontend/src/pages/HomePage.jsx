import { Link } from "react-router-dom";

export default function HomePage() {
  return (
    <div>
      <h1 style={{ marginTop: 0 }}>One Piece TCG Marketplace (Demo)</h1>
      <p style={{ color: "#555", maxWidth: 720 }}>
        Simple marketplace features: browse listings, create your own listing, buy with
        fake credits, and trade cards with other users.
      </p>

      <div style={{ display: "flex", gap: 10, flexWrap: "wrap", marginTop: 14 }}>
        <Link to="/listings">Browse listings →</Link>
        <Link to="/new-listing">Create a listing →</Link>
        <Link to="/trades">Trades →</Link>
      </div>
    </div>
  );
}
