package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FinancialTransfer;

import java.util.Optional;

public interface FinancialFeeRule {
    Optional<FeeParameters> inferFee(FinancialTransfer financialTransfer);
}
