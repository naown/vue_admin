package com.naown.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.naown.common.lang.Const;
import com.naown.common.lang.Result;
import com.naown.entity.User;
import com.naown.service.IUserService;
import com.naown.utils.RedisUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.Principal;

/**
 * 验证码处理器
 * @author: chenjian
 * @since: 2021/5/12 23:18 周三
 **/
@RestController
public class AuthController extends BaseController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    /**
     * 生成随机验证码并且返回
     * @return
     */
    @SneakyThrows
    @GetMapping("/captcha")
    public Result captcha(){
        //String key = UUID.randomUUID().toString();
        //String code = defaultKaptcha.createText();

        String key = "aaaaa";
        String code = "11111";
        BufferedImage image = defaultKaptcha.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image,"jpg",outputStream);
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String base64Img = "data:image/jpeg;base64," + base64Encoder.encode(outputStream.toByteArray());

        redisUtil.hset(Const.CAPTCHA_KEY,key,code,120);
        return Result.succeed(
                MapUtil.builder()
                        .put("key",key)
                        .put("captchaImg",base64Img)
                        .build()
        );
    }

    /**
     * 获取用户信息
     * @param principal
     * @return
     */
    @GetMapping("/userInfo")
    public Result getUserInfo(Principal principal) {
        User user = userService.getByUserName(principal.getName());
        return Result.succeed(MapUtil.builder()
                .put("id",user.getId())
                .put("username",user.getUsername())
                .put("avatar",user.getAvatar())
                .put("created",user.getCreated())
                .map()
        );
    }
}
