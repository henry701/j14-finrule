package br.com.henry.finrule.model.dto;

import br.com.henry.finrule.model.entity.BankAccount;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class FinancialTransferCreation {

    @NotNull(message = "Source account is mandatory")
    private BankAccount sourceAccount;

    @NotNull(message = "Destination account is mandatory")
    private BankAccount destinationAccount;

    @NotNull(message = "Financial transfer value is mandatory")
    private BigDecimal value;

    @Future
    @NotNull(message = "Financial transfer transfer date is mandatory")
    private ZonedDateTime transferDate;

}
