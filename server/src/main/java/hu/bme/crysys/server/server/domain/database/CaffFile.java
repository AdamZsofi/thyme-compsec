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
    @JoinColumn(name = "author_id")
    private UserData author_of_files;

    @Lob
    @Column(name = "data")
    private Byte[] data;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserData getAuthor() {
        return author_of_files;
    }

    public void setAuthor(UserData author) {
        this.author_of_files = author;
    }

    public Byte[] getData() {
        return data;
    }

    public void setData(Byte[] data) {
        this.data = data;
    }
}
