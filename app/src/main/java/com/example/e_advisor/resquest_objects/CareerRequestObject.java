package com.example.e_advisor.resquest_objects;

public class CareerRequestObject {
    String courseOfStudy, qualification;

    public CareerRequestObject(String courseOfStudy, String qualification) {
        this.courseOfStudy = courseOfStudy;
        this.qualification = qualification;
    }

    public String getCourseOfStudy() {
        return courseOfStudy;
    }

    public void setCourseOfStudy(String courseOfStudy) {
        this.courseOfStudy = courseOfStudy;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
}
