package br.com.henry.finrule.business;

import br.com.henry.finrule.model.entity.FeeType;
import br.com.henry.finrule.model.entity.FinancialTransfer;
import br.com.henry.finrule.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@ComponentScan(basePackages={"br.com.henry.finrule"})
@EnableJpaRepositories(basePackages="br.com.henry.finrule.repository")
@EntityScan(basePackages = "br.com.henry.finrule.model.entity")
public class FinancialFeeRuleInferrerTest {

    @Autowired
    private FinancialFeeRuleInferrer financialFeeRuleInferrer;

    @Test
    public void testSameDayRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(10_000), Duration.ofMinutes(5));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(BigDecimal.valueOf(3).add(financialTransfer.getValue().multiply(new BigDecimal("0.03"))), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.A, inferredFeeParameters.getFeeType());
    }

    @Test
    public void testFiveDaysRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(10_000), Duration.ofDays(5));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(BigDecimal.valueOf(12).multiply(BigDecimal.valueOf(5)), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.B, inferredFeeParameters.getFeeType());
    }

    @Test
    public void testTenDaysRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(10_000), Duration.ofDays(10));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(BigDecimal.valueOf(12).multiply(BigDecimal.valueOf(10)), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.B, inferredFeeParameters.getFeeType());
    }

    @Test
    public void testFifteenDaysRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(10_000), Duration.ofDays(15));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(financialTransfer.getValue().multiply(new BigDecimal("0.08")), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.C, inferredFeeParameters.getFeeType());
    }

    @Test
    public void testTwentyDaysRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(10_000), Duration.ofDays(20));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(financialTransfer.getValue().multiply(new BigDecimal("0.08")), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.C, inferredFeeParameters.getFeeType());
    }

    @Test
    public void testTwentyFiveDaysRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(10_000), Duration.ofDays(25));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(financialTransfer.getValue().multiply(new BigDecimal("0.06")), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.C, inferredFeeParameters.getFeeType());
    }

    @Test
    public void testThirtyDaysRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(10_000), Duration.ofDays(30));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(financialTransfer.getValue().multiply(new BigDecimal("0.06")), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.C, inferredFeeParameters.getFeeType());
    }

    @Test
    public void testThirtyFiveDaysRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(10_000), Duration.ofDays(35));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(financialTransfer.getValue().multiply(new BigDecimal("0.04")), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.C, inferredFeeParameters.getFeeType());
    }

    @Test
    public void testFortyDaysRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(10_000), Duration.ofDays(40));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(financialTransfer.getValue().multiply(new BigDecimal("0.04")), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.C, inferredFeeParameters.getFeeType());
    }

    @Test
    public void testFortyFiveDaysRuleFee() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(100_001), Duration.ofDays(45));
        FeeParameters inferredFeeParameters = financialFeeRuleInferrer.inferFee(financialTransfer);
        Assert.assertEquals(financialTransfer.getValue().multiply(new BigDecimal("0.02")), inferredFeeParameters.getFee());
        Assert.assertEquals(FeeType.C, inferredFeeParameters.getFeeType());
    }

    @Test(expected = Exception.class)
    public void testFortyFiveDaysRuleFeeFailure() {
        FinancialTransfer financialTransfer = generateFinancialTransfer(BigDecimal.valueOf(1), Duration.ofDays(45));
        financialFeeRuleInferrer.inferFee(financialTransfer);
    }

    private FinancialTransfer generateFinancialTransfer(BigDecimal value, Duration transferDelay) {
        ZonedDateTime fixedStart = ZonedDateTime.parse("2020-07-21T12:00:00.000Z");
        FinancialTransfer financialTransfer = TestUtils.getStandardTestFinancialTransfer();
        financialTransfer.setValue(value);
        financialTransfer.setSchedulingDate(fixedStart);
        financialTransfer.setTransferDate(fixedStart.plus(transferDelay));
        financialTransfer.setFee(null);
        financialTransfer.setFeeType(null);
        return financialTransfer;
    }

}