package dev.erpix.easykan.model.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class ProjectResponseDTO {

    private UUID id;
    private String name;
    private UUID owner;

}
