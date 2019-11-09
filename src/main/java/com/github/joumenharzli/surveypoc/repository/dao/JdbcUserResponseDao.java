/*
 * Copyright (C) 2018 Joumen Harzli
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.github.joumenharzli.surveypoc.repository.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.github.joumenharzli.surveypoc.domain.UserResponse;

/**
 * JDBC implementation for {@link UserResponseDao}
 *
 * @author Joumen Harzli
 */
@Repository
public class JdbcUserResponseDao implements UserResponseDao {

  private static final String INSERT_USER_RESPONSE = "INSERT INTO user_responses (content,user_id,question_id) " +
      "VALUES (?, ?, ?)";

  private static final String SELECT_USER_RESPONSES_FOR_QUESTIONS = "SELECT ur.content AS content, " +
      "ur.question_id AS question_id, ur.user_id AS user_id FROM user_responses AS ur WHERE ur.user_id = :user_id " +
      "AND ur.question_id IN (:question_ids) ORDER BY ur.question_id,ur.user_id";

  private static final String UPDATE_USER_RESPONSE = "UPDATE user_responses SET content = :content " +
      "WHERE user_id = :user.id AND question_id = :question.id";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate parameterJdbcTemplate;

  private final RowMapper<UserResponse> mapper = JdbcTemplateMapperFactory
      .newInstance()
      .addKeys("id", "question_id", "user_id")
      .newRowMapper(UserResponse.class);

  public JdbcUserResponseDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate parameterJdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.parameterJdbcTemplate = parameterJdbcTemplate;
  }

  /**
   * Add a new responses of the user
   *
   * @param userResponses entities to save
   * @return an array of the number of rows affected by each statement
   * @throws DaoException             if there is an sql exception
   * @throws IllegalArgumentException if any given argument is invalid
   */
  @Override
  public int[] addUserResponses(List<UserResponse> userResponses) {

    Assert.notEmpty(userResponses, "User responses cannot be null or empty");

    try {
      return jdbcTemplate.batchUpdate(INSERT_USER_RESPONSE, userResponseBatchPreparedStatementSetter(userResponses));
    } catch (Exception exception) {
      throw new DaoException("Unable to add responses of the questions for the user", exception);
    }

  }

  /**
   * Update responses of the user
   *
   * @param userResponses entities to save
   * @return an array of the number of rows affected by each statement
   * @throws DaoException             if there is an sql exception
   * @throws IllegalArgumentException if any given argument is invalid
   */
  @Override
  public int[] updateUserResponses(List<UserResponse> userResponses) {

    Assert.notEmpty(userResponses, "User responses cannot be null or empty");

    SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(userResponses.toArray());

    try {
      return parameterJdbcTemplate.batchUpdate(UPDATE_USER_RESPONSE, batchParams);
    } catch (Exception exception) {
      throw new DaoException("Unable to update responses of the questions for the user", exception);
    }
  }

  /**
   * Find the responses for the provided questions and user
   *
   * @param userId       user who responded
   * @param questionsIds questions that the user may responded
   * @return list of responses
   * @throws DaoException             if there is an sql exception
   * @throws IllegalArgumentException if any given argument is invalid
   */
  @Override
  public List<UserResponse> findResponsesOfUserByUserIdAndQuestionIds(Long userId, List<Long> questionsIds) {

    Assert.notNull(userId, "User id cannot be null");
    Assert.notEmpty(questionsIds, "Questions ids cannot be null or empty");
    questionsIds.forEach(questionId -> Assert.notNull(questionId, "Id of the question cannot be null"));

    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("question_ids", questionsIds);
    parameters.addValue("user_id", userId);

    try {
      return parameterJdbcTemplate.query(SELECT_USER_RESPONSES_FOR_QUESTIONS, parameters, mapper);
    } catch (Exception exception) {
      throw new DaoException("Unable to find responses of the user for the questions", exception);
    }
  }

  /**
   * Batch update callback defines the way that the batch insertion
   * of the user responses will be executed
   *
   * @param userResponses list of the user responses
   * @return an instance of {@link BatchPreparedStatementSetter}
   */
  private BatchPreparedStatementSetter userResponseBatchPreparedStatementSetter(List<UserResponse> userResponses) {
    return new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        UserResponse userResponse = userResponses.get(i);
        Assert.notNull(userResponse, "User response cannot be null");

        Long questionId = userResponse.getQuestionId();
        Long userId = userResponse.getUserId();

        Assert.notNull(questionId, "Question id in the user response entity cannot be null");
        Assert.notNull(userId, "User id in the user response entity cannot be null");

        ps.setString(1, userResponse.getContent());
        ps.setLong(2, userId);
        ps.setLong(3, questionId);
      }

      @Override
      public int getBatchSize() {
        return userResponses.size();
      }
    };
  }

}
