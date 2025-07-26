/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_ENABLE_DISCORD_LOGIN: string;
  readonly VITE_ENABLE_GITHUB_LOGIN: string;
  readonly VITE_ENABLE_GITLAB_LOGIN: string;
}