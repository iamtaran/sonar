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

package org.sonar.core.issue.db;

import org.junit.Before;
import org.junit.Test;
import org.sonar.core.persistence.AbstractDaoTestCase;

import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

public class ActionPlanStatsDaoTest extends AbstractDaoTestCase {

  private ActionPlanStatsDao dao;

  @Before
  public void createDao() {
    dao = new ActionPlanStatsDao(getMyBatis());
  }

  @Test
  public void should_find_by_project() {
    setupData("should_find_by_project");

    Collection<ActionPlanStatsDto> result = dao.findByProjectId(1l);
    assertThat(result).isNotEmpty();

    ActionPlanStatsDto actionPlanStatsDto = result.iterator().next();
    assertThat(actionPlanStatsDto.getTotalIssues()).isEqualTo(2);
    assertThat(actionPlanStatsDto.getOpenIssues()).isEqualTo(1);
  }

}