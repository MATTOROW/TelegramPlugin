package com.mattorow.jiraplugin.telegrambot;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.mattorow.jiraplugin.config.PropertyLoader;
import com.mattorow.jiraplugin.service.JiraIssueService;
import com.mattorow.jiraplugin.service.JiraUserBindingService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class TelegramBotComponent {
    private final TelegramBot bot;
    private final JiraIssueService jiraIssueService;
    private final JiraUserBindingService bindingService;
    private final PropertyLoader propertyLoader;

    @Inject
    public TelegramBotComponent(
            JiraIssueService jiraIssueService,
            JiraUserBindingService bindingService,
            PropertyLoader propertyLoader) {
        this.propertyLoader = propertyLoader;
        String botToken = propertyLoader.get("telegram.bot.token");
        this.bot = new TelegramBot(botToken);
        this.jiraIssueService = jiraIssueService;
        this.bindingService = bindingService;

        init();
    }

    public void init() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                handleUpdate(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }

    private void handleUpdate(Update update) {
        if (update.message() == null || update.message().text() == null) return;

        Long chatId = update.message().chat().id();
        String text = update.message().text();

        try {
            if ("/start".equals(text)) {
                sendHelpMessage(chatId);
            } else if ("/issues".equals(text)) {
                handleIssuesCommand(chatId);
            } else if (text.startsWith("/bind")) {
                handleBindCommand(chatId, text);
            }
        } catch (Exception e) {
            bot.execute(new SendMessage(chatId, "⚠️ Error: " + e.getMessage()));
        }
    }

    private void sendHelpMessage(Long chatId) {
        String helpText = "🤖 *Jira Integration Bot*\nAvailable commands:\n/issues - Get your open Jira issues\n/bind <code> - Link your Telegram account\n";

        bot.execute(new SendMessage(chatId, helpText).parseMode(ParseMode.Markdown));
    }

    private void handleIssuesCommand(Long chatId) {
        String jiraUsername = bindingService.getUsernameByChatId(chatId);
        if (jiraUsername == null) {
            bot.execute(new SendMessage(chatId,
                    "🔒 Please bind your Jira account first using /bind <code>"));
            return;
        }

        List<Issue> issues = jiraIssueService.getOpenIssues(jiraUsername);
        if (issues == null || issues.isEmpty()) {
            bot.execute(new SendMessage(chatId, "No open issues found"));
            return;
        }

        StringBuilder response = new StringBuilder("📋 *Your Open Issues:*\n\n");
        for (Issue issue : issues) {
            response.append(String.format(
                    "• [%s](%s/browse/%s) - %s\n",
                    issue.getKey(),
                    ComponentAccessor.getApplicationProperties().getString("jira.baseurl"),
                    issue.getKey(),
                    issue.getSummary()
            ));
        }

        bot.execute(new SendMessage(chatId, response.toString())
                .parseMode(ParseMode.Markdown)
                .disableWebPagePreview(true));
    }

    private void handleBindCommand(Long chatId, String text) {
        String[] parts = text.split(" ");
        if (parts.length != 2) {
            bot.execute(new SendMessage(chatId,
                    "Usage: /bind <verification_code>"));
            return;
        }

        String code = parts[1];
        boolean success = bindingService.verifyBinding(code, chatId);

        bot.execute(new SendMessage(chatId,
                success ? "✅ Account linked successfully!" : "❌ Invalid verification code"));
    }
}

