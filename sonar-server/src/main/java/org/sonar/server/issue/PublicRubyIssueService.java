/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.issue;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.sonar.api.issue.IssueFinder;
import org.sonar.api.issue.IssueQuery;
import org.sonar.api.issue.IssueQueryResult;
import org.sonar.api.issue.RubyIssueService;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.web.UserRole;
import org.sonar.server.util.RubyUtils;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Facade of issue components for JRuby on Rails webapp
 *
 * @since 3.6
 */
public class PublicRubyIssueService implements RubyIssueService {

  private final IssueFinder finder;

  public PublicRubyIssueService(IssueFinder f) {
    this.finder = f;
  }

  /**
   * Requires the role {@link org.sonar.api.web.UserRole#CODEVIEWER}
   */
  @Override
  public IssueQueryResult find(Map<String, Object> params) {
    return finder.find(toQuery(params));
  }

  IssueQuery toQuery(Map<String, Object> props) {
    IssueQuery.Builder builder = IssueQuery.builder();
    builder.requiredRole(UserRole.CODEVIEWER);
    builder.issueKeys(RubyUtils.toStrings(props.get("issueKeys")));
    builder.severities(RubyUtils.toStrings(props.get("severities")));
    builder.statuses(RubyUtils.toStrings(props.get("statuses")));
    builder.resolutions(RubyUtils.toStrings(props.get("resolutions")));
    builder.resolved(RubyUtils.toBoolean(props.get("resolved")));
    builder.components(RubyUtils.toStrings(props.get("components")));
    builder.componentRoots(RubyUtils.toStrings(props.get("componentRoots")));
    builder.rules(toRules(props.get("rules")));
    builder.actionPlans(RubyUtils.toStrings(props.get("actionPlans")));
    builder.userLogins(RubyUtils.toStrings(props.get("userLogins")));
    builder.assignees(RubyUtils.toStrings(props.get("assignees")));
    builder.assigned(RubyUtils.toBoolean(props.get("assigned")));
    builder.planned(RubyUtils.toBoolean(props.get("planned")));
    builder.createdAfter(RubyUtils.toDate(props.get("createdAfter")));
    builder.createdBefore(RubyUtils.toDate(props.get("createdBefore")));
    builder.pageSize(RubyUtils.toInteger(props.get("pageSize")));
    builder.pageIndex(RubyUtils.toInteger(props.get("pageIndex")));
    String sort = (String) props.get("sort");
    if (sort != null) {
      builder.sort(IssueQuery.Sort.valueOf(sort));
    }
    return builder.build();
  }

  @SuppressWarnings("unchecked")
  static Collection<RuleKey> toRules(Object o) {
    Collection<RuleKey> result = null;
    if (o != null) {
      if (o instanceof List) {
        // assume that it contains only strings
        result = stringsToRules((List<String>) o);
      } else if (o instanceof String) {
        result = stringsToRules(Lists.newArrayList(Splitter.on(',').omitEmptyStrings().split((String) o)));
      }
    }
    return result;
  }

  private static Collection<RuleKey> stringsToRules(Collection<String> o) {
    return Collections2.transform(o, new Function<String, RuleKey>() {
      @Override
      public RuleKey apply(@Nullable String s) {
        return s != null ? RuleKey.parse(s) : null;
      }
    });
  }



  public void start() {
    // used to force pico to instantiate the singleton at startup
  }
}
