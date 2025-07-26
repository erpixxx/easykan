import "../scss/components/IconButton.scss"

export interface IconButtonProps {
  icon: string;
  onClick?: () => void;
}

export function IconButton({ icon, onClick }: IconButtonProps) {
  return (
    <button className="icon-btn" onClick={onClick}>
      <span className="icon-btn--icon material-icons-outlined">{icon}</span>
    </button>
  );
}