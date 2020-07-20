package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FeeType;
import br.com.henry.finrule.model.entity.FinancialTransfer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Order(value = 200)
public class FinancialFeeTenDaysRuleB implements FinancialFeeRule {

    public static final Duration MAX_DURATION_TO_HANDLE = Duration.ofDays(10);
    public static final BigDecimal FEE_PER_DAY = BigDecimal.valueOf(12);

    @Override
    public Optional<FeeParameters> inferFee(FinancialTransfer financialTransfer) {
        ZonedDateTime scheduleDateTime = DateUtils.normalizeToDate(financialTransfer.getSchedulingDate());
        ZonedDateTime transferDateTime = DateUtils.normalizeToDate(financialTransfer.getTransferDate());
        Duration gapBetweenDates = Duration.between(scheduleDateTime, transferDateTime);
        if(gapBetweenDates.compareTo(MAX_DURATION_TO_HANDLE) > 0) {
            return Optional.empty();
        }
        return Optional.of(new FeeParameters(FEE_PER_DAY.multiply(BigDecimal.valueOf(gapBetweenDates.toDays())), FeeType.B));
    }

}
