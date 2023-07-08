package org.demo.resteasy.controller;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author jacksparrow414
 * @date 2023/7/8
 */
public class StreamControllerTest {
    
    
    @SneakyThrows
    @Test
    public void assetGetStream() {
        Properties propsRet;
        URL url = new URL("http://localhost:8080/mvc-demo/mvc/stream/getStream");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
//        out.writeBytes(getEncodedArgs(propsIn));
//        out.flush();
//        out.close();
        InputStream in = connection.getInputStream();
        ObjectInputStream result = new ObjectInputStream(in);
        propsRet = (Properties) result.readObject();
        System.out.println(propsRet);
    }
}