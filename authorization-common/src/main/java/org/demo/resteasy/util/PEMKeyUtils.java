package org.demo.resteasy.util;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class PEMKeyUtils {

    /**
     * https://stackoverflow.com/questions/20389255/reading-a-resource-file-from-within-jar
     * @param keyLocation
     * @return
     * @throws Exception
     */
    public static String readKeyAsString(String keyLocation) throws Exception {

        InputStream inputStream = PEMKeyUtils.class.getClassLoader().getResourceAsStream(keyLocation);
        return IOUtils.toString(inputStream);

        // 下面这种写法在同一个工程下没问题，但是打成jar之后读取resource文件会有问题,解决方案见注释
//        URI uri = currentThread().getContextClassLoader().getResource(keyLocation).toURI();
//        byte[] byteArray = Files.readAllBytes(Paths.get(uri));
//        return new String(byteArray);
    }
}
