package com.co.flypass.payments.bff.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class UnknownConnectorException extends RuntimeException {
  private final String connectorId;
  private final Set<String> allowed;

  public UnknownConnectorException(String connectorId, Set<String> allowed) {
    super("Unknown connector: " + connectorId);
    this.connectorId = connectorId;
    this.allowed = allowed;
  }

}
