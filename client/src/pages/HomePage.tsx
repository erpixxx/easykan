import "../scss/home.scss";
import { BoardNav } from "../components/BoardNav.tsx";
import { BoardView } from "../components/BoardView.tsx";
import {BoardViewNav} from "../components/BoardViewNav.tsx";

export function HomePage() {
    return (
      <div className="home">
        <header className="home-header">
          <BoardNav />
          <BoardViewNav />
        </header>
        <BoardView />
      </div>
    );
}
