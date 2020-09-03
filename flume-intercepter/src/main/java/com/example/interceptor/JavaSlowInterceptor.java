package com.example.interceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.interceptor.Interceptor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pinge-shize
 */
public class JavaSlowInterceptor implements Interceptor {

    private static String ip = "";

    private static String hostName = "";

    private static final String NAME = "script_filename";

    private static int flag = 1;

    private static final StringBuffer slowLog = new StringBuffer();

    private static final Pattern p = Pattern.compile("^\\[[0-9]*-.*");

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
        if (StringUtils.isBlank(body)) {
            return null;
        }
        if (body.contains(NAME)) {
            body = "[" + ip + "] [" + hostName + "] " + body;
        }
        Matcher m = p.matcher(body);
        if (m.matches()) {
            if (flag == 1) {
                flag = 0;
                slowLog.append(body).append("\n");
            } else {
                String s = slowLog.toString();
                slowLog.setLength(0);
                slowLog.append(body).append("\n");
                event.setBody(s.getBytes());
                return event;
            }
        } else {
            if (flag == 0) {
                slowLog.append(body).append("\n");
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String a = "[01-Sep-2020 18:12:24]  [pool www] pid 10647";
        String b = "script_filename = /home/work/higo_im_service/public/index.php";
        String c = "[0x00007fdb4dc1e850] curl_exec() /home/work/higo_im_service/vendor/lumen/lohas/src/Lohas/Utilities/Http/HttpClient.php:242";

        String d = "[01-Sep-2020 18:12:24]  [pool www] pid 1069";
        String e = "script_filename = /home/work/higo_im_service/public/index1.php";
        String f = "[0x00007fdb4dc1e850] curl1_exec() /home/work/higo_im_service/vendor/lumen/lohas/src/Lohas/Utilities/Http/HttpClient.php:242";

        List<Event> list = new ArrayList<>();
        Event event1 = new SimpleEvent();
        event1.setBody(a.getBytes());

        Event event6 = new SimpleEvent();
        event6.setBody(f.getBytes());

        Event event2 = new SimpleEvent();
        event2.setBody(b.getBytes());

        Event event3 = new SimpleEvent();
        event3.setBody(c.getBytes());

        Event event4 = new SimpleEvent();
        event4.setBody(d.getBytes());

        Event event5 = new SimpleEvent();
        event5.setBody(e.getBytes());

        list.add(event1);
        list.add(event2);
        list.add(event3);
        list.add(event4);
        list.add(event5);
        list.add(event6);

        List<Event> s = test(list);
        System.out.println(new String(s.get(0).getBody()));
    }

    public static List<Event> test(List<Event> list) {
        List<Event> intercepted = new ArrayList<>(list.size());
        for (Event event : list) {
            Event interceptedEvent = test2(event);
            if (interceptedEvent != null) {
                intercepted.add(interceptedEvent);
            }
        }
        return intercepted;
    }

    public static Event test2(Event event) {
        String body = new String(event.getBody(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(body)) {
            return null;
        }
        if (body.contains(NAME)) {
            body = "[" + ip + "] [" + hostName + "] " + body;
        }
        Matcher m = p.matcher(body);
        if (m.matches()) {
            if (flag == 1) {
                flag = 0;
                slowLog.append(body).append("\n");
            } else {
                String s = slowLog.toString();
                slowLog.setLength(0);
                slowLog.append(body).append("\n");
                event.setBody(s.getBytes());
                return event;
            }
        } else {
            if (flag == 0) {
                slowLog.append(body).append("\n");
            }
        }
        return null;
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
