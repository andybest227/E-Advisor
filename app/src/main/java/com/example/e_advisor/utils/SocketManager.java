package com.example.e_advisor.utils;

import android.util.Log;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class SocketManager {
    private static Socket socket;
    private static final APIAddress apiAddress = new APIAddress();
    private static final String BASE_URL = apiAddress.api_address();

    public static Socket getSocket() {
        if (socket == null) {
            try {
                IO.Options options = new IO.Options();
                options.transports = new String[]{"websocket"};
                options.reconnection = true;
                options.forceNew = true;

                socket = IO.socket(BASE_URL, options);
            } catch (URISyntaxException e) {
                Log.e("SocketManager", "Invalid URI: " + BASE_URL, e);
                return null;
            }
        }
        return socket;
    }

    public static void disconnectSocket() {
        if (socket != null) {
            if (socket.connected()) {
                socket.disconnect();
            }
            socket.off();
            socket = null;
        }
    }
}

