import type { ProjectCreateDto } from "../types/dto/project/ProjectCreateDto.ts";
import type { ProjectListItem } from "../types/dto/project/ProjectListItem.ts";
import api from "./axiosInstance.ts";

export const create = async (
  payload: ProjectCreateDto,
): Promise<ProjectListItem> => {
  console.log(payload);
  const res = await api.post("/projects", payload);
  return res.data;
};

export const getProjectList = async (): Promise<ProjectListItem[]> => {
  const res = await api.get("/projects");
  return res.data;
};
