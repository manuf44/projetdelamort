package com.accenture.controller.advice;


import com.accenture.exception.TacheException;
import com.accenture.model.ErreurReponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApplicationControllerAdvice {


    @ExceptionHandler(TacheException.class)
    public ResponseEntity<ErreurReponse> gestionTacheException(TacheException ex){
        ErreurReponse er = new ErreurReponse(LocalDateTime.now(), "Erreur fonctionnelle", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(er);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErreurReponse> entityNotFoundException(EntityNotFoundException ex){
        ErreurReponse er = new ErreurReponse(LocalDateTime.now(), "Mauvaise Requete", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(er);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurReponse> problemeValidation(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult().getAllErrors()
                .stream()
                .map(objectError -> objectError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErreurReponse er = new ErreurReponse(LocalDateTime.now(), "Validation erreur", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(er);
    }

}
