package dev.enche.application.repositories;

import dev.enche.application.entities.School;
import dev.enche.application.entities.Student;
import dev.enche.application.persistence.StudentPersistence;
import dev.enche.persistence.core.Query;
import dev.enche.web.core.HttpQueryParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class StudentRepository {

    public static final StudentPersistence persistence = new StudentPersistence();

    private Query<Student> handleQueryParams(Query<Student> query, Map<String, HttpQueryParam> queryParams) {
        var result = query;

        if (queryParams.containsKey("name")) {
            final var param = queryParams.get("name");
            result = result.is(name -> name.toUpperCase().contains(param.getValue().toUpperCase()), Student::getName);
        }

        return result;
    }
    public List<Student> list(Map<String, HttpQueryParam> queryParams) {
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
