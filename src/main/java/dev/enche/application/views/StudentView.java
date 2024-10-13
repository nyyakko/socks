package dev.enche.application.views;

public class StudentView {
    private String name;
    private String school;

    public StudentView() {}

    public StudentView(String name, String school) {
        this.name = name;
        this.school = school;
    }

    public String getName() { return name; }
    public String getSchool() { return school; }
}
