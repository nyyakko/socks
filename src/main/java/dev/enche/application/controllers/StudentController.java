package dev.enche.application.controllers;

import dev.enche.application.entities.Student;
import dev.enche.application.repositories.StudentRepository;
import dev.enche.application.web.HttpRequest;

import java.util.List;
import java.util.UUID;

public class StudentController {

    private final StudentRepository repository = new StudentRepository();

    public List<Student> list(HttpRequest request) {
        return repository.list();
    }

    public Student find(HttpRequest request) {
        return repository.find(UUID.fromString(request.getPathParam("id"))).orElseThrow(() -> new RuntimeException("NOT FOUND"));
    }

    public Void save(HttpRequest request) {
        repository.save((Student)request.getBody());
        return null;
    }

    public Void delete(HttpRequest request) {
        repository.delete(UUID.fromString(request.getPathParam("id")));
        return null;
    }

}
