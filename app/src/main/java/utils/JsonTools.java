package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import zust.yyj.entity.User;

public class JsonTools {
    private Gson gson;
    private GsonBuilder builder;

    public JsonTools() {
        super();
        this.gson = new Gson();
        this.builder = new GsonBuilder();
    }

    //实体类转为json数据
    public <T> String objToJson(T t){
        return gson.toJson(t,t.getClass());
    }

    //Json转实体类
    public <T> T jsonToObj(String jsonStr,Class<T> respClass){
        T t = gson.fromJson(jsonStr,respClass);
        return t;
    }
    public static void main (String[] args){
        User user = new User("zxc","zxc",20,"10087","123456");
        JsonTools jt = new JsonTools();
//        String json = jt.objToJson(user);
        Gson gson = new Gson();
//        String json = gson.toJson(user);
        String json = jt.objToJson(user);
        System.out.println(json);

    }
}
