package coop.magnesium.utils;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by rsperoni on 09/11/17.
 */
public class RestUtils {

    /**
     * Extract filename from HTTP heaeders.
     *
     * @param headers
     * @return
     */
    public static String getFieldContent(MultivaluedMap<String, String> headers, String fieldName) {
        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");

        for (String field : contentDisposition) {
            if ((field.trim().startsWith(fieldName))) {
                String[] name = field.split("=");
                return sanitizeFieldContent(name[1]);
            }
        }
        return null;
    }

    /**
     * Extract contentType from HTTP heaeders.
     *
     * @param headers
     * @return
     */
    public static String getContentType(MultivaluedMap<String, String> headers) {
        String contentType = headers.getFirst("Content-Type");
        return sanitizeFieldContent(contentType);
    }

    public static String sanitizeFieldContent(String s) {
        return s.trim().replaceAll("\"", "");
    }

}
