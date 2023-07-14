package cf.huzpsb.ali_serverless;

import com.aliyun.fc.runtime.FunctionComputeLogger;

import java.text.SimpleDateFormat;

public class CFLogger implements FunctionComputeLogger {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private StringBuilder sb = new StringBuilder();

    private String format(String input, String type) {
        return "[" + simpleDateFormat.format(System.currentTimeMillis()) + "] [" + type.toUpperCase() + "] " + input + "\n";
    }

    @Override
    public void trace(String s) {
        sb.append(format(s, "trace"));
    }

    @Override
    public void debug(String s) {
        sb.append(format(s, "debug"));
    }

    @Override
    public void info(String s) {
        sb.append(format(s, "info"));
    }

    @Override
    public void warn(String s) {
        sb.append(format(s, "warn"));
    }

    @Override
    public void error(String s) {
        sb.append(format(s, "error"));
    }

    @Override
    public void fatal(String s) {
        sb.append(format(s, "fatal"));
    }

    @Override
    public void setLogLevel(Level level) {
        sb.append(format("setLogLevel: " + level, "emulate"));
    }

    public String getLog() {
        return sb.toString();
    }
}
