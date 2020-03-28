package mapcluster.smile.com.bdmapcluster.util;

import com.google.gson.Gson;

import okhttp3.RequestBody;

/**
 * @author greensun
 *
 * @date 2018/8/30
 *
 * @desc
 */
public class JsonUtil {
    private static Gson gson = null;

    public static<T> T Json2Bean(String json, Class<T> cls) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(json, cls);
    }

    public static String Bean2Json(Object obj) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.toJson(obj);
    }

    public static RequestBody getRequestBody(Object obj) {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), Bean2Json(obj));
    }
}
