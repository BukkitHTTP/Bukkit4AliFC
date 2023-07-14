package cf.huzpsb.ali_serverless;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionInitializer;
import com.aliyun.fc.runtime.HttpRequestHandler;
import nano.http.bukkit.internal.Bukkit_Node;
import nano.http.d2.console.Logger;
import nano.http.d2.core.Response;
import nano.http.d2.hooks.HookManager;
import nano.http.d2.serve.ServeProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

/**
 * Hello world!
 */
public class Main implements HttpRequestHandler, FunctionInitializer {
    private static ServeProvider provider = null;
    private static boolean initialized = false;

    @Override
    public void initialize(Context context) throws IOException {
        initialized = true;
        provider = new CFRouter(null, "/func");
        long startTime = System.currentTimeMillis();
        if (context == null) {
            CFRouter.logger = new CFLogger();
            Logger.impl = CFRouter.logger;
            Logger.warning("Context is null, using CFLogger instead of context logger!");
        } else {
            Logger.impl = context.getLogger();
        }
        Logger.info("Initializing BukkitHTTP Emulator 4 Ali-FC...");
        Logger.info("Version: " + nano.http.bukkit.Main.VERSION);
        Logger.info("Initializing logger...");
        Logger.info("Initializing hooks...");
        HookManager.invoke();
        Logger.info("Initializing router...");
        try {
            ClassLoader mainLoader = getClass().getClassLoader();
            URL resourceUrl = Main.class.getResource("/task.jar");
            if (resourceUrl == null) {
                throw new Exception("task.jar not found!");
            }
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{resourceUrl}, mainLoader);
            Logger.info("Loading plugin...");
            Scanner scanner = new Scanner(urlClassLoader.getResourceAsStream("plugin.properties"));
            String main = null;
            String uri = null;
            String name = null;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("main=")) {
                    main = line.substring(5);
                } else if (line.startsWith("uri=")) {
                    uri = line.substring(4);
                } else if (line.startsWith("name=")) {
                    name = line.substring(5);
                }
            }
            if (main == null || uri == null || name == null) {
                throw new Exception("No main/uri/name found in plugin.properties");
            }
            Bukkit_Node node = new Bukkit_Node(uri, urlClassLoader, main, name);
            Logger.info("Initializing plugin...");
            node.onEnable(name, new File("."), uri);
            provider = new CFRouter(node, "/func");
        } catch (Throwable t) {
            Logger.error("Failed to initialize BukkitHTTP Emulator 4 Ali-FC!", t);
            throw new IOException(t);
        }
        Logger.info("Done! (" + (System.currentTimeMillis() - startTime) + "ms)");
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context)
            throws IOException, ServletException {
        if (!initialized) {
            initialize(null);
        }
        String requestURI = (String) request.getAttribute("FC_REQUEST_PATH");
        if (requestURI.length() <= 1) {
            requestURI = "/";
        }
        String requestClientIP = (String) request.getAttribute("FC_REQUEST_CLIENT_IP");

        Properties requestHeaders = new Properties();
        Enumeration<String> keys = request.getHeaderNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            requestHeaders.setProperty(key, request.getHeader(key));
        }

        Properties requestParams = new Properties();
        keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            requestParams.setProperty(key, request.getParameter(key));
        }

        Response resp = HookManager.requestHook.serve(requestURI, request.getMethod(), requestHeaders, requestParams, new Properties(), provider, requestClientIP);
        response.setStatus(Integer.parseInt(resp.status.split(" ")[0]));
        response.addHeader("Content-Type", resp.mimeType);
        for (Object keyR : resp.header.keySet()) {
            response.addHeader((String) keyR, resp.header.getProperty((String) keyR));
        }

        OutputStream out = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = resp.data.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
        out.close();
    }
}
