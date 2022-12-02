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

    @OneToMany(mappedBy = "userData")
    @JsonManagedReference
    @JsonIgnore
    private List<CaffFile> ownFiles;

    private String password;

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
    public List<CaffFile> getOwnFiles() {
        return ownFiles;
    }

    public void setOwnFiles(List<CaffFile> ownFiles) {
        this.ownFiles = ownFiles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
