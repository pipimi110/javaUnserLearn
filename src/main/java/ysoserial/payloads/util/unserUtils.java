package ysoserial.payloads.util;

import java.io.*;

public class unserUtils {
    public unserUtils() {
    }

    public static String bytesTohexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            StringBuilder ret = new StringBuilder(2 * bytes.length);

            for (int i = 0; i < bytes.length; ++i) {
                int b = 15 & bytes[i] >> 4;
                ret.append("0123456789abcdef".charAt(b));
                b = 15 & bytes[i];
                ret.append("0123456789abcdef".charAt(b));
            }

            return ret.toString();
        }
    }

    static int hexCharToInt(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        } else if (c >= 'A' && c <= 'F') {
            return c - 65 + 10;
        } else if (c >= 'a' && c <= 'f') {
            return c - 97 + 10;
        } else {
            throw new RuntimeException("invalid hex char '" + c + "'");
        }
    }

    public static byte[] hexStringToBytes(String s) {
        if (s == null) {
            return null;
        } else {
            int sz = s.length();
            byte[] ret = new byte[sz / 2];

            for (int i = 0; i < sz; i += 2) {
                ret[i / 2] = (byte) (hexCharToInt(s.charAt(i)) << 4 | hexCharToInt(s.charAt(i + 1)));
            }

            return ret;
        }
    }

    public static String base64Encode(byte[] bs) throws Exception {
        Class base64;
        String value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", null).invoke(base64, null);
            value = (String) Encoder.getClass().getMethod("encodeToString", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }

    public static byte[] objectToBytes(Object obj) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        out.flush();
        byte[] bytes = bos.toByteArray();
        bos.close();
        return bytes;
    }

    public static String objectToB64String(Object obj) throws Exception {
        byte[] bytes = objectToBytes(obj);
        String b64 = base64Encode(bytes);
        return b64;
    }
    public static String objectToHexString(Object obj) throws Exception {
        byte[] bytes = objectToBytes(obj);
        String hex = bytesTohexString(bytes);
        return hex;
    }

    public static void objToFile(Object obj, String file) throws Exception {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("D:\\javaWebTool\\ysoserial\\" + file));
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
    }

    public static void fileToObj(String file) throws Exception {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("D:\\javaWebTool\\ysoserial\\" + file));
        objectInputStream.readObject();
        objectInputStream.close();
    }

    public static void main(String[] args) throws Exception {
        String filePre = "cc11";
        String command = "calc.exe";
        String file = filePre + command + ".ser";
        fileToObj(file);
    }
}
