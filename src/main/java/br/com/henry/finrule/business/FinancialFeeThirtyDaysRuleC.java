package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FeeType;
import br.com.henry.finrule.model.entity.FinancialTransfer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Service
@Order(value = 16000)
public class FinancialFeeThirtyDaysRuleC implements FinancialFeeRule {

    private static final Duration EXCLUSIVE_MIN_DURATION_TO_HANDLE = Duration.ofDays(30);
    private static final Duration INCLUSIVE_MAX_DURATION_TO_HANDLE = Duration.ofDays(40);
    private static final BigDecimal VARIABLE_FEE_FACTOR = new BigDecimal("0.04");

    @Override
    public Optional<FeeParameters> inferFee(FinancialTransfer financialTransfer) {
        return RuleUtils.applyVariableFeeIfBetweenDurations(financialTransfer, VARIABLE_FEE_FACTOR, EXCLUSIVE_MIN_DURATION_TO_HANDLE, INCLUSIVE_MAX_DURATION_TO_HANDLE, FeeType.C);
    }

}
