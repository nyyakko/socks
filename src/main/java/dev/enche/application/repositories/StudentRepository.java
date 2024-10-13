package dev.enche.application.repositories;

import dev.enche.application.entities.Student;
import dev.enche.application.persistence.StudentPersistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StudentRepository {

    public static final StudentPersistence persistence = new StudentPersistence();

    public List<Student> list() {
        return persistence.selectAny().getAll();
    }

    public Optional<Student> find(UUID id) {
        return persistence.select(ctx -> ctx.where(query -> query.is(id))).getSingle();
    }

    public void save(Student person) {
        persistence.insert(person);
    }

    public void delete(UUID id) {
        persistence.delete(id);
    }

}
