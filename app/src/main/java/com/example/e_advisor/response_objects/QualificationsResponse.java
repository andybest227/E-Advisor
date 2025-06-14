package com.example.e_advisor.response_objects;

import java.util.List;

public class QualificationsResponse {

    private List<Qualifications> qualifications;

    public List<Qualifications> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<Qualifications> qualifications) {
        this.qualifications = qualifications;
    }

    public static class Qualifications {
        private List<String> uniquequalifications;

        public List<String> getUniquequalifications() {
            return uniquequalifications;
        }

        public void setUniqueCourses(List<String> uniqueCourses) {
            this.uniquequalifications = uniqueCourses;
        }
    }
}
