package dev.erpix.easykan.server.domain.project.security;

import dev.erpix.easykan.server.domain.PermissionUtils;
import dev.erpix.easykan.server.domain.project.model.ProjectPermission;
import dev.erpix.easykan.server.domain.project.service.ProjectPermissionService;
import dev.erpix.easykan.server.domain.user.model.User;
import dev.erpix.easykan.server.domain.user.model.UserPermission;
import dev.erpix.easykan.server.domain.user.util.UserDetailsProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class RequireProjectPermissionAspect {

	private final ProjectPermissionService projectPermissionService;

	private final UserDetailsProvider userDetailsProvider;

	@Before("@annotation(requireProjectPermission)")
	public void checkPermission(JoinPoint joinPoint, RequireProjectPermission requireProjectPermission) {
		ProjectPermission[] requiredPermissions = requireProjectPermission.value();

		User user = userDetailsProvider.getRequiredCurrentUserDetails().user();

		// Admins bypass permission checks
		if (PermissionUtils.hasPermission(user.getPermissions(), UserPermission.ADMIN)) {
			return;
		}

		UUID userId = user.getId();
		UUID projectId = findProjectId(joinPoint);
		long memberPerms = projectPermissionService.getPermissionMaskForUserInProject(projectId, userId);

		// Owners bypass permission checks
		if (PermissionUtils.hasPermission(memberPerms, ProjectPermission.OWNER)) {
			return;
		}

		if (!PermissionUtils.hasPermission(memberPerms, requiredPermissions)) {
			throw new AccessDeniedException(requireProjectPermission.message());
		}
	}

	private static UUID findProjectId(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Object[] args = joinPoint.getArgs();

		for (int i = 0; i < parameterAnnotations.length; i++) {
			for (Annotation annotation : parameterAnnotations[i]) {
				if (annotation instanceof ProjectId) {
					if (args[i] instanceof UUID) {
						return (UUID) args[i];
					}
					else {
						throw new IllegalArgumentException("Parameter annotated with @ProjectId must be of type UUID.");
					}
				}
			}
		}

		throw new IllegalStateException(
				"Missing @ProjectId annotation on a method parameter for a method protected by @RequireProjectPermission.");
	}

}
