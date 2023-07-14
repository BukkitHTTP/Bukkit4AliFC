package cf.huzpsb.ali_serverless;

import nano.http.bukkit.internal.Bukkit_Node;
import nano.http.d2.consts.Mime;
import nano.http.d2.consts.Status;
import nano.http.d2.core.Response;
import nano.http.d2.serve.ServeProvider;

import java.util.Properties;

public class CFRouter implements ServeProvider {
    public static CFLogger logger = null;
    private final Bukkit_Node node;
    private final String uri;

    public CFRouter(Bukkit_Node node, String uri) {
        this.node = node;
        this.uri = uri;
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        if (uri.equals("/logs") && logger != null) {
            return new Response(Status.HTTP_OK, Mime.MIME_PLAINTEXT, logger.getLog());
        }
        if (uri.startsWith(this.uri)) {
            return node.serve(new StringBuilder(uri).delete(0, this.uri.length()).toString(), method, header, parms, files);
        }
        return node.fallback(uri, method, header, parms, files);
    }
}
