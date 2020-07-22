package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FeeType;
import br.com.henry.finrule.model.entity.FinancialTransfer;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

public final class RuleUtils {

    private RuleUtils() {
        // Static class
    }

    public static Optional<FeeParameters> applyVariableFeeIfBetweenDurations(FinancialTransfer financialTransfer, BigDecimal variableFeeFactor, Duration minDurationExclusive, Duration maxDurationInclusive, FeeType feeType) {
        Duration gapBetweenDates = DateUtils.calculateNormalizedGap(financialTransfer.getSchedulingDate(), financialTransfer.getTransferDate());
        if(gapBetweenDates.compareTo(minDurationExclusive) <= 0 || gapBetweenDates.compareTo(maxDurationInclusive) > 0) {
            return Optional.empty();
        }
        return Optional.of(new FeeParameters(financialTransfer.getValue().multiply(variableFeeFactor), feeType));
    }

    public static Optional<FeeParameters> applyVariableFeeIfSurpassesDuration(FinancialTransfer financialTransfer, BigDecimal variableFeeFactor, Duration minDurationExclusive, FeeType feeType) {
        Duration gapBetweenDates = DateUtils.calculateNormalizedGap(financialTransfer.getSchedulingDate(), financialTransfer.getTransferDate());
        if(gapBetweenDates.compareTo(minDurationExclusive) <= 0) {
            return Optional.empty();
        }
        return Optional.of(new FeeParameters(financialTransfer.getValue().multiply(variableFeeFactor), feeType));
    }

}
