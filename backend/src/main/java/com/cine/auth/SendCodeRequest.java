package com.cine.auth;

/**
 * 发送验证码请求体。
 */
public class SendCodeRequest {
    /** 手机号或邮箱 */
    private String account;
    /** 下发渠道：sms（短信） / email（邮件） */
    private String channel;

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
