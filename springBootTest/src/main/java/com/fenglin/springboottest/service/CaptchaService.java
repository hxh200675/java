package com.fenglin.springboottest.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数字图形验证码：服务端内存存储，5 分钟过期，供注册等接口防刷校验。
 * 单实例开发环境够用；生产建议替换为 Redis 等集中存储。
 */
@Service
public class CaptchaService {

    private static final int WIDTH = 110;
    private static final int HEIGHT = 40;
    private static final int LENGTH = 4;                 // 4 位数字
    private static final long EXPIRE_MS = 5 * 60 * 1000; // 5 分钟

    /** key -> 验证码信息（一次性使用，校验后删除） */
    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    /** 生成验证码，返回前端所需的 key 与 base64 图片 */
    public CaptchaVO generate() {
        cleanup();
        String code = randomCode(LENGTH);
        String key = UUID.randomUUID().toString().replace("-", "");
        store.put(key, new Entry(code, System.currentTimeMillis() + EXPIRE_MS));
        return new CaptchaVO(key, render(code));
    }

    /** 校验验证码：无效/过期/已被使用均返回 false，且校验后无论成败都销毁 */
    public boolean validate(String key, String code) {
        if (key == null || code == null) {
            return false;
        }
        Entry entry = store.remove(key); // 一次性使用
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() > entry.expire) {
            return false; // 已过期
        }
        return code.trim().equals(entry.code);
    }

    /** 渲染成 base64 PNG */
    private static String render(String code) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 干扰线
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(rand(0, 255), rand(0, 255), rand(0, 255)));
            g.drawLine(rand(0, WIDTH), rand(0, HEIGHT), rand(0, WIDTH), rand(0, HEIGHT));
        }
        // 数字（深色，避免与干扰线混淆）
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(rand(0, 120), rand(0, 120), rand(0, 120)));
            g.drawString(String.valueOf(code.charAt(i)), 15 + i * 22, 30);
        }
        g.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }

    private static String randomCode(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(rand(0, 9));
        }
        return sb.toString();
    }

    private static int rand(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    /** 惰性清理过期项 */
    private void cleanup() {
        long now = System.currentTimeMillis();
        store.entrySet().removeIf(e -> e.getValue().expire < now);
    }

    /** 单个验证码记录 */
    private static class Entry {
        final String code;
        final long expire;
        Entry(String code, long expire) {
            this.code = code;
            this.expire = expire;
        }
    }

    /** 返回给前端的验证码视图对象 */
    public static class CaptchaVO {
        private String captchaKey;
        private String image;

        public CaptchaVO(String captchaKey, String image) {
            this.captchaKey = captchaKey;
            this.image = image;
        }

        public String getCaptchaKey() {
            return captchaKey;
        }

        public void setCaptchaKey(String captchaKey) {
            this.captchaKey = captchaKey;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
