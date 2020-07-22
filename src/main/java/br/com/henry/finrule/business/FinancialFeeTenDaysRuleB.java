package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FeeType;
import br.com.henry.finrule.model.entity.FinancialTransfer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Service
@Order(value = 200)
public class FinancialFeeTenDaysRuleB implements FinancialFeeRule {

    private static final Duration INCLUSIVE_MAX_DURATION_TO_HANDLE = Duration.ofDays(10);
    private static final BigDecimal FEE_PER_DAY = BigDecimal.valueOf(12);

    @Override
    public Optional<FeeParameters> inferFee(FinancialTransfer financialTransfer) {
        Duration gapBetweenDates = DateUtils.calculateNormalizedGap(financialTransfer.getSchedulingDate(), financialTransfer.getTransferDate());
        if(gapBetweenDates.compareTo(INCLUSIVE_MAX_DURATION_TO_HANDLE) > 0) {
            return Optional.empty();
        }
        return Optional.of(new FeeParameters(FEE_PER_DAY.multiply(BigDecimal.valueOf(gapBetweenDates.toDays())), FeeType.B));
    }

}
