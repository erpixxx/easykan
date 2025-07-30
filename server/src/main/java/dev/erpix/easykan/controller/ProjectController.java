package dev.erpix.easykan.controller;

import dev.erpix.easykan.model.project.dto.ProjectCreateRequestDto;
import dev.erpix.easykan.model.project.dto.ProjectResponseDto;
import dev.erpix.easykan.model.project.EKProject;
import dev.erpix.easykan.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(Authentication auth, @RequestBody ProjectCreateRequestDto request) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        EKProject project = projectService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
        if (projectService.deleteProject(projectId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getUserProjects(Authentication auth) {
        UUID userId = UUID.fromString((String) auth.getPrincipal());
        List<ProjectResponseDto> projects = projectService.getUserProjects(userId).stream()
                .map(projectService::toDto)
                .toList();
        return ResponseEntity.ok(projects);
    }

//    @PostMapping
//    public ResponseEntity<ProjectResponse> createProject(Authentication auth, @RequestBody CreateProjectRequest request) {
//        UUID userId = UUID.fromString((String) auth.getPrincipal());
//        EKProject project = projectService.createProject(userId, request.getName());
//        ProjectResponse response = projectService.toDto(project);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

}
