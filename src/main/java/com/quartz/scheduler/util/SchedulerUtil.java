package com.quartz.scheduler.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class SchedulerUtil {

    public static class Constants {
        public static final String JOB_SCHEDULE_TIME = "jobScheduleTime";
        public static final String JOB_NAME_REQ_PARAM = "jobName";
        public static final String JOB_CRON_EXP_REQ_PARAM = "cronExpression";
        public static final String JOB_REQ_BODY = "jsonBody";
        public static final String HTTP_METHOD = "httpMethod";
        public static final String HTTP_URL_ENDPOINT = "url";
        public static final String BASIC_AUTH_TOKEN = "basicAuthToken";

    }

    public static boolean isNotNullObjects(Object... objects) {
        boolean isAnyNull = Arrays.stream(objects).anyMatch(x -> Objects.isNull(x));
        return !isAnyNull;
    }

    public static boolean isNullObject(Object object) {
        return !isNotNullObjects(object);
    }

    /**
     * <pre>
     * if <i>String</i>:
     *  null	@return false
     *  ""	@return false
     *  " "	@return false
     *  if <i>Collection</i>:
     *  null or empty	@return false
     *  if <i>Map</i>:
     *  null or empty	@return false
     * </pre>
     *
     * @param objects
     * @return
     */
    public static boolean isNotNullNotEmptyObjects(Object... objects) {
        Predicate<Object> predicateStr = (x -> String.class.isAssignableFrom(x.getClass())
                && StringUtils.isEmpty(x.toString().trim()));
        Predicate<Object> predicateColl = (x -> Collection.class.isAssignableFrom(x.getClass())
                && ((Collection<?>) x).isEmpty());
        Predicate<Object> predicateMap = (x -> Map.class.isAssignableFrom(x.getClass()) && ((Map<?, ?>) x).isEmpty());

        return isNotNullObjects(objects) && !Arrays.stream(objects)
                .filter(x -> predicateStr.or(predicateColl).or(predicateMap).test(x)).findAny().isPresent();
    }

    public static ObjectNode createJsonNode() {
        return JsonNodeFactory.instance.objectNode();
    }

    public static ObjectNode createErrorJsonNode(HttpStatus status, String uri, String message) {
        ObjectNode jsonNode = createJsonNode();
        jsonNode.putPOJO("timestamp", new Date());
        jsonNode.put("statusCode", status.value());
        jsonNode.putPOJO("error", status.getReasonPhrase());
        jsonNode.put("path", uri);
        jsonNode.put("message", message);

        return jsonNode;
    }

    public static ObjectNode createErrorJsonNode(HttpStatus status, String uri, Map<String, ObjectNode> messageMap) {
        ObjectNode jsonNode = createJsonNode();
        jsonNode.putPOJO("timestamp", new Date());
        jsonNode.put("statusCode", status.value());
        jsonNode.putPOJO("error", status.getReasonPhrase());
        jsonNode.put("path", uri);

        ArrayNode arrayJsonNode = jsonNode.putArray("messages");

        // Get all errors
        messageMap.forEach((k, v) -> arrayJsonNode.add(v));

        return jsonNode;
    }


}