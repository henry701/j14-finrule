package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FeeType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FeeParameters {

    private BigDecimal fee;

    private FeeType feeType;

}
