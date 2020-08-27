package com.example.interceptor;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pinge-shize
 */
public class JavaSlowInterceptor implements Interceptor {

    private String ip = "";

    private String hostName = "";

    private static final String NAME = "script_filename";

    @Override
    public void initialize() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
            hostName = addr.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Event intercept(Event event) {
        String body = new String(event.getBody(), StandardCharsets.UTF_8);
        if (body.contains(NAME)) {
            body = "[" + ip + "] [" + hostName + "] " + body;
            event.setBody(body.getBytes());
            return event;
        }
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> list) {
        List<Event> intercepted = new ArrayList<>(list.size());
        for (Event event : list) {
            Event interceptedEvent = intercept(event);
            if (interceptedEvent != null) {
                intercepted.add(interceptedEvent);
            }
        }
        return intercepted;

    }

    public static class Builder implements Interceptor.Builder {

        @Override
        public Interceptor build() {
            return new JavaSlowInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }

    @Override
    public void close() {

    }
}
