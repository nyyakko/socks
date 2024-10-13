package dev.enche.application.entities.relations;

import dev.enche.persistence.Identifiable;

import java.util.UUID;

/*
    Many (Student) to One (School)
*/
public class StudentToSchool extends Identifiable {
    private UUID schoolId;
    private UUID studentId;

    public StudentToSchool() {}

    public StudentToSchool(UUID schoolId, UUID studentId) {
        this.schoolId = schoolId;
        this.studentId = studentId;
    }

    public UUID getSchoolId() { return schoolId; }
    public UUID getStudentId() { return studentId; }
}
