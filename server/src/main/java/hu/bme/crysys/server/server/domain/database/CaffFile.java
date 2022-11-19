package hu.bme.crysys.server.server.domain.database;

import javax.persistence.*;

@Entity
@Table(name = "CaffFile")
public class CaffFile {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserData userData;

    @Lob
    @Column(name = "data")
    private Byte[] data;
}
