package com.example.e_advisor.response_objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class MaterialResponse {
    private boolean success;
    private List<Material> materials;

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public static class Material implements Parcelable {
        private String _id;
        private String title;
        private String description;
        private String type;
        private String url;
        private String course;

        protected Material(Parcel in) {
            _id = in.readString();
            title = in.readString();
            description = in.readString();
            type = in.readString();
            url = in.readString();
            course = in.readString();
        }

        public static final Creator<Material> CREATOR = new Creator<Material>() {
            @Override
            public Material createFromParcel(Parcel in) {
                return new Material(in);
            }

            @Override
            public Material[] newArray(int size) {
                return new Material[size];
            }
        };

        // Getters and Setters
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCourse() {
            return course;
        }

        public void setCourse(String course) {
            this.course = course;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeString(_id);
            parcel.writeString(title);
            parcel.writeString(description);
            parcel.writeString(type);
            parcel.writeString(url);
            parcel.writeString(course);
        }

        @NonNull
        @Override
        public String toString() {
            return "Material{" +
                    "_id='" + _id + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    ", course='" + course + '\'' +
                    '}';
        }
    }
}

