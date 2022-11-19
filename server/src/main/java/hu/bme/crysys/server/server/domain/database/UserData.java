package hu.bme.crysys.server.server.domain.database;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    private String password;

    @OneToMany(mappedBy = "userData")
    private List<CaffComment> comments;

    @OneToMany(mappedBy = "author_of_files", fetch = FetchType.LAZY)
    private List<CaffFile> files;

    public UserData(Integer id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }
}
