package nano.http.d2.console;

import com.aliyun.fc.runtime.FunctionComputeLogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class Logger {
    public static FunctionComputeLogger impl = null;

    public static void warning(String str) {
        impl.warn(str);
    }

    public static void error(String str) {
        impl.error(str);
    }

    public static void error(String str, Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        e.printStackTrace(pw);
        pw.flush();
        impl.error(str + "\n" + baos);
    }

    public static void debug(String str) {
        impl.debug(str);
    }

    public static void info(String str) {
        impl.info(str);
    }
}
