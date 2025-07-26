export function BoardViewNav() {
  return (
    <nav className="header-board-view-nav">
      <div className="board-view-nav-option">
        <button className="board-view-nav-option--btn">
          Create new column
        </button>
      </div>
      <div className="board-view-nav-option">
        <button className="board-view-nav-option--btn">
          Member:
          <span className="board-view-nav-option--btn--selected">
            erpix
          </span>
        </button>
      </div>
      <div className="board-view-nav-option">
        <button className="board-view-nav-option--btn">
          Tags:
          <span className="board-view-nav-option--btn--selected">
            all
          </span>
        </button>
      </div>
      <div className="board-view-nav-option">
        <button className="board-view-nav-option--btn">
          <span className="material-icons-outlined">edit</span>
        </button>
      </div>
    </nav>
  );
}