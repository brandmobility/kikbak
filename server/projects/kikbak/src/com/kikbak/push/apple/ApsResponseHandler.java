package com.kikbak.push.apple;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

public class ApsResponseHandler extends Thread{

	static Logger log = Logger.getLogger(ApsResponseHandler.class);

	Socket socket = null;
    boolean running = true;

    public ApsResponseHandler(Socket socket) {

        this.socket = socket;
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        byte[] byteBuffer = new byte[38];
        while (running) {

            try {
                int nRead = socket.getInputStream().read(byteBuffer);

                // failed to read on socket
                if (nRead == -1) {
                    socket.close();
                    running = false;
                }
            } catch (IOException e) {
                log.error("Error reading socket " + e, e);
                break;
            }
        }
    }
}
