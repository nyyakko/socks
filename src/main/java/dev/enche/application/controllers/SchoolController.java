package dev.enche.application.controllers;

import dev.enche.application.dto.StudentDTO;
import dev.enche.application.entities.School;
import dev.enche.application.entities.Student;
import dev.enche.application.repositories.SchoolRepository;
import dev.enche.web.core.HttpRequest;

import java.util.List;
import java.util.UUID;

public class SchoolController {

    private final SchoolRepository repository = new SchoolRepository();

    public List<School> list(HttpRequest request) {
        return repository.list();
    }

    public School find(HttpRequest request) {
        return repository.find(UUID.fromString(request.getPathParam("id"))).orElseThrow(() -> new RuntimeException("NOT FOUND"));
    }

    public Void save(HttpRequest request) {
        repository.save((School)request.getBody());
        return null;
    }

    public Void delete(HttpRequest request) {
        repository.delete(UUID.fromString(request.getPathParam("id")));
        return null;
    }

    public List<Student> listStudents(HttpRequest request) {
        final var companyId = UUID.fromString(request.getPathParam("schoolId"));
        if (repository.find(companyId).isEmpty()) {
            throw new RuntimeException("NOT FOUND");
        }
        return repository.listStudents(companyId);
    }

    public Student findStudent(HttpRequest request) {
        final var companyId = UUID.fromString(request.getPathParam("schoolId"));
        if (repository.find(companyId).isEmpty()) {
            throw new RuntimeException("NOT FOUND");
        }
        final var workerId = UUID.fromString(request.getPathParam("studentId"));
        return repository.findStudent(companyId, workerId).orElseThrow(() -> new RuntimeException("NOT FOUND"));
    }

    public Void saveStudent(HttpRequest request) {
        final var companyId = UUID.fromString(request.getPathParam("schoolId"));
        if (repository.find(companyId).isEmpty()) {
            throw new RuntimeException("NOT FOUND");
        }
        final var workerId = ((StudentDTO)request.getBody()).getId();
        repository.saveStudent(companyId, workerId);
        return null;
    }

    public Void deleteStudent(HttpRequest request) {
        final var companyId = UUID.fromString(request.getPathParam("schoolId"));
        if (repository.find(companyId).isEmpty()) {
            throw new RuntimeException("NOT FOUND");
        }
        final var workerId = UUID.fromString(request.getPathParam("studentId"));
        repository.deleteStudent(companyId, workerId);
        return null;
    }
}
