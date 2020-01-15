package si.fri.rso.gregor.songcatalog.api.v1.resources;

//import com.amazonaws.services.s3.AmazonS3Client;

import com.google.gson.Gson;
import com.kumuluz.ee.cors.annotations.CrossOrigin;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import si.fri.rso.gregor.songcatalog.api.v1.dtos.UploadSongResponse;
import si.fri.rso.gregor.songcatalog.lib.Songs;
import si.fri.rso.gregor.songcatalog.services.dtos.SongProcessRequest;
import si.fri.rso.gregor.songcatalog.services.beans.SongsBean;
import si.fri.rso.gregor.songcatalog.services.clients.AmazonRekognitionClient;
import si.fri.rso.gregor.songcatalog.services.clients.AmazonS3Client;
import si.fri.rso.gregor.songcatalog.services.clients.SongsProcessingApi;
import si.fri.rso.gregor.songcatalog.services.streaming.EventProducerImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;


@ApplicationScoped
@Path("/listen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CrossOrigin(allowOrigin = "*", allowSubdomains = true, supportedHeaders = "*")
public class SongsResource {
    final int chunk_size = 1024 * 1024; // 1MB chunks
    private final File audio;

    public SongsResource() throws IOException {
        // serve media from file system
        URL website = new URL("https://rso-music.s3.amazonaws.com/d2ed099eb3cda2e1635fa1c97287c7735611ec50");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream("temp.mp3");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        String MEDIA_FILE = "temp.mp3";
        URL url = this.getClass().getResource(MEDIA_FILE);
        audio = new File(url.getFile());
    }

    //A simple way to verify if the server supports range headers.
    @HEAD
    @Produces("audio/mp3")
    public Response header() {
        return Response.ok().status(206).header(HttpHeaders.CONTENT_LENGTH, audio.length()).build();
    }

    @GET
    @Produces("audio/mp3")
    public Response streamAudio(@HeaderParam("Range") String range) throws Exception {
        return buildStream(audio, range);
    }

    /**
     * Adapted from http://stackoverflow.com/questions/12768812/video-streaming-to-ipad-does-not-work-with-tapestry5/12829541#12829541
     *
     * @param asset Media file
     * @param range range header
     * @return Streaming output
     * @throws Exception IOException if an error occurs in streaming.
     */
    private Response buildStream(final File asset, final String range) throws Exception {
        // range not requested : Firefox does not send range headers
        if (range == null) {
            StreamingOutput streamer = output -> {
                try (FileChannel inputChannel = new FileInputStream(asset).getChannel(); WritableByteChannel outputChannel = Channels.newChannel(output)) {
                    inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                }
            };
            return Response.ok(streamer).status(200).header(HttpHeaders.CONTENT_LENGTH, asset.length()).build();
        }

        String[] ranges = range.split("=")[1].split("-");
        final int from = Integer.parseInt(ranges[0]);

        /*
          Chunk media if the range upper bound is unspecified. Chrome, Opera sends "bytes=0-"
         */
        int to = chunk_size + from;
        if (to >= asset.length()) {
            to = (int) (asset.length() - 1);
        }
        if (ranges.length == 2) {
            to = Integer.parseInt(ranges[1]);
        }

        final String responseRange = String.format("bytes %d-%d/%d", from, to, asset.length());
        final RandomAccessFile raf = new RandomAccessFile(asset, "r");
        raf.seek(from);

        final int len = to - from + 1;
        final MediaStreamer streamer = new MediaStreamer(len, raf);
        Response.ResponseBuilder res = Response.ok(streamer)
                .status(Response.Status.PARTIAL_CONTENT)
                .header("Accept-Ranges", "bytes")
                .header("Content-Range", responseRange)
                .header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth())
                .header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()));
        return res.build();
    }
}
