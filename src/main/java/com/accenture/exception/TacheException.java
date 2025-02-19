package com.accenture.exception;


/**
 * Classe d'exception métier concernant les taches
 */
public class TacheException extends RuntimeException {
  public TacheException(String message) {
    super(message);
  }
}
