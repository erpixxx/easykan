import type { UserSummaryDto } from "../UserSummaryDto.ts";

export interface ProjectListItem {
  id: string;
  name: string;
  members: UserSummaryDto[];
}
