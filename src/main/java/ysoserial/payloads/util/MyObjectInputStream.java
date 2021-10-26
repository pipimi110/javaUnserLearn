package ysoserial.payloads.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.commons.collections.Transformer;
//tctf final 2021 buggyloader
public class MyObjectInputStream extends ObjectInputStream {
    private ClassLoader classLoader;

    public MyObjectInputStream(InputStream inputStream) throws Exception {
        super(inputStream);
        URL[] urls = ((URLClassLoader)Transformer.class.getClassLoader()).getURLs();
        this.classLoader = new URLClassLoader(urls);
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        Class clazz = this.classLoader.loadClass(desc.getName());
        return clazz;
    }
}
