import { LoginCard } from "../components/LoginCard.tsx";
import "../scss/login.scss"

export function LoginPage() {
  return(
    <div className="login-page">
      <LoginCard />
    </div>
  );
}