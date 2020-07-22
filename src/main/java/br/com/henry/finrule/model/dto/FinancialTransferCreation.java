package br.com.henry.finrule.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class FinancialTransferCreation {

    @NotNull(message = "Source account is mandatory")
    @Valid
    private BankAccountInput sourceAccount;

    @NotNull(message = "Destination account is mandatory")
    @Valid
    private BankAccountInput destinationAccount;

    @NotNull(message = "Financial transfer value is mandatory")
    @Min(value = 0, message = "Financial transfer value cannot be negative")
    private BigDecimal value;

    @Future
    @NotNull(message = "Financial transfer transfer date is mandatory")
    private ZonedDateTime transferDate;

}
