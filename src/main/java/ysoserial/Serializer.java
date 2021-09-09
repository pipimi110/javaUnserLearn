package ysoserial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

public class Serializer implements Callable<byte[]> {
    private final Object object;

    public Serializer(Object object) {
        this.object = object;
    }

    public byte[] call() throws Exception {
        return serialize(object);
    }

    public static byte[] serialize(final Object obj) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        serialize(obj, out);
        return out.toByteArray();
    }

    public static void serialize(final Object obj, final OutputStream out) throws IOException {
//	    String code = out.toString();
//        System.out.println("code:"+code);
        final ObjectOutputStream objOut = new ObjectOutputStream(out);
//	    String code1 = objOut.toString();
//        System.out.println("code1:"+code1);
        objOut.writeObject(obj);
    }

}
