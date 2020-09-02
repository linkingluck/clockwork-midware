package com.linkingluck.midware.resource.reader.impl;

import com.alibaba.fastjson.JSON;
import com.linkingluck.midware.resource.reader.ResourceReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class JsonReader implements ResourceReader {
    @Override
    public String getSuffix() {
        return "json";
    }

    @Override
    public boolean isBatch() {
        return true;
    }

    @Override
    public <E> List<E> read(Class<E> clz, InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader buffer = new BufferedReader(inputStreamReader);
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
            String jsonStr = sb.toString();
            return JSON.parseArray(jsonStr, clz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
