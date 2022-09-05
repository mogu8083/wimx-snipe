package com.ulalalab.api.common.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtil {
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
    public static byte[] convertDoubleToByteArray(double number) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
        byteBuffer.putDouble(number);
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
}