import {IconButton} from "../../IconButton.tsx";

export function BoardNav() {
  return (
    <nav className="header-boards-nav">
      <div className="boards-list">
        <SingleBoard name="Board 1" classes="active" />
        <SingleBoard name="First Board" />
        <SingleBoard name="Second Board" />
      </div>
      <div className="mobile-menu">
        <IconButton icon="menu" />
      </div>
      <IconButton icon="settings" />
    </nav>
  );
}

export function SingleBoard({ name, classes }: { name: string, classes?: string }) {
  return (
    <button className={`boards-single-board ${classes || ''}`}>
      {name}
      <IconButton icon="close" />
    </button>
  );
}