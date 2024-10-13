package dev.enche.application;

import dev.enche.application.controllers.SchoolController;
import dev.enche.application.controllers.StudentController;
import dev.enche.application.dto.StudentDTO;
import dev.enche.application.entities.School;
import dev.enche.application.entities.Student;
import dev.enche.web.HttpRouter;
import dev.enche.web.core.enums.HttpMethod;

public class Main {

    public static void main(String[] args) {
        HttpRouter router = new HttpRouter();

        router.registerRouter(StudentController.class, "/v1/students", HttpMethod.GET, StudentController::list);
        router.registerRouter(StudentController.class, "/v1/students/{id}", HttpMethod.GET, StudentController::find);
        router.registerRouter(StudentController.class, "/v1/students", HttpMethod.POST, Student.class, StudentController::save);
        router.registerRouter(StudentController.class, "/v1/students/{id}", HttpMethod.DELETE, StudentController::delete);

        router.registerRouter(SchoolController.class, "/v1/schools", HttpMethod.GET, SchoolController::list);
        router.registerRouter(SchoolController.class, "/v1/schools/{id}", HttpMethod.GET, SchoolController::find);
        router.registerRouter(SchoolController.class, "/v1/schools", HttpMethod.POST, School.class, SchoolController::save);
        router.registerRouter(SchoolController.class, "/v1/schools/{id}", HttpMethod.DELETE, SchoolController::delete);
        router.registerRouter(SchoolController.class, "/v1/schools/{schoolId}/students", HttpMethod.GET, SchoolController::listStudents);
        router.registerRouter(SchoolController.class, "/v1/schools/{schoolId}/students/{studentId}", HttpMethod.GET, SchoolController::findStudent);
        router.registerRouter(SchoolController.class, "/v1/schools/{schoolId}/students", HttpMethod.POST, StudentDTO.class, SchoolController::saveStudent);

        router.routeAll();
    }

}
