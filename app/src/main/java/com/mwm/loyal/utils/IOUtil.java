package com.mwm.loyal.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtil {
    /**
     * 把流读成字符串
     *
     * @param is 输入流
     * @return 字符串
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            //
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                //
            }
        }
        return sb.toString();
    }

    /**
     * 关闭流
     *
     * @param stream 可关闭的流
     */
    public static void closeStream(Closeable stream) {
        try {
            if (stream != null)
                stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] InputStreamToByte(InputStream is) throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            stream.write(ch);
        }
        byte byteData[] = stream.toByteArray();
        stream.close();
        return byteData;
    }
}
