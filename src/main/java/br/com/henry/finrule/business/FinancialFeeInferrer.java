package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FinancialTransfer;

public interface FinancialFeeInferrer {
    FeeParameters inferFee(FinancialTransfer financialTransfer);
}
