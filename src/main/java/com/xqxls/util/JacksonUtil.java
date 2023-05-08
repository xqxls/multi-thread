package com.xqxls.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

/**
 * @author 胡卓
 * @create 2023-05-08 15:02
 * @Description
 */
public class JacksonUtil {

    public static String objectToJson (Object o) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }

    public static <T> T jsonToObject (String json, Class<T> clazz) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Reader reader = new StringReader(json);
        return om.readValue(reader, clazz);
    }

    // 文件转Json对象
    public static <T> T fileToObject (String filePath, Class<T> clazz) throws IOException {
        ObjectMapper om = new ObjectMapper();
        File file = new File(filePath);
        return om.readValue(file, clazz);
    }

    // Json对象转文件
    public static <T>  void objectToFile (Object o, String filePath) throws IOException {
        ObjectMapper om = new ObjectMapper();
        String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        File f = new File(filePath);
        if (!f.exists()) {
            File folder = f.getParentFile();
            // 上级目录不存在则新建
            if (!folder.exists()) {
                boolean b = folder.mkdirs();
                if (!b) {
                    throw new IOException("Folder " + folder.getName() + "create fail!");
                }
            }
        }
        boolean b = f.createNewFile();
        if (b) {
            try (FileWriter file = new FileWriter(f)) {
                file.write(json);
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IOException("File " + filePath + "create fail!");
        }
    }

}
