package br.com.henry.finrule.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class BankAccountInput {

    @NotBlank(message = "Bank account number is mandatory")
    @Pattern(regexp = "[0-9]{6}", message = "Bank account number must have 6 digits")
    private String accountNumber;

}
