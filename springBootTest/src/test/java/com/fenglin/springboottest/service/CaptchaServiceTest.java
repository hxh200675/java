package com.fenglin.springboottest.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证码核心逻辑测试：生成 -> 正确码通过、错误码拒绝、一次性消费。
 */
class CaptchaServiceTest {

    @SuppressWarnings("unchecked")
    private String peekCode(CaptchaService svc, String key) throws Exception {
        Field storeField = CaptchaService.class.getDeclaredField("store");
        storeField.setAccessible(true);
        Map<String, Object> store = (Map<String, Object>) storeField.get(svc);
        Object entry = store.get(key);
        assertNotNull(entry, "生成的验证码应已存入内存");
        Field codeField = entry.getClass().getDeclaredField("code");
        codeField.setAccessible(true);
        return (String) codeField.get(entry);
    }

    @Test
    void generateAndValidate() throws Exception {
        CaptchaService svc = new CaptchaService();

        // 1) 正确码 -> 通过
        CaptchaService.CaptchaVO vo1 = svc.generate();
        assertNotNull(vo1.getCaptchaKey());
        assertNotNull(vo1.getImage());
        String code1 = peekCode(svc, vo1.getCaptchaKey());
        assertTrue(svc.validate(vo1.getCaptchaKey(), code1), "正确验证码应通过校验");

        // 2) 错误码 -> 拒绝（同时消费掉 key）
        CaptchaService.CaptchaVO vo2 = svc.generate();
        String code2 = peekCode(svc, vo2.getCaptchaKey());
        assertEquals(4, code2.length(), "验证码应为 4 位数字");
        assertFalse(svc.validate(vo2.getCaptchaKey(), "9999"), "错误验证码应被拒绝");

        // 3) 已被消费的 key 再次使用 -> 拒绝
        assertFalse(svc.validate(vo2.getCaptchaKey(), code2), "已使用的验证码 key 不应再次通过");
    }
}
