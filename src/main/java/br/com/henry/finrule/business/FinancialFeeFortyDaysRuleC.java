package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FeeType;
import br.com.henry.finrule.model.entity.FinancialTransfer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Service
@Order(value = 10000)
public class FinancialFeeFortyDaysRuleC implements FinancialFeeRule {

    private static final Duration EXCLUSIVE_MIN_DURATION_TO_HANDLE = Duration.ofDays(40);
    private static final BigDecimal VARIABLE_FEE_FACTOR = new BigDecimal("0.02");
    private static final BigDecimal MINIMUM_VALUE_EXCLUSIVE = BigDecimal.valueOf(100_000);

    @Override
    public Optional<FeeParameters> inferFee(FinancialTransfer financialTransfer) {
        return financialTransfer.getValue().compareTo(MINIMUM_VALUE_EXCLUSIVE) <= 0 ? Optional.empty() : RuleUtils.applyVariableFeeIfSurpassesDuration(financialTransfer, VARIABLE_FEE_FACTOR, EXCLUSIVE_MIN_DURATION_TO_HANDLE, FeeType.C);
    }

}
