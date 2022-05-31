package com.example.h264pull;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class H264Player implements SocketLive.SocketCallback {

    private MediaCodec mediaCodec;

    public H264Player(Surface surface) {
        try {
            final MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 720, 1280);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 720 * 1280);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mediaCodec.configure(format, surface, null, 0);
            mediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callBack(byte[] bytes) {
        //这里是一帧一帧投递的，因此不需要大的while循环
        //将每帧数据送入mediaCodec解码，然后输出到surface
        int inIndex = mediaCodec.dequeueInputBuffer(10000);
        if (inIndex >= 0){
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inIndex);
            inputBuffer.put(bytes);
            mediaCodec.queueInputBuffer(inIndex ,0,bytes.length, createPTS(),0);
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo , 10000);
        //可能不止一帧
        while (outIndex >= 0){
            mediaCodec.releaseOutputBuffer(outIndex , true);
            outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo , 10000);
        }
    }

    private long createPTS(){
        return System.currentTimeMillis();
    }
}
