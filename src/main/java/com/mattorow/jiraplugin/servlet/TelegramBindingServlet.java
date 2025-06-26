package com.mattorow.jiraplugin.servlet;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.mattorow.jiraplugin.service.JiraUserBindingService;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TelegramBindingServlet extends HttpServlet {
    private final TemplateRenderer renderer;
    private final JiraUserBindingService bindingService;

    public TelegramBindingServlet(
            @ComponentImport TemplateRenderer renderer,
            JiraUserBindingService bindingService) {
        this.renderer = renderer;
        this.bindingService = bindingService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getRemoteUser();
        String verificationCode = getOrCreateVerificationCode(username);

        Map<String, Object> context = new HashMap<>();
        context.put("verificationCode", verificationCode);
        context.put("jiraUsername", username);

        renderer.render("templates/telegram-bind.vm", context, resp.getWriter());
    }

    private String getOrCreateVerificationCode(String username) {
        String existingCode = bindingService.getVerificationCode(username);
        if (existingCode != null) {
            return existingCode;
        }

        return bindingService.generateVerificationCode(username);
    }
}
