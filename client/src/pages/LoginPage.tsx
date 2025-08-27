import { LoginCard } from "../components/features/login/LoginCard.tsx";
import {PageTransition} from "../components/PageTransition.tsx";
import "../scss/login.scss"

export function LoginPage() {
  return(
    <PageTransition>
      <div className="login-page">
        <LoginCard />
      </div>
    </PageTransition>
  );
}