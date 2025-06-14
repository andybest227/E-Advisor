package com.example.e_advisor.response_objects;

import java.util.List;

public class CoursesApiResponse {
    private List<CoursesResponse> coursesResponse;

    // Getters and setters
    public List<CoursesResponse> getCoursesResponse() {
        return coursesResponse;
    }

    public void setCoursesResponse(List<CoursesResponse> coursesResponse) {
        this.coursesResponse = coursesResponse;
    }

    public static class CoursesResponse {
        private List<String> uniquecourses;

        // Getters and setters
        public List<String> getUniquecourses() {
            return uniquecourses;
        }

        public void setUniquecourses(List<String> uniquecourses) {
            this.uniquecourses = uniquecourses;
        }
    }
}