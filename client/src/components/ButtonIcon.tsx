import "../scss/components/ButtonIcon.scss"

type ButtonIconProps = {
  icon: string,
  label?: string,
  className?: string
  onClick?: () => void;
}

export function ButtonIcon({icon, label, className = "", onClick}: ButtonIconProps) {
  return (
    <button className={`btn-transparent btn-icon ${className}`} onClick={onClick}>
      <span className="material-icons">{icon}</span>
      {label && <span className="button-label">{label}</span>}
    </button>
  );
}