package com.niklaus.mvvm.utils;

import android.graphics.Bitmap;
import android.os.Build;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteUtil {

    public static String bytes2HexString(byte[] b) {
        StringBuilder result = new StringBuilder();
        String hex;

        for (byte value : b) {
            hex = Integer.toHexString(value & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase());
        }
        return result.toString();
    }

    public static String bytes2HexStringLE(byte[] b) {
        StringBuilder result = new StringBuilder();
        String hex;

        for (int i = b.length - 1; i >= 0; i--) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase());
        }
        return result.toString();
    }

    public static byte[] bytes2bytes2LE(byte[] b) {
        int size = b.length;
        byte[] result = new byte[size];

        for (int i = size - 1; i >= 0; i--) {
            result[size - i - 1] = b[i];
        }
        return result;
    }

    public static String byte2HexString(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    /**
     * 清除byte[]空内容
     */
    public static byte[] replaceZero(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        for (byte aByte : bytes) {
            if (aByte != 0x00) {
                buffer.put(aByte);
            }
        }
        byte[] validByte = new byte[buffer.position()];
        buffer.get(validByte, 0, buffer.position());
        return validByte;
    }

    public static String byteToStr(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * byte[]转化成int
     */
    public static int byteArrayToInt(byte[] byteArray) {
        int intValue = 0;
        for (byte b : byteArray) {
            intValue = intValue * 8 + b;
        }
        return intValue;
    }

    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }

        //低版本中用一行的字节*高度
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static String ip2int16(String ip) {
        StringBuilder sb = new StringBuilder();
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            byte[] bytes = inetAddress.getAddress();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
