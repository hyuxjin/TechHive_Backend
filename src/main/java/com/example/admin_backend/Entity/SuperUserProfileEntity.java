package com.example.admin_backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblsuperuserprofile")
public class SuperUserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "superuserprofile_id")
    private int superuserProfileId;

    @OneToOne
    @JoinColumn(name = "superuser_id", referencedColumnName = "superuserId") // Consistent naming with the DB column
    private SuperUserEntity superuser;

    @Lob
    @Column(name = "superuser_profile_picture", columnDefinition = "LONGBLOB") // Adding name to match the convention
    private byte[] superuserProfilePicture;

    // Default constructor (required by Hibernate)
    public SuperUserProfileEntity() {
    }

    // Constructor with parameters (optional, based on your use case)
    public SuperUserProfileEntity(int superuserProfileId, SuperUserEntity superuser, byte[] superuserProfilePicture) {
        this.superuserProfileId = superuserProfileId;
        this.superuser = superuser;
        this.superuserProfilePicture = superuserProfilePicture;
    }

    // Getter and Setter for superuserProfileId
    public int getSuperuserProfileId() {
        return superuserProfileId;
    }

    public void setSuperuserProfileId(int superuserProfileId) {
        this.superuserProfileId = superuserProfileId;
    }

    // Getter and Setter for superuser
    public SuperUserEntity getSuperuser() {
        return superuser;
    }

    public void setSuperuser(SuperUserEntity superuser) {
        this.superuser = superuser;
    }

    // Getter and Setter for superuserProfilePicture
    public byte[] getSuperuserProfilePicture() {
        return superuserProfilePicture;
    }

    public void setSuperuserProfilePicture(byte[] superuserProfilePicture) {
        this.superuserProfilePicture = superuserProfilePicture;
    }
}
