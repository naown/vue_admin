package com.naown.common.lang;

/**
 * @author: chenjian
 * @since: 2021/5/12 23:28 周三
 **/
public interface Const {
    /** redis验证码set的key */
    String CAPTCHA_KEY = "captcha";
    /** 正常 */
    Integer STATUS_ON = 1;
    /** 禁用 */
    Integer STATUS_OFF = 0;
    /** 默认密码 */
    String DEFAULT_PASSWORD = "888888";
    /** 默认头像 */
    String DEFAULT_AVATAR = "https://cdn.jsdelivr.net/gh/naown/imgs@main/user.png";
    /** 临时文件路径 */
    String TEMP_PATH = System.getProperty("java.io.tmpdir");
}
