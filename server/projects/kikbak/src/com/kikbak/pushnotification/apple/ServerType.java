package com.kikbak.pushnotification.apple;

public enum ServerType {
    development("gateway.sandbox.push.apple.com", 2195, "feedback.sandbox.push.apple.com", 2196),
    production("gateway.push.apple.com", 2195, "feedback.push.apple.com", 2196);

    private final String host;
    private final int port;
    private final String feedbackHost;
    private final int feedbackPort;

    ServerType(String host, int port, String feedbackHost, int feedbackPort) {
        this.host = host;
        this.port = port;
        this.feedbackHost = feedbackHost;
        this.feedbackPort = feedbackPort;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getFeedbackHost() {
        return feedbackHost;
    }

    public int getFeedbackPort() {
        return feedbackPort;
    }
}