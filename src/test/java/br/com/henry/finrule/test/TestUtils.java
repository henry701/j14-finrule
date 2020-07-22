package br.com.henry.finrule.test;

import br.com.henry.finrule.model.entity.BankAccount;
import br.com.henry.finrule.model.entity.FeeType;
import br.com.henry.finrule.model.entity.FinancialTransfer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

public final class TestUtils {

    private TestUtils() {
        // Static class
    }

    public static BankAccount getStandardTestBankAccount() {
        return buildTestAccount("000001");
    }

    public static BankAccount buildTestAccount(String accountNumber) {
        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        return account;
    }

    public static FinancialTransfer getStandardTestFinancialTransfer() {
        FinancialTransfer financialTransfer = new FinancialTransfer();
        financialTransfer.setId(1L);
        financialTransfer.setSchedulingDate(ZonedDateTime.now());
        financialTransfer.setSourceAccount(getStandardTestBankAccount());
        financialTransfer.setDestinationAccount(getStandardTestBankAccount());
        financialTransfer.setFeeType(FeeType.B);
        financialTransfer.setFee(BigDecimal.ONE);
        financialTransfer.setValue(BigDecimal.valueOf(5));
        financialTransfer.setTransferDate(ZonedDateTime.now().plus(Duration.ofDays(5)));
        return financialTransfer;
    }

}
