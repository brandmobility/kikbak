package com.kikbak.mail;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.io.Closeables;
import com.kikbak.config.ContextUtil;

public class EmailSender {
    private static Logger log = Logger.getLogger(EmailSender.class);

    private static PropertiesConfiguration config = ContextUtil.getBean("staticPropertiesConfiguration",
            PropertiesConfiguration.class);

    private static final String SEND_MAIL_URI = "https://sendgrid.com/api/mail.send.json";

    public static void send(String recipient, String subject, String body) {
        List<String> recipients = new ArrayList<String>(1);
        recipients.add(recipient);
        send(recipients, subject, body);
    }

    public static void send(List<String> recipients, String subject, String body) {
        URL url = null;
        try {
            StringBuilder b = new StringBuilder(SEND_MAIL_URI);
            b.append('?');
            addParam(b, "api_user", config.getString("sendgrid.login"));
            addParam(b, "api_key", config.getString("sendgrid.passwd"));
            addParam(b, "from", config.getString("sendgrid.from"));
            addParam(b, "fromname", config.getString("sendgrid.fromname"));
            addParam(b, "to", "-"); // needs to be present
            addParam(b, "x-smtpapi", getSendgridEncoded(recipients));
            addParam(b, "subject", subject);
            addParam(b, "text", body);
            url = new URL(b.toString());
            URLConnection conn = url.openConnection();
            InputStream input = conn.getInputStream();
            // String content = CharStreams.toString(new InputStreamReader(input, Charsets.UTF_8));
            Closeables.close(input, true);
        } catch (Exception e) {
            String msg = url == null ? "null" : url.toString();
            log.error("Could not send email via sendgrid: " + msg, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static String getSendgridEncoded(List<String> recipients) {
        JSONArray array = new JSONArray();
        array.addAll(recipients);
        JSONObject json = new JSONObject();
        json.put("to", array);
        return json.toJSONString();
    }

    private static StringBuilder addParam(StringBuilder target, String param, String value) {
        try {
            target.append(param);
            target.append('=');
            target.append(URLEncoder.encode(value, "UTF-8"));
            target.append('&');
            return target;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
