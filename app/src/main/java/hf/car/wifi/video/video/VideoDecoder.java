//package hf.car.wifi.video.video;
//
//import android.annotation.SuppressLint;
//import android.media.MediaCodec;
//import android.media.MediaFormat;
//import android.util.Log;
//import android.view.Surface;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.Arrays;
//
//public class VideoDecoder implements VideoCodec{
//
//    private Surface mSurface;
//    private static final int TYPE_SPS = 7;
//    private static final int TYPE_PPS = 8;
//    private static final int TYPE_FRAME_DATA = 5;
//    private static final int NO_FRAME_DATA = -1;
//    private final int TIMEOUT_US = 10000;
//    private static final String TAG = "daolema";
//    private Server mServer;
//    private Worker mWorker;
//    private byte[] mSps;
//    private byte[] mPps;
//    private final static int HEAD_OFFSET = 512;
//
//    public VideoDecoder(Surface surface){
//        mSurface = surface;
//    }
//
//    public void start() {
//        if (mWorker == null) {
//            mWorker = new Worker();
//            mWorker.setRunning(true);
//            mWorker.start();
//        }
//    }
//
//    public void stop() {
//        if (mWorker != null) {
//            mWorker.setRunning(false);
//            mWorker = null;
//        }
//        if (mServer != null) {
//            if (!mServer.hasRelease()) {
//                mServer.disconnect();
//            }
//        }
//    }
//
//    private class Worker extends Thread {
//        volatile boolean isRunning;
//        private MediaCodec decoder;
//        private int mWidth;
//        private int mHeight;
//        MediaCodec.BufferInfo mBufferInfo;
//
//        /**
//         * 等待客户端连接，解码器配置
//         *
//         * @return
//         */
//        public boolean prepare() {
//            mServer.connect();
//            mBufferInfo = new MediaCodec.BufferInfo();
//            // 首先读取编码的视频的长度和宽度
//            // try {
//            // mWidth = mServer.readInt();
//            // mHeight = mServer.readInt();
//            mWidth = 1280;
//            mHeight = 720;
//            // } catch (IOException e) {
//            // e.printStackTrace();
//            // return false;
//            // }
//            // 编码器那边会先发sps和pps来，头一帧就由sps和pps组成
//            int spsLength = bytesToInt(mServer.readLength());
//            byte[] sps = mServer.readSPSPPS(spsLength);
//            mSps = Arrays.copyOfRange(sps, 4, spsLength);
//            int ppsLength = bytesToInt(mServer.readLength());
//            byte[] pps = mServer.readSPSPPS(ppsLength);
//            mPps = Arrays.copyOfRange(pps, 4, ppsLength);
//            MediaFormat format = MediaFormat.createVideoFormat(
//                    MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);
//            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, mHeight * mWidth);
//            format.setInteger(MediaFormat.KEY_MAX_HEIGHT, mHeight);
//            format.setInteger(MediaFormat.KEY_MAX_WIDTH, mWidth);
//            format.setByteBuffer("csd-0", ByteBuffer.wrap(mSps));
//            format.setByteBuffer("csd-1", ByteBuffer.wrap(mPps));
//            try {
//                decoder = MediaCodec
//                        .createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            decoder.configure(format, mSurface, null, 0);
//            decoder.start();
//            return true;
//        }
//
//        public void setRunning(boolean running) {
//            isRunning = running;
//        }
//
//        @Override
//        public void run() {
//            if (!prepare()) {
//                Log.i(TAG, "视频解码器初始化失败");
//                isRunning = false;
//            }
//            while (isRunning) {
//                decode();
//            }
//            release();
//
//        }
//
//        private void decode() {
//            byte[] data = new byte[100000];
//            byte[] frameData = new byte[200000];
//            boolean isEOS = false;
//            while (!isEOS) {
//                // 判断是否是流的结尾
//                int inIndex = decoder.dequeueInputBuffer(TIMEOUT_US);
//                if (inIndex >= 0) {
//                    /**
//                     * 测试
//                     */
//                    // byte[] frame=mServer.readFrame();
//                    int frameLength = bytesToInt(mServer.readLength());
//                    Frame frame = mServer.readFrame(frameLength);
//                    ByteBuffer buffer = decoder.getInputBuffer(inIndex);
//                    if (buffer == null) {
//                        Log.i(TAG, "buffer=null");
//                        return;
//                    }
//                    buffer.clear();
//                    if (frame == null) {
//                        Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM");
//                        decoder.queueInputBuffer(inIndex, 0, 0, 0,
//                                MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                        isEOS = true;
//                        isRunning = false;
//                        // 服务已经断开，释放服务端
//                        mServer.disconnect();
//                    } else {
//                        buffer.put(frame.mData, 0, frame.length);
//                        buffer.clear();
//                        buffer.limit(frame.length);
//                        decoder.queueInputBuffer(inIndex, 0, frame.length, 0,
//                                MediaCodec.BUFFER_FLAG_SYNC_FRAME);
//                    }
//                } else {
//                    isEOS = true;
//                }
//                int outIndex = decoder.dequeueOutputBuffer(mBufferInfo,
//                        TIMEOUT_US);
//                // Log.i(TAG, "video decoding .....");
//                while (outIndex >= 0) {
//                    // ByteBuffer buffer =
//                    decoder.getOutputBuffer(outIndex);
//                    decoder.releaseOutputBuffer(outIndex, true);
//                    outIndex = decoder.dequeueOutputBuffer(mBufferInfo,
//                            TIMEOUT_US);// 再次获取数据，如果没有数据输出则outIndex=-1
//                    // 循环结束
//                }
//            }
//
//        }
//
//        /**
//         * 释放资源
//         */
//        @SuppressLint("NewApi")
//        private void release() {
//            if (decoder != null) {
//                decoder.stop();
//                decoder.release();
//            }
//        }
//    }
//
//    public int bytesToInt(byte[] bytes) {
//        int i;
//        i = (int) ((bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8)
//                | ((bytes[2] & 0xff) << 16) | ((bytes[3] & 0xff) << 24));
//        return i;
//    }
//}
