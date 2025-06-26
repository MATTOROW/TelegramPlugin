package com.mattorow.jiraplugin.model;

import net.java.ao.Entity;

public interface TelegramBinding extends Entity {
    String getJiraUsername();

    void setJiraUsername(String username);

    Long getTelegramChatId();

    void setTelegramChatId(Long chatId);

    String getVerificationCode();

    void setVerificationCode(String code);

    boolean getIsVerified();

    void setIsVerified(boolean verified);
}