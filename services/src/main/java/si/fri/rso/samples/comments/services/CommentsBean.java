package si.fri.rso.samples.comments.services;

import si.fri.rso.samples.comments.lib.Comment;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommentsBean {

    private Logger log = Logger.getLogger(CommentsBean.class.getName());

    private List<Comment> comments;

    @PostConstruct
    private void init() {
        comments = new ArrayList<>();
        comments.add(new Comment(1, UUID.randomUUID().toString(), "Very nice photo!"));
        comments.add(new Comment(1, comments.get(0).getAuthorId(), "Keep up the good work!"));
        comments.add(new Comment(1, UUID.randomUUID().toString(), "Beautiful"));
    }

    public List<Comment> getComments() {

        return comments;

    }

    public List<Comment> getCommentsForImage(Integer imageId) {

        return comments.stream().filter(comment -> comment.getImageId().equals(imageId)).collect(Collectors.toList());
    }

}
