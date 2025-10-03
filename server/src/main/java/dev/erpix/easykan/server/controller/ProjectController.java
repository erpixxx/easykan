package dev.erpix.easykan.server.controller;

import dev.erpix.easykan.server.domain.project.dto.ProjectCreateDto;
import dev.erpix.easykan.server.domain.project.dto.ProjectSummaryDto;
import dev.erpix.easykan.server.domain.project.service.ProjectService;
import dev.erpix.easykan.server.domain.user.security.JpaUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@PostMapping
	public ResponseEntity<ProjectSummaryDto> createProject(@AuthenticationPrincipal JpaUserDetails userDetails,
			@RequestBody @Valid ProjectCreateDto projectCreateDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(projectService.createProject(projectCreateDto, userDetails.user().getId()));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteProject(@AuthenticationPrincipal JpaUserDetails userDetails,
			@RequestParam("projectId") String projectId) {
		projectService.deleteProject(UUID.fromString(projectId), userDetails.user().getId());
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<List<ProjectSummaryDto>> getProjectsForCurrentUser(
			@AuthenticationPrincipal JpaUserDetails userDetails) {
		List<ProjectSummaryDto> projects = projectService.getProjectsForUser(userDetails.user().getId());
		return ResponseEntity.ok(projects);
	}

	@GetMapping("/all")
	public ResponseEntity<Page<ProjectSummaryDto>> getAllProjects(@ParameterObject Pageable pageable) {
		Page<ProjectSummaryDto> projects = projectService.getAllProjects(pageable);
		return ResponseEntity.ok(projects);
	}

}
