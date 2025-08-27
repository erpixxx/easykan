export interface User {
  id: string;
  login: string;
  displayName: string;
  email: string;
  permissions: number;
}

export const hasPermission = (user: User | null, perm: number) => {
  if (!user) return false;
  return (user.permissions & perm) !== 0;
};