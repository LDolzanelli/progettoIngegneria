package it.unibs.ingsw.destinazioni.application.rest.advice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.swagger.v3.oas.annotations.Hidden;
import it.unibs.ingsw.destinazioni.application.exceptions.base.BaseUseCaseException;
import it.unibs.ingsw.destinazioni.ui.rest.advice.strategy.ExceptionHandlingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Hidden
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final List<ExceptionHandlingStrategy> strategies;

    /**
     * Gestisce tutte le eccezioni che estendono BaseUseCaseException
     * utilizzando il pattern Strategy appropriato.
     */
    @ExceptionHandler(BaseUseCaseException.class)
    public ResponseEntity<String> handleUseCaseException(BaseUseCaseException ex) {
        log.error("Gestione eccezione di caso d'uso: {} - {}", ex.getUseCaseType(), ex.getMessage(), ex);

        ExceptionHandlingStrategy strategy = findStrategy(ex);
        if (strategy != null) {
            return strategy.handleException(ex);
        }

        // Fallback se non trova una strategia specifica
        log.warn("Nessuna strategia trovata per il caso d'uso: {}", ex.getUseCaseType());
        return ResponseEntity.internalServerError().body("Si è verificato un errore imprevisto. Riprova più tardi.");
    }

    /**
     * Gestisce eccezioni generiche non specifiche dei servizi.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Argomento non valido: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body("Dati forniti non validi: " + ex.getMessage());
    }

    /**
     * Gestisce tutte le altre eccezioni non gestite specificamente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Errore generico non gestito: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body("Si è verificato un errore imprevisto. Riprova più tardi.");
    }

    /**
     * Trova la strategia appropriata per gestire l'eccezione.
     */
    private ExceptionHandlingStrategy findStrategy(Exception exception) {
        return strategies.stream().filter(strategy -> strategy.canHandle(exception)).findFirst().orElse(null);
    }
}
