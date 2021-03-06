package ar.edu.unsam.consorciovirtual.controller.advice;

import ar.edu.unsam.consorciovirtual.businessExceptions.DataConsistencyException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException excepcion) {
        return new ResponseEntity<String>(excepcion.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException excepcion) {
        return new ResponseEntity<String>(excepcion.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataConsistencyException.class)
    public ResponseEntity<String> handleDataConsistencyException(DataConsistencyException excepcion){
        return new ResponseEntity<String>(excepcion.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
