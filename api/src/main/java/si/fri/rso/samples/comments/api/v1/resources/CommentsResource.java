package si.fri.rso.samples.comments.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.annotation.Counted;
import si.fri.rso.samples.comments.lib.Comment;
import si.fri.rso.samples.comments.services.CommentsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Log
@ApplicationScoped
@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentsResource {

    @Inject
    private CommentsBean commentsBean;

    @GET
    @Counted
    public Response getComments(@QueryParam("imageId") Integer imageId) {

        List<Comment> comments;

        if (imageId != null) {
            comments = commentsBean.getCommentsForImage(imageId);
        } else {
            comments = commentsBean.getComments();
        }

        return Response.ok(comments).build();
    }

    @GET
    @Path("count")
    public Response getCommentsCount(@QueryParam("imageId") Integer imageId) {

        List<Comment> comments;

        if (imageId != null) {
            comments = commentsBean.getCommentsForImage(imageId);
        } else {
            comments = commentsBean.getComments();
        }

        return Response.ok(comments.size()).build();
    }

}
