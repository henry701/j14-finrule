package br.com.henry.finrule.controller;

import br.com.henry.finrule.business.FeeParameters;
import br.com.henry.finrule.business.FinancialFeeInferrer;
import br.com.henry.finrule.model.dto.FinancialTransferCreation;
import br.com.henry.finrule.model.dto.FinancialTransferResponse;
import br.com.henry.finrule.model.entity.BankAccount;
import br.com.henry.finrule.model.entity.FinancialTransfer;
import br.com.henry.finrule.repository.BankAccountRepository;
import br.com.henry.finrule.repository.FinancialTransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@Slf4j
@Validated
public class FinancialTransferController {

    private final FinancialTransferRepository financialTransferRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper modelMapper;
    private final FinancialFeeInferrer financialFeeInferrer;

    public FinancialTransferController(@Autowired FinancialTransferRepository financialTransferRepository, @Autowired ModelMapper modelMapper, @Autowired FinancialFeeInferrer financialFeeInferrer, @Autowired BankAccountRepository bankAccountRepository) {
        this.financialTransferRepository = financialTransferRepository;
        this.modelMapper = modelMapper;
        this.financialFeeInferrer = financialFeeInferrer;
        this.bankAccountRepository = bankAccountRepository;
    }

    @PostMapping(value = "/financialTransfer", consumes = MediaType.APPLICATION_JSON_VALUE, name = "schedule-financial-transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinancialTransferResponse> create(@Valid @RequestBody FinancialTransferCreation financialTransferCreation) {
        FinancialTransfer financialTransfer = modelMapper.map(financialTransferCreation, FinancialTransfer.class);
        financialTransfer.setSchedulingDate(ZonedDateTime.now());
        FeeParameters feeParameters = financialFeeInferrer.inferFee(financialTransfer);
        financialTransfer.setFee(feeParameters.getFee());
        financialTransfer.setFeeType(feeParameters.getFeeType());
        financialTransfer.setDestinationAccount(findBankAccountOrDefault(financialTransfer.getDestinationAccount()));
        financialTransfer.setSourceAccount(findBankAccountOrDefault(financialTransfer.getSourceAccount()));
        financialTransfer = financialTransferRepository.save(financialTransfer);
        FinancialTransferResponse financialTransferResponse = modelMapper.map(financialTransfer, FinancialTransferResponse.class);
        return ResponseEntity.ok().body(financialTransferResponse);
    }

    private BankAccount findBankAccountOrDefault(BankAccount bankAccount) {
        return bankAccountRepository.findByAccountNumber(bankAccount.getAccountNumber()).orElse(bankAccount);
    }

    @GetMapping(value = "/financialTransfer/{id}", name = "get-financial-transfer-by-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FinancialTransferResponse> getById(@PathVariable(name = "id") Long id) {
        return financialTransferRepository.findById(id)
            .map(financialTransfer -> ResponseEntity.ok(modelMapper.map(financialTransfer, FinancialTransferResponse.class)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/financialTransfer/{id}", name = "cancel-financial-transfer-by-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        return financialTransferRepository.findById(id)
            .map(financialTransfer -> {
                if (financialTransfer.getTransferDate().isBefore(ZonedDateTime.now())) {
                    return ResponseEntity.unprocessableEntity().body("Cannot cancel a transfer that has already been done!");
                }
                financialTransferRepository.deleteById(id);
                return ResponseEntity.ok(modelMapper.map(financialTransfer, FinancialTransferResponse.class));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/financialTransfer", name = "get-financial-transfer-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Stream<FinancialTransferResponse>> list() {
        return ResponseEntity.ok(StreamSupport.stream(financialTransferRepository.findAll().spliterator(), true)
            .map(c -> modelMapper.map(c, FinancialTransferResponse.class)));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        return errors;
    }

}
