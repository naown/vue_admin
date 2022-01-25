package com.naown;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.naown.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        User user = new User();
        user.setAvatar("头像");
        user.setCity("城市");
        String s = JSONObject.toJSONString(user, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
        JSONObject jsonObject = JSONObject.parseObject(s);
        jsonObject.put("version","212");
        String s1 = jsonObject.toJSONString();
        System.out.println(s);
        System.out.println(s1);
    }

}
