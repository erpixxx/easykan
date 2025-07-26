import logo from '../assets/EasyKan.svg';

export function Logo() {
  return (
    <svg className="ek-brand-logo" viewBox="0 0 100 100">
      <path d={logo} />
    </svg>
  );
}