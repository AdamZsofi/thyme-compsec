package hu.bme.crysys.server.server.domain.database;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "CaffFile")
public class CaffFile {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = 0L;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;

    @Lob
    @Column(name = "data")
    private Byte[] data;

    @OneToMany(mappedBy = "caffFile")
    private List<CaffComment> comments;

}
