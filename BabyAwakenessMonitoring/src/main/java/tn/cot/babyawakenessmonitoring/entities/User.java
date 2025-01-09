package tn.cot.babyawakenessmonitoring.entities;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import tn.cot.babyawakenessmonitoring.enums.Role;
import tn.cot.babyawakenessmonitoring.utils.Argon2Utils;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;


@Entity
public class User implements Serializable {
    @Id
    @Column("id")
    private String id;
    @Column("fullName")
    private String fullName;
    @Column("email")
    private String email;
    @Column("password")
    private String password;
    @Column("mobile")
    private String mobile;
    @Column("creationDate")
    private String  creationDate;
    @Column("role")
    private Set<Role> roles ;
    @Column("isAccountActivated")
    private boolean isAccountActivated;

    public String getId() {return id;}

    public String getEmail() {return email;}

    public String getPassword() {return password;}

    public String getFullName() {return fullName;}

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobile() {return mobile;}

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreationDate() {return creationDate;}

    public Set<Role> getRoles() {return roles;}

    public boolean getAccountActivated() {return isAccountActivated;}

    public void setEmail(String email) {this.email = email;}

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreationDate(String creationDate) {this.creationDate = creationDate;}

    public void setRoles(Set<Role> roles) {this.roles = roles;}

    public void setAccountActivated(boolean accountActivated) {this.isAccountActivated = accountActivated;}

    public User() {
        this.id = UUID.randomUUID().toString();
        this.isAccountActivated = false;
    }

    public User(String id,String fullName, String email, String password, String creationDate, Set<Role> roles, String tenantId, String mobile,boolean isAccountActivated) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.creationDate = creationDate;
        this.roles = roles;
        this.mobile = mobile;
        this.isAccountActivated = isAccountActivated;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                "fullName='" + fullName + '\'' +
                "email='" + email + '\'' +
                "password='" + password + '\'' +
                "mobile='" + mobile + '\'' +
                "creationDate=" + creationDate +
                "roles=" + roles +
                "accoutActivated=" + isAccountActivated +
                '}';
    }

    public void hashPassword(String password, Argon2Utils argonUtility) {
        this.password = argonUtility.hash(password.toCharArray());
    }
}
