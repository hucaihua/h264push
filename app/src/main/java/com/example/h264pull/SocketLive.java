package com.example.h264pull;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class SocketLive {
    private static final String TAG = "hucaihua";
    private final int port;
    private SocketCallback socketCallback;
    MyWebSocketClient myWebSocketClient;
    public SocketLive(SocketCallback socketCallback, int port) {
        this.socketCallback = socketCallback;
        this.port = port;
    }
    public void start() {
        try {
            URI url = new URI("ws://10.93.137.36:"+this.port);
            myWebSocketClient = new MyWebSocketClient(url);
            myWebSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            Log.i(TAG, "打开 socket  onOpen: ");
        }

        @Override
        public void onMessage(String s) {
            Log.i(TAG, "打开 socket  onOpen: ");
        }
        //不断回调他
        @Override
        public void onMessage(ByteBuffer bytes) {
            Log.i(TAG, "消息长度  : "+bytes.remaining());
            byte[] buf = new byte[bytes.remaining()];
            bytes.get(buf);
            socketCallback.callBack(buf);
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            Log.i(TAG, "onClose: ");
        }

        @Override
        public void onError(Exception e) {
            Log.i(TAG, "onError: " + e);
        }
    }
    public interface SocketCallback{
        void callBack(byte[] data);
    }
}
