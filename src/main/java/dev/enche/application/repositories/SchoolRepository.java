package dev.enche.application.repositories;

import dev.enche.application.entities.School;
import dev.enche.application.entities.Student;
import dev.enche.application.entities.relations.StudentToSchool;
import dev.enche.application.persistence.SchoolPersistence;
import dev.enche.application.persistence.relations.StudentToSchoolPersistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SchoolRepository {

    private static final SchoolPersistence persistence = new SchoolPersistence();
    private static final StudentToSchoolPersistence studentSchoolPersistence = new StudentToSchoolPersistence();

    public List<School> list() {
        return persistence.selectAny().getAll();
    }

    public Optional<School> find(UUID id) {
        return
            persistence
                .select(ctx -> ctx
                    .where(query -> query
                        .is(id)
                    )
                )
                .getSingle()
            ;
    }

    public void save(School school) {
        persistence.insert(school);
    }

    public void delete(UUID id) {
        persistence.delete(id);
    }

    public List<Student> listStudents(UUID companyId) {
        return
            studentSchoolPersistence
                .select(ctx -> ctx
                    .whereThen(id -> id.equals(companyId), StudentToSchool::getSchoolId)
                )
                .getAll()
                .stream()
                .map(StudentToSchool::getStudentId)
                .map(foundId ->
                    StudentRepository.persistence
                        .select(ctx -> ctx
                            .whereThen(id -> id.equals(foundId), Student::getId)
                        )
                        .mustGetSingle()
                )
                .toList()
            ;
    }

    public Optional<Student> findStudent(UUID companyId, UUID workerId) {
        return
            studentSchoolPersistence
                .select(ctx -> ctx
                    .where(query -> query
                        .is(id -> id.equals(companyId), StudentToSchool::getSchoolId)
                        .is(id -> id.equals(workerId), StudentToSchool::getStudentId)
                    )
                )
                .getSingle()
                .map(StudentToSchool::getStudentId)
                .map(foundId ->
                    StudentRepository.persistence
                        .select(ctx -> ctx
                            .whereThen(id -> id.equals(foundId), Student::getId)
                        )
                        .mustGetSingle()
                )
            ;
    }

    public void saveStudent(UUID companyId, UUID workerId) {
        studentSchoolPersistence.insert(new StudentToSchool(companyId, workerId));
    }

    public void deleteStudent(UUID companyId, UUID workerId) {
        studentSchoolPersistence
            .select(ctx -> ctx
                .where(query -> query
                    .is(id -> id.equals(companyId), StudentToSchool::getSchoolId)
                    .is(id -> id.equals(workerId), StudentToSchool::getStudentId)
                )
            )
            .getSingle()
            .map(StudentToSchool::getId)
            .ifPresent(studentSchoolPersistence::delete)
        ;
    }
}
