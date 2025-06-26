package com.mattorow.jiraplugin.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.mattorow.jiraplugin.model.TelegramBinding;
import net.java.ao.Query;

import javax.inject.Named;
import java.util.UUID;

@Named
public class JiraUserBindingService {

    @JiraImport
    private ActiveObjects ao;

    public String getVerificationCode(String jiraUsername) {
        TelegramBinding[] bindings = ao.find(TelegramBinding.class,
                Query.select().where("JIRA_USERNAME = ?", jiraUsername));

        return bindings.length > 0 ? bindings[0].getVerificationCode() : generateVerificationCode(jiraUsername);
    }

    public String generateVerificationCode(String jiraUsername) {
        String code = UUID.randomUUID().toString().substring(0, 6);

        TelegramBinding binding = ao.create(TelegramBinding.class);
        binding.setJiraUsername(jiraUsername);
        binding.setVerificationCode(code);
        binding.setIsVerified(false);
        binding.save();

        return code;
    }

    public boolean verifyBinding(String code, long chatId) {
        TelegramBinding[] bindings = ao.find(TelegramBinding.class,
                Query.select().where("VERIFICATION_CODE = ?", code));

        if (bindings.length > 0) {
            TelegramBinding binding = bindings[0];
            binding.setTelegramChatId(chatId);
            binding.setIsVerified(true);
            binding.save();
            return true;
        }
        return false;
    }

    public Long getChatIdForUser(String jiraUsername) {
        TelegramBinding[] bindings = ao.find(TelegramBinding.class,
                Query.select().where("JIRA_USERNAME = ? AND IS_VERIFIED = ?", jiraUsername, true));

        return bindings.length > 0 ? bindings[0].getTelegramChatId() : null;
    }

    public String getUsernameByChatId(Long chatId) {
        TelegramBinding[] bindings = ao.find(TelegramBinding.class,
                Query.select().where("TELEGRAM_CHAT_ID = ?", chatId));
        return bindings.length > 0 ? bindings[0].getJiraUsername() : null;
    }
}
