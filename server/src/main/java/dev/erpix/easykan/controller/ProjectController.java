package dev.erpix.easykan.controller;

import dev.erpix.easykan.model.EKUserDetails;
import dev.erpix.easykan.model.project.EKProject;
import dev.erpix.easykan.model.project.dto.ProjectCreateRequestDto;
import dev.erpix.easykan.model.project.dto.ProjectResponseDto;
import dev.erpix.easykan.service.ProjectService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Projects",
        description = "Project related operations")
@RestController
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getCurrentUserProjects(@AuthenticationPrincipal EKUserDetails userDetails) {
        UUID userId = userDetails.getUser().getId();
        List<ProjectResponseDto> projects = projectService.getUserProjects(userId).stream()
                .map(ProjectResponseDto::fromProject)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(
            @AuthenticationPrincipal EKUserDetails userDetails,
            @RequestBody @Valid ProjectCreateRequestDto requestDto
    ) {
        UUID userId = userDetails.getUser().getId();
        EKProject project = projectService.createProject(userId, requestDto);
        ProjectResponseDto responseDto = ProjectResponseDto.fromProject(project);
        return ResponseEntity.ok(responseDto);
    }

}
