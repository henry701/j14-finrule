package br.com.henry.finrule.controller;

import br.com.henry.finrule.model.dto.FinancialTransferCreation;
import br.com.henry.finrule.model.dto.FinancialTransactionResponse;
import br.com.henry.finrule.model.entity.FinancialTransfer;
import br.com.henry.finrule.repository.FinancialTransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@Slf4j
public class FinancialTransferController {

    private final FinancialTransferRepository financialTransferRepository;
    private final ModelMapper modelMapper;

    public FinancialTransferController(@Autowired FinancialTransferRepository financialTransferRepository, @Autowired ModelMapper modelMapper) {
        this.financialTransferRepository = financialTransferRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/financialTransaction", consumes = MediaType.APPLICATION_JSON_VALUE, name = "schedule-financial-transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinancialTransactionResponse> create(@Valid @RequestBody FinancialTransferCreation financialTransferCreation, HttpServletRequest request) {
        FinancialTransfer financialTransfer = modelMapper.map(financialTransferCreation, FinancialTransfer.class);
        financialTransfer = financialTransferRepository.save(financialTransfer);
        FinancialTransactionResponse financialTransactionResponse = modelMapper.map(financialTransfer, FinancialTransactionResponse.class);
        return ResponseEntity.ok().body(financialTransactionResponse);
    }

    @GetMapping(value = "/financialTransaction/{id}", name = "get-financial-transaction-by-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinancialTransactionResponse> getById(@PathVariable(name = "id") Long id) {
        return financialTransferRepository.findById(id)
            .map(financialTransfer -> ResponseEntity.ok(modelMapper.map(financialTransfer, FinancialTransactionResponse.class)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/financialTransaction/{id}", name = "cancel-financial-transaction-by-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinancialTransactionResponse> delete(@PathVariable(name = "id") Long id) {
        return financialTransferRepository.findById(id)
            .map(financialTransfer -> {
                financialTransferRepository.deleteById(id);
                return ResponseEntity.ok(modelMapper.map(financialTransfer, FinancialTransactionResponse.class));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/financialTransaction", name = "get-financial-transaction-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Stream<FinancialTransactionResponse>> list() {
        return ResponseEntity.ok(StreamSupport.stream(financialTransferRepository.findAll().spliterator(), true)
            .map(c -> modelMapper.map(c, FinancialTransactionResponse.class)));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        return errors;
    }

}
