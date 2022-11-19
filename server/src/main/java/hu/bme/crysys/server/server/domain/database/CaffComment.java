package hu.bme.crysys.server.server.domain.database;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CaffComment")
public class CaffComment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id = 0;

    @ManyToOne
    @JoinColumn(name = "caff_id")
    private CaffFile caffFile;

    @Column(name = "created")
    private LocalDateTime created;

    @Lob
    @Column(name = "content")
    private String content;

    @PrePersist
    private void prePersist() {
        if (created == null) created = LocalDateTime.now();
    }
}
