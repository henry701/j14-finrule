package br.com.henry.finrule.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "bankAccounts")
@DynamicUpdate
@Data
@NoArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Nullable
    private Long id;

    @NotNull(message = "Bank account number is mandatory")
    @Pattern(regexp = "[0-9]{6}", message = "Bank account number must have 6 digits")
    private String accountNumber;

}
