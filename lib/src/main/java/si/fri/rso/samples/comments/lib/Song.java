package si.fri.rso.samples.comments.lib;

import java.time.Instant;

public class Song {

    private Integer songId;
    private Instant createdAt;
    private String authorId;
    private String text;

    public Song(Integer songId, String authorId, String text) {
        this.songId = songId;
        this.createdAt = Instant.now();
        this.authorId = authorId;
        this.text = text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getImageId() {
        return songId;
    }

    public void setImageId(Integer imageId) {
        this.songId = imageId;
    }
}
