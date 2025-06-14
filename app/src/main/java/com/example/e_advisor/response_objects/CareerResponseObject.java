package com.example.e_advisor.response_objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class CareerResponseObject {
    private boolean success;
    private List<Careers> careers;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Careers> getCareers() {
        return careers;
    }

    public void setCareers(List<Careers> careers) {
        this.careers = careers;
    }

    public static class Careers implements Parcelable {
        private String _id;
        private String title;
        private String description;
        private String[] qualifications;
        private String[] relatedCourses;
        private String[] tips;
        private String[] tags;

        public Careers() {}

        protected Careers(Parcel in) {
            _id = in.readString();
            title = in.readString();
            description = in.readString();
            qualifications = in.createStringArray();
            relatedCourses = in.createStringArray();
            tips = in.createStringArray();
            tags = in.createStringArray();
        }

        public static final Creator<Careers> CREATOR = new Creator<Careers>() {
            @Override
            public Careers createFromParcel(Parcel in) {
                return new Careers(in);
            }

            @Override
            public Careers[] newArray(int size) {
                return new Careers[size];
            }
        };

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String[] getQualifications() {
            return qualifications;
        }

        public void setQualifications(String[] qualifications) {
            this.qualifications = qualifications;
        }

        public String[] getRelatedCourses() {
            return relatedCourses;
        }

        public void setRelatedCourses(String[] relatedCourses) {
            this.relatedCourses = relatedCourses;
        }

        public String[] getTips() {
            return tips;
        }

        public void setTips(String[] tips) {
            this.tips = tips;
        }

        public String[] getTags() {
            return tags;
        }

        public void setTags(String[] tags) {
            this.tags = tags;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeString(_id);
            parcel.writeString(title);
            parcel.writeString(description);
            parcel.writeStringArray(qualifications);
            parcel.writeStringArray(relatedCourses);
            parcel.writeStringArray(tips);
            parcel.writeStringArray(tags);
        }

        @NonNull
        @Override
        public String toString() {
            return "Careers{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", qualifications=" + Arrays.toString(qualifications) +
                    ", relatedCourses=" + Arrays.toString(relatedCourses) +
                    ", tips=" + Arrays.toString(tips) +
                    ", tags=" + Arrays.toString(tags) +
                    '}';
        }
    }
}
