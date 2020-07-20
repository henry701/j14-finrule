package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FinancialTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FinancialFeeRuleInferrer implements FinancialFeeInferrer {

    private final List<FinancialFeeRule> financialFeeRuleList;

    public FinancialFeeRuleInferrer(@Autowired List<FinancialFeeRule> financialFeeRuleList) {
        this.financialFeeRuleList = financialFeeRuleList;
    }

    @Override
    public FeeParameters inferFee(FinancialTransfer financialTransfer) {
        return financialFeeRuleList
            .parallelStream()
            .map(rule -> rule.inferFee(financialTransfer))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No fee rule is applicable for financialTransfer " + financialTransfer));
    }

}
