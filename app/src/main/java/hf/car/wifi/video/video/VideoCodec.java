package hf.car.wifi.video.video;

public interface VideoCodec {

    String MIME_TYPE = "video/avc";
    int VIDEO_FRAME_PER_SECOND = 15;
    int VIDEO_I_FRAME_INTERVAL = 5;
    int VIDEO_BITRATE = 500 * 8 * 1000;
}
