package dev.enche.application.dto;

import java.util.UUID;

public class StudentDTO {
    private UUID id;

    StudentDTO() {}

    StudentDTO(UUID id) {
        this.id = id;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
}
