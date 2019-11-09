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

package com.github.joumenharzli.surveypoc.service.dto;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * User Responses List
 * A wrapper for a list of user response for question dto
 * This useful to apply {@code Bean validations}
 *
 * @author Joumen Harzli
 */
public class UserResponsesForQuestionListDto {

  @Valid
  @NotEmpty
  private List<UserResponseForQuestionDto> responses;

  public UserResponsesForQuestionListDto() {
    this.responses = new ArrayList<>();
  }

  public List<UserResponseForQuestionDto> getResponses() {
    return responses;
  }

  public void setResponses(List<UserResponseForQuestionDto> responses) {
    this.responses = responses;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UserResponsesForQuestionListDto that = (UserResponsesForQuestionListDto) o;

    return new EqualsBuilder()
        .append(responses, that.responses)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(responses)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("responses", responses)
        .toString();
  }
}
