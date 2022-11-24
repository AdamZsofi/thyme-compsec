package hu.bme.crysys.server.server.domain.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "CaffFile")
public class CaffFile {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @JsonIgnore
    private UserData userData;

    @Lob
    @Column(name = "path")
    @JsonIgnore
    private String path;

    @OneToMany(mappedBy = "caffFile")
    @JsonIgnore
    @JsonManagedReference
    private List<CaffComment> comments;

    public CaffFile() {}

    public CaffFile(String path, UserData userData) {
        this.path = path;
        this.userData = userData;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    @JsonIgnore
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @JsonIgnore
    public List<CaffComment> getComments() {
        return comments;
    }

    public void setComments(List<CaffComment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CaffFile caffFile = (CaffFile) o;

        if (!Objects.equals(id, caffFile.id)) return false;
        if (!Objects.equals(userData, caffFile.userData)) return false;
        if (!Objects.equals(path, caffFile.path)) return false;
        return Objects.equals(comments, caffFile.comments);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userData != null ? userData.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        return result;
    }
}
