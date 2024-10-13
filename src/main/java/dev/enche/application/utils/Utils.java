package dev.enche.application.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;

public class Utils {

    public static String objectAsJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(object);
    }

    public static String objectAsPrettyJson(Object object) throws JsonProcessingException {
        return new JSONObject(objectAsJson(object)).toString(4);
    }

}
