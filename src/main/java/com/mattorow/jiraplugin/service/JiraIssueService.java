package com.mattorow.jiraplugin.service;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.query.Query;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;

@Named
public class JiraIssueService {

    public List<Issue> getOpenIssues(String username) {
        try {
            ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(username);

            if (user == null) return Collections.emptyList();

            JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser.class);
            SearchService searchService = ComponentAccessor.getComponent(SearchService.class);

            Query query = jqlQueryParser.parseQuery("status = Open");
            SearchService.ParseResult parseResult = searchService.parseQuery(user, query.getQueryString());

            if (parseResult.isValid()) {
                return searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter()).getResults();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
