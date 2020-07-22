package br.com.henry.finrule.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "financialTransfers")
@DynamicUpdate
@Data
@NoArgsConstructor
public class FinancialTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Nullable
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @NotNull(message = "Source account is mandatory")
    @Valid
    private BankAccount sourceAccount;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
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

    @Future
    @NotNull(message = "Financial transfer transfer date is mandatory")
    private ZonedDateTime transferDate;

    @NotNull(message = "Financial transfer fee type is mandatory")
    private FeeType feeType;

}
