package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FeeType;
import br.com.henry.finrule.model.entity.FinancialTransfer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Order(value = 100)
public class FinancialFeeSameDayRuleA implements FinancialFeeRule {

    public static final BigDecimal FIXED_FEE = BigDecimal.valueOf(3);
    public static final BigDecimal VARIABLE_FEE_FACTOR = new BigDecimal("0.03");

    @Override
    public Optional<FeeParameters> inferFee(FinancialTransfer financialTransfer) {
        if(!DateUtils.normalizeToDate(financialTransfer.getSchedulingDate()).isEqual(DateUtils.normalizeToDate(financialTransfer.getTransferDate()))) {
            return Optional.empty();
        }
        return Optional.of(new FeeParameters(FIXED_FEE.add(financialTransfer.getValue().multiply(VARIABLE_FEE_FACTOR)), FeeType.A));
    }

}
