package com.example.admin_backend.dto;

public class ReportDTO {

        private String description;
        private String location;
        private String image;
        private int userId;
    
        // Getters and Setters
        public String getDescription() {
            return description;
        }
    
        public void setDescription(String description) {
            this.description = description;
        }
    
        public String getLocation() {
            return location;
        }
    
        public void setLocation(String location) {
            this.location = location;
        }
    
        public String getImage() {
            return image;
        }
    
        public void setImage(String image) {
            this.image = image;
        }
    
        public int getUserId() {
            return userId;
        }
    
        public void setUserId(int userId) {
            this.userId = userId;
        }
}


