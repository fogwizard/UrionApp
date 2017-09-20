package com.urionapp.bp;

public class StringByte {
    private static final char[] DIGITS_LOWER = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[(l << 1)];
        int j = 0;
        for (int i = 0; i < l; i++) {
            int i2 = j + 1;
            out[j] = toDigits[(data[i] & 240) >>> 4];
            j = i2 + 1;
            out[i2] = toDigits[data[i] & 15];
        }
        return out;
    }

    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    public static byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & 1) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }
        byte[] out = new byte[(len >> 1)];
        int i = 0;
        int j = 0;
        while (j < len) {
            j++;
            j++;
            out[i] = (byte) (((toDigit(data[j], j) << 4) | toDigit(data[j], j)) & 255);
            i++;
        }
        return out;
    }

    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit != -1) {
            return digit;
        }
        throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
    }

    public static void main(String[] args) {
        String srcStr = "待转换字符串";
        String encodeStr = encodeHexStr(srcStr.getBytes());
        String decodeStr = new String(decodeHex(encodeStr.toCharArray()));
        System.out.println("转换前：" + srcStr);
        System.out.println("转换后：" + encodeStr);
        System.out.println("还原后：" + decodeStr);
    }
}
