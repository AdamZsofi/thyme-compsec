package hu.bme.crysys.server.server.domain.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "CaffComment")
public class CaffComment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id = 0;

    @ManyToOne
    @JoinColumn(name = "caff_id")
    @JsonBackReference
    private CaffFile caffFile;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "author")
    private String author;

    @Lob
    @Column(name = "content")
    private String content;

    @PrePersist
    private void prePersist() {
        if (created == null) created = LocalDateTime.now();
    }

    public CaffComment() {}

    public CaffComment(CaffFile caffFile, String author, String content) {
        this.caffFile = caffFile;
        this.author = author;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CaffFile getCaffFile() {
        return caffFile;
    }

    public void setCaffFile(CaffFile caffFile) {
        this.caffFile = caffFile;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CaffComment that = (CaffComment) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(caffFile, that.caffFile)) return false;
        if (!Objects.equals(created, that.created)) return false;
        if (!Objects.equals(author, that.author)) return false;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (caffFile != null ? caffFile.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
