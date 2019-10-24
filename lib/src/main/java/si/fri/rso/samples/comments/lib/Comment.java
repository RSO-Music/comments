package si.fri.rso.samples.comments.lib;

import java.time.Instant;

public class Comment {

    private Integer imageId;
    private Instant createdAt;
    private String authorId;
    private String text;

    public Comment(Integer imageId, String authorId, String text) {
        this.imageId = imageId;
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
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}
