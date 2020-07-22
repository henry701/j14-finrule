package br.com.henry.finrule.model.dto;

import br.com.henry.finrule.model.entity.BankAccount;
import br.com.henry.finrule.model.entity.FeeType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class FinancialTransferResponse {

    @NotNull(message = "Transfer ID is mandatory")
    private Long id;

    @NotNull(message = "Source account is mandatory")
    @Valid
    private BankAccount sourceAccount;

    @NotNull(message = "Destination account is mandatory")
    @Valid
    private BankAccount destinationAccount;

    @NotNull(message = "Financial transfer value is mandatory")
    @Min(value = 0, message = "Financial transfer value cannot be negative")
    private BigDecimal value;

    @NotNull(message = "Financial transfer fee is mandatory")
    @Min(value = 0, message = "Financial transfer fee cannot be negative")
    private BigDecimal fee;

    @PastOrPresent
    @NotNull(message = "Financial transfer scheduling date is mandatory")
    private ZonedDateTime schedulingDate;

    @NotNull(message = "Financial transfer transfer date is mandatory")
    private ZonedDateTime transferDate;

    @NotNull(message = "Financial transfer fee type is mandatory")
    private FeeType feeType;

}
