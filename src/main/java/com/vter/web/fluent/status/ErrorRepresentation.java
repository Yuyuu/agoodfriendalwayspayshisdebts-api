package com.vter.web.fluent.status;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ErrorRepresentation {

  private ErrorRepresentation() {}

  public static ErrorRepresentation forErrorMessages(List<String> messages) {
    final ErrorRepresentation representation = new ErrorRepresentation();
    messages.forEach(representation::addErrorMessage);
    return representation;
  }

  public List<Map<String, String>> errors() {
    return errors;
  }

  public void addErrorMessage(String errorMessage) {
    final Map<String, String> error = Maps.newHashMap();
    error.put("message", errorMessage);
    errors.add(error);
  }

  private final List<Map<String, String>> errors = Lists.newArrayList();
}
