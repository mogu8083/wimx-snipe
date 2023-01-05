package com.ulalalab.snipe.infra.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ByteUtils {

    private static final int[] CRC_TABLES = {
        0x0000, 0xc0c1, 0xc181, 0x0140, 0xc301, 0x03c0, 0x0280, 0xc241,
        0xc601, 0x06c0, 0x0780, 0xc741, 0x0500, 0xc5c1, 0xc481, 0x0440,
        0xcc01, 0x0cc0, 0x0d80, 0xcd41, 0x0f00, 0xcfc1, 0xce81, 0x0e40,
        0x0a00, 0xcac1, 0xcb81, 0x0b40, 0xc901, 0x09c0, 0x0880, 0xc841,
        0xd801, 0x18c0, 0x1980, 0xd941, 0x1b00, 0xdbc1, 0xda81, 0x1a40,
        0x1e00, 0xdec1, 0xdf81, 0x1f40, 0xdd01, 0x1dc0, 0x1c80, 0xdc41,
        0x1400, 0xd4c1, 0xd581, 0x1540, 0xd701, 0x17c0, 0x1680, 0xd641,
        0xd201, 0x12c0, 0x1380, 0xd341, 0x1100, 0xd1c1, 0xd081, 0x1040,
        0xf001, 0x30c0, 0x3180, 0xf141, 0x3300, 0xf3c1, 0xf281, 0x3240,
        0x3600, 0xf6c1, 0xf781, 0x3740, 0xf501, 0x35c0, 0x3480, 0xf441,
        0x3c00, 0xfcc1, 0xfd81, 0x3d40, 0xff01, 0x3fc0, 0x3e80, 0xfe41,
        0xfa01, 0x3ac0, 0x3b80, 0xfb41, 0x3900, 0xf9c1, 0xf881, 0x3840,
        0x2800, 0xe8c1, 0xe981, 0x2940, 0xeb01, 0x2bc0, 0x2a80, 0xea41,
        0xee01, 0x2ec0, 0x2f80, 0xef41, 0x2d00, 0xedc1, 0xec81, 0x2c40,
        0xe401, 0x24c0, 0x2580, 0xe541, 0x2700, 0xe7c1, 0xe681, 0x2640,
        0x2200, 0xe2c1, 0xe381, 0x2340, 0xe101, 0x21c0, 0x2080, 0xe041,
        0xa001, 0x60c0, 0x6180, 0xa141, 0x6300, 0xa3c1, 0xa281, 0x6240,
        0x6600, 0xa6c1, 0xa781, 0x6740, 0xa501, 0x65c0, 0x6480, 0xa441,
        0x6c00, 0xacc1, 0xad81, 0x6d40, 0xaf01, 0x6fc0, 0x6e80, 0xae41,
        0xaa01, 0x6ac0, 0x6b80, 0xab41, 0x6900, 0xa9c1, 0xa881, 0x6840,
        0x7800, 0xb8c1, 0xb981, 0x7940, 0xbb01, 0x7bc0, 0x7a80, 0xba41,
        0xbe01, 0x7ec0, 0x7f80, 0xbf41, 0x7d00, 0xbdc1, 0xbc81, 0x7c40,
        0xb401, 0x74c0, 0x7580, 0xb541, 0x7700, 0xb7c1, 0xb681, 0x7640,
        0x7200, 0xb2c1, 0xb381, 0x7340, 0xb101, 0x71c0, 0x7080, 0xb041,
        0x5000, 0x90c1, 0x9181, 0x5140, 0x9301, 0x53c0, 0x5280, 0x9241,
        0x9601, 0x56c0, 0x5780, 0x9741, 0x5500, 0x95c1, 0x9481, 0x5440,
        0x9c01, 0x5cc0, 0x5d80, 0x9d41, 0x5f00, 0x9fc1, 0x9e81, 0x5e40,
        0x5a00, 0x9ac1, 0x9b81, 0x5b40, 0x9901, 0x59c0, 0x5880, 0x9841,
        0x8801, 0x48c0, 0x4980, 0x8941, 0x4b00, 0x8bc1, 0x8a81, 0x4a40,
        0x4e00, 0x8ec1, 0x8f81, 0x4f40, 0x8d01, 0x4dc0, 0x4c80, 0x8c41,
        0x4400, 0x84c1, 0x8581, 0x4540, 0x8701, 0x47c0, 0x4680, 0x8641,
        0x8201, 0x42c0, 0x4380, 0x8341, 0x4100, 0x81c1, 0x8081, 0x4040
    };

    /**
     * ByteBuf -> Hex String
     */
    public static String byteBufToHexString(ByteBuf buf, int startIndex, int endIndex) {
        StringBuffer sb = new StringBuffer();

        for (int i = startIndex; i < endIndex; i++) {
            sb.append(byteToHexString(buf.getByte(i)));
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Hex -> 10진수 변환
     */
    public static String getHexToDec(String hex) {
        long v = Long.parseLong(hex, 16);
        return String.valueOf(v);
    }

    /**
     * 10진수 -> Hex 변환
     */
    public static String getDecToHex(String dec) {
        long intDec = Long.parseLong(dec);
        return Long.toHexString(intDec).toUpperCase();
    }

    /**
     * 10진수 -> Hex 변환
     */
    public static String getDecToHex(long dec) {
        return Long.toHexString(dec).toUpperCase();
    }

    /**
     * Byte -> Int형 변환
     */
    public static int getByteToInt(byte[] arr) {
        return (arr[0] & 0xff)<<24 | (arr[1] & 0xff)<<16 | (arr[2] & 0xff)<<8 | (arr[3] & 0xff);
    }

    /**
     *  유니코드 => 문자 변환
     */
    public static String getCharCode(int... codePoints) {
        return new String(codePoints, 0, codePoints.length);
    }

    /**
     * 16진수 => 바이트배열
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * 바이트배열 => 16진수(HEX)
     */
    public static String byteArrayToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02X ", b&0xff));
        }
        return sb.toString();
    }

    /**
     * 바이트 => 16진수
     */
    public static String byteToHexString(byte b){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%02X", b&0xff));
        return sb.toString();
    }

    /**
     * int형 -> 바이트배열 (BIG ENDIAN)
     */
	/*
	public static byte[] intToByteArray(final int integer) {
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
		buff.putInt(integer);
		buff.order(ByteOrder.BIG_ENDIAN);
		//buff.order(ByteOrder.LITTLE_ENDIAN);
		return buff.array();
	}
	*/
    public static byte[] intToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)(value >> 24);
        byteArray[1] = (byte)(value >> 16);
        byteArray[2] = (byte)(value >> 8);
        byteArray[3] = (byte)(value);
        return byteArray;
    }

    /**
     * 바이트배열 -> int형
     */
    public static int byteArrayToInt(byte[] bytes) {
        final int size = Integer.SIZE / 8;
        ByteBuffer buff = ByteBuffer.allocate(size);
        final byte[] newBytes = new byte[size];
        for (int i = 0; i < size; i++) {
            if (i + bytes.length < size) {
                newBytes[i] = (byte) 0x00;
            } else {
                newBytes[i] = bytes[i + bytes.length - size];
            }
        }
        buff = ByteBuffer.wrap(newBytes);
        buff.order(ByteOrder.BIG_ENDIAN); // Endian에 맞게 세팅
        return buff.getInt();
    }

    /**
     * short형 -> 바이트배열 (BIG ENDIAN)
     */
    public static byte[] shortToByte(short a) {
        byte[] shortToByte = new byte[2];
        shortToByte[0] |= (byte)((a & 0xFF00) >>> 8);
        shortToByte[1] |= (byte)(a & 0xFF & 0xff);
        return shortToByte;
    }

    /**
     * 바이트배열 -> short형
     */
    public static short byteArrayToShort(byte[] bytes) {
        short newValue = 0;
        newValue |= (((int)bytes[0])<<8)&0xFF00;
        newValue |= (((int)bytes[1]))&0xFF;
        return newValue;
    }

    /**
     * long -> 바이트배열(unsigned Int)
     */
    public static byte[] unSignedInt(long number) {
        byte[] data = new byte[4];
        data[0] = (byte) ((number >> 24) & 0xff);
        data[1] = (byte) ((number >> 16) & 0xff);
        data[2] = (byte) ((number >> 8) & 0xff);
        data[3] = (byte) (number & 0xff);
        return data;
    }

    /**
     * 바이트배열(unsigned Int) -> long
     */
    public static long unSignedInt(byte[] data, int offset) {
        return (((long) data[offset] & 0xffL) << 24) | (((long) data[offset + 1] & 0xffL) << 16)
                | (((long) data[offset + 2] & 0xffL) << 8) | ((long) data[offset + 3] & 0xffL);
    }

    /**
     * int -> 바이트배열(unsigned short)
     */
    public static int unSignedShort(byte[] data, int offset) {
        return (((int) data[offset] & 0xff) << 8) | ((int) data[offset + 1] & 0xff);
    }

    /**
     * 바이트배열(unsigned short) -> int
     */
    public static byte[] unSignedShort(int number ) {
        byte[] data = new byte[4];
        data[0] = (byte) ((number >> 8) & 0xff);
        data[1] = (byte) (number & 0xff);
        return data;
    }

    /**
     * double -> byte 배열 (8byte)
     */
//    public static byte[] convertShortToByteArray(int number) {
//        ByteBuffer byteBuffer = ByteBuffer.allocate(Short.BYTES);
//        byteBuffer.putShort();
//        return byteBuffer.array();
//    }

    /**
     * double -> byte 배열 (8byte)
     */
    public static byte[] convertDoubleToByteArray(double number) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
        byteBuffer.putDouble(number);
        return byteBuffer.array();
    }

    /**
     * float -> byte 배열 (4byte)
     */
    public static byte[] convertFloatToByteArray(float number) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Float.BYTES);
        byteBuffer.putFloat(number);
        return byteBuffer.array();
    }

    /**
     * int -> byte 배열 (4byte)
     */
    public static byte[] convertIntToByteArray(int number) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
        byteBuffer.putInt(number);
        return byteBuffer.array();
    }

    /**
     * long -> byte 배열 (8byte)
     */
    public static byte[] convertLongToByteArray(long number) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(number);
        return byteBuffer.array();
    }
}