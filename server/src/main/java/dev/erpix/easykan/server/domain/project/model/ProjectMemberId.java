package dev.erpix.easykan.server.domain.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProjectMemberId implements Serializable {

	@NotNull
	@Column(name = "project_id", nullable = false)
	private UUID projectId;

	@NotNull
	@Column(name = "user_id", nullable = false)
	private UUID userId;

}
