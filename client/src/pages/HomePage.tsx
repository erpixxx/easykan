import "../scss/home.scss";
import { BoardNav } from "../components/features/board/BoardNav.tsx";
import { BoardViewNav } from "../components/features/board/BoardViewNav.tsx";

export function HomePage() {
  return (
    <div className="home">
      <header className="home-header">
        <BoardNav />
        <BoardViewNav />
      </header>
      {/*<BoardView />*/}
    </div>
  );
}
