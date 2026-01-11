import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { api } from "../app/api";

export default function RegisterPage({ onAuth }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false);
  const nav = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    setBusy(true);
    try {
      const { data } = await api.post("/auth/register", { username, password });
      localStorage.setItem("cv_auth", JSON.stringify(data));
      onAuth?.(data);
      nav("/listings");
    } catch (err) {
      alert(err.response?.data?.message || err.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <div style={{ maxWidth: 460 }}>
      <h1 style={{ marginTop: 0 }}>Register</h1>
      <form onSubmit={submit} style={{ display: "grid", gap: 10 }}>
        <label style={{ display: "grid", gap: 6 }}>
          <span>Username</span>
          <input value={username} onChange={(e) => setUsername(e.target.value)} required style={{ padding: "8px 10px" }} />
        </label>
        <label style={{ display: "grid", gap: 6 }}>
          <span>Password</span>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required style={{ padding: "8px 10px" }} />
        </label>
        <button disabled={busy} style={{ padding: "10px 12px", fontWeight: 700 }}>
          {busy ? "Creating…" : "Create account"}
        </button>
      </form>

      <p style={{ marginTop: 14 }}>
        Already have an account? <Link to="/login">Login</Link>
      </p>
    </div>
  );
}
