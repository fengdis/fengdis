package com.fengdis.component.rpc.rocketmq;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Serialization {

    private static final String encoding = "utf-8";
    private static final Logger logger = LoggerFactory.getLogger(Serialization.class);
    private static Gson gson = new GsonBuilder().setDateFormat(DateFormat.LONG).create();

    public static final byte[] serialize(Object object) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);

        try {
            String json = gson.toJson(object);
            gzip.write(json.getBytes(encoding));
            gzip.finish();
            gzip.flush();
            return out.toByteArray();
        }catch (Exception e){
            logger.error("serialize error",e);
            throw e;
        }finally {
            gzip.close();
            out.close();
        }
    }


    public static final <T> T deserialize(byte[] bytes, Class<T> type) throws Exception{
        final int BUFFER = 256;
        String json = null;

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPInputStream gis = new GZIPInputStream(is);
        try {
            int count = 0;
            byte data[] = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1){
                out.write(data, 0, count);

            }
            out.flush();
            out.close();
            json = new String(out.toByteArray(), encoding);
            return gson.fromJson(json, type);
        }catch (Exception e){
            logger.error("deserialize error",e);
            throw e;
        }finally {
            gis.close();
            out.flush();
            out.close();
            is.close();
        }
    }

    public static final String serializeAsJson(Object object){
        Gson gson = new GsonBuilder().setDateFormat(DateFormat.LONG).create();
        return gson.toJson(object);
    }
}
