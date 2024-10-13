package dev.enche.application.repositories;

import dev.enche.application.entities.School;
import dev.enche.application.entities.Student;
import dev.enche.application.entities.relations.StudentToSchool;
import dev.enche.application.persistence.SchoolPersistence;
import dev.enche.application.persistence.relations.StudentToSchoolPersistence;
import dev.enche.persistence.core.Query;
import dev.enche.web.core.HttpQueryParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SchoolRepository {

    private static final SchoolPersistence persistence = new SchoolPersistence();
    private static final StudentToSchoolPersistence studentSchoolPersistence = new StudentToSchoolPersistence();

    private Query<School> handleQueryParams(Query<School> query, Map<String, HttpQueryParam> queryParams) {
        var result = query;

        if (queryParams.containsKey("name")) {
            final var param = queryParams.get("name");
            result = result.is(name -> name.toUpperCase().contains(param.getValue().toUpperCase()), School::getName);
        }

        return result;
    }

    public List<School> list(Map<String, HttpQueryParam> queryParams) {
        return
            persistence
                .select(ctx -> ctx
                    .where(query ->
                        handleQueryParams(query, queryParams)
                    )
                )
                .getAll()
            ;
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

    public List<Student> listStudents(UUID schoolId) {
        return
            studentSchoolPersistence
                .select(ctx -> ctx
                    .whereThen(id -> id.equals(schoolId), StudentToSchool::getSchoolId)
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

    public Optional<Student> findStudent(UUID schoolId, UUID studentId) {
        return
            studentSchoolPersistence
                .select(ctx -> ctx
                    .where(query -> query
                        .is(id -> id.equals(schoolId), StudentToSchool::getSchoolId)
                        .is(id -> id.equals(studentId), StudentToSchool::getStudentId)
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

    public void saveStudent(UUID schoolId, UUID studentId) {
        studentSchoolPersistence.insert(new StudentToSchool(schoolId, studentId));
    }

    public void deleteStudent(UUID schoolId, UUID studentId) {
        studentSchoolPersistence
            .select(ctx -> ctx
                .where(query -> query
                    .is(id -> id.equals(schoolId), StudentToSchool::getSchoolId)
                    .is(id -> id.equals(studentId), StudentToSchool::getStudentId)
                )
            )
            .getSingle()
            .map(StudentToSchool::getId)
            .ifPresent(studentSchoolPersistence::delete)
        ;
    }
}
