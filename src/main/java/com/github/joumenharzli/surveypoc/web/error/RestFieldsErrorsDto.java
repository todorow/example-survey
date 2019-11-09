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

package com.github.joumenharzli.surveypoc.web.error;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import io.swagger.annotations.ApiModel;

/**
 * A representation for the rest fields errors list
 *
 * @author Joumen Harzli
 */
@ApiModel("RestFieldsErrorsDto")
public class RestFieldsErrorsDto extends RestErrorDto implements Serializable {

  private List<RestFieldErrorDto> fieldsErrors;

  /**
   * Constructor for the rest fields errors entity
   */
  public RestFieldsErrorsDto(String code, String message) {
    super(code, message);
    fieldsErrors = new ArrayList<>();
  }

  /**
   * Add a rest field error to the list of fields errors
   *
   * @param error error to add to the list
   */
  public void addError(RestFieldErrorDto error) {
    Assert.notNull(error, "Cannot add a null error to the list of fields errors");

    if (fieldsErrors == null) {
      fieldsErrors = new ArrayList<>();
    }

    fieldsErrors.add(error);
  }

  /**
   * Get the list of the fields errors
   *
   * @return list of fields errors
   */
  public List<RestFieldErrorDto> getFieldsErrors() {
    return fieldsErrors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RestFieldsErrorsDto that = (RestFieldsErrorsDto) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(fieldsErrors, that.fieldsErrors)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(fieldsErrors)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("fieldsErrors", fieldsErrors)
        .toString();
  }
}
