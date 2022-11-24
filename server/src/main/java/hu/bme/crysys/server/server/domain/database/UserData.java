package hu.bme.crysys.server.server.domain.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "UserData")
public class UserData {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id = 0;

    @Column(name = "username")
    @NotNull
    private String userName;

    @Column(name = "password")
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "isAdmin")
    @NotNull
    @JsonIgnore
    private Boolean isAdmin;

    @OneToMany(mappedBy = "userData")
    @JsonManagedReference
    @JsonIgnore
    private List<CaffFile> ownFiles;

    @OneToMany(mappedBy = "userData")
    @JsonManagedReference
    @JsonIgnore
    private List<CaffFile> downloadableFiles;

    @PrePersist
    private void prePersist() {
        if (isAdmin == null) isAdmin = false;
    }

    public UserData() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @JsonIgnore
    public List<CaffFile> getOwnFiles() {
        return ownFiles;
    }

    public void setOwnFiles(List<CaffFile> ownFiles) {
        this.ownFiles = ownFiles;
    }

    @JsonIgnore
    public List<CaffFile> getDownloadableFiles() {
        return downloadableFiles;
    }

    public void setDownloadableFiles(List<CaffFile> downloadableFiles) {
        this.downloadableFiles = downloadableFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData userData = (UserData) o;

        if (!Objects.equals(id, userData.id)) return false;
        if (!Objects.equals(userName, userData.userName)) return false;
        return Objects.equals(password, userData.password);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (isAdmin != null ? isAdmin.hashCode() : 0);
        result = 31 * result + (ownFiles != null ? ownFiles.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                ", files=" + ownFiles +
                '}';
    }
}
