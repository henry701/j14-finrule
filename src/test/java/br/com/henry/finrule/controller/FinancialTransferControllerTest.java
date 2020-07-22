package br.com.henry.finrule.controller;

import br.com.henry.finrule.model.dto.BankAccountInput;
import br.com.henry.finrule.model.dto.FinancialTransferCreation;
import br.com.henry.finrule.model.entity.FinancialTransfer;
import br.com.henry.finrule.repository.BankAccountRepository;
import br.com.henry.finrule.repository.FinancialTransferRepository;
import br.com.henry.finrule.test.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.exparity.hamcrest.date.ZonedDateTimeMatchers;
import org.exparity.hamcrest.date.core.TemporalMatcher;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FinancialTransferController.class)
@EnableAutoConfiguration
@ComponentScan(basePackages={"br.com.henry.finrule"})
@EntityScan(basePackages = "br.com.henry.finrule.model.entity")
public class FinancialTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinancialTransferRepository financialTransferRepository;

    @MockBean
    private BankAccountRepository bankAccountRepository;

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public static void beforeClass() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testFinancialTransferCreation() throws Exception {

        when(financialTransferRepository.save(any())).then(c -> {
            FinancialTransfer financialTransfer = c.getArgument(0);
            financialTransfer.setId(1L);
            Assert.assertNotNull(financialTransfer.getDestinationAccount());
            Assert.assertNotNull(financialTransfer.getDestinationAccount().getAccountNumber());
            Assert.assertNotNull(financialTransfer.getFee());
            Assert.assertNotNull(financialTransfer.getFeeType());
            Assert.assertNotNull(financialTransfer.getSchedulingDate());
            Assert.assertNotNull(financialTransfer.getTransferDate());
            Assert.assertNotNull(financialTransfer.getSourceAccount());
            Assert.assertNotNull(financialTransfer.getValue());
            Assert.assertNotNull(financialTransfer.getSourceAccount().getAccountNumber());
            return financialTransfer;
        });

        FinancialTransferCreation financialTransferCreation = new FinancialTransferCreation();
        financialTransferCreation.setTransferDate(ZonedDateTime.now().plus(Duration.ofDays(3)));
        financialTransferCreation.setValue(BigDecimal.ONE);
        financialTransferCreation.setDestinationAccount(new BankAccountInput());
        financialTransferCreation.getDestinationAccount().setAccountNumber("000001");
        financialTransferCreation.setSourceAccount(new BankAccountInput());
        financialTransferCreation.getSourceAccount().setAccountNumber("000002");

        this.mockMvc.perform(post("/financialTransfer")
            .content(mapper.writeValueAsString(financialTransferCreation))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fee").exists())
            .andExpect(jsonPath("$.feeType").exists())
            .andExpect(jsonPath("$.value").value(financialTransferCreation.getValue()))
            .andExpect(jsonPath("$.sourceAccount.accountNumber").value(financialTransferCreation.getSourceAccount().getAccountNumber()))
            .andExpect(jsonPath("$.destinationAccount.accountNumber").value(financialTransferCreation.getDestinationAccount().getAccountNumber()))
            .andExpect(jsonPath("$.transferDate").value(wrapParseMatcher(ZonedDateTimeMatchers.sameInstant(financialTransferCreation.getTransferDate()))))
            .andExpect(jsonPath("$.schedulingDate").value(wrapParseMatcher(ZonedDateTimeMatchers.sameDay(ZonedDateTime.now()))))
            .andExpect(jsonPath("$.id").value(1L));

        ArgumentCaptor<FinancialTransfer> financialTransferArgumentCaptor = ArgumentCaptor.forClass(FinancialTransfer.class);

        verify(financialTransferRepository, times(1)).save(financialTransferArgumentCaptor.capture());

        FinancialTransfer savedFinancialTransfer = financialTransferArgumentCaptor.getValue();

        Assert.assertEquals(savedFinancialTransfer.getValue(), financialTransferCreation.getValue());
        Assert.assertEquals(savedFinancialTransfer.getSourceAccount().getAccountNumber(), financialTransferCreation.getSourceAccount().getAccountNumber());
        Assert.assertEquals(savedFinancialTransfer.getDestinationAccount().getAccountNumber(), financialTransferCreation.getDestinationAccount().getAccountNumber());
        MatcherAssert.assertThat(savedFinancialTransfer.getTransferDate(), ZonedDateTimeMatchers.sameInstant(financialTransferCreation.getTransferDate()));
        
    }

    private Matcher<String> wrapParseMatcher(TemporalMatcher<ZonedDateTime> zonedDateTimeTemporalMatcher) {
        return new BaseMatcher<>() {
            @Override
            public boolean matches(Object actual) {
                return zonedDateTimeTemporalMatcher.matches(ZonedDateTime.parse(actual.toString()));
            }
            @Override
            public void describeTo(Description description) {
                zonedDateTimeTemporalMatcher.describeTo(description);
            }
        };
    }

    @Test
    public void testWrongFinancialTransferCreation() throws Exception {

        FinancialTransferCreation financialTransferCreation = new FinancialTransferCreation();
        financialTransferCreation.setValue(BigDecimal.valueOf(-55.1));
        financialTransferCreation.setTransferDate(ZonedDateTime.now().minus(Duration.ofHours(10)));
        financialTransferCreation.setDestinationAccount(new BankAccountInput());

        this.mockMvc.perform(post("/financialTransfer")
            .content(mapper.writeValueAsString(financialTransferCreation))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.value").exists())
            .andExpect(jsonPath("$.transferDate").exists())
            .andExpect(jsonPath("$.['destinationAccount.accountNumber']").exists())
            .andExpect(jsonPath("$.sourceAccount").exists())
            .andExpect(jsonPath("$.id").doesNotExist());

        verify(financialTransferRepository, never()).save(any());
    }

    @Test
    public void testFinancialTransferQuery() throws Exception {

        FinancialTransfer financialTransfer = TestUtils.getStandardTestFinancialTransfer();

        when(financialTransferRepository.findById(financialTransfer.getId())).thenReturn(Optional.of(financialTransfer));

        this.mockMvc.perform(get("/financialTransfer/{id}", financialTransfer.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.value").value(financialTransfer.getValue()))
            .andExpect(jsonPath("$.transferDate").value(wrapParseMatcher(ZonedDateTimeMatchers.sameInstant(financialTransfer.getTransferDate()))))
            .andExpect(jsonPath("$.destinationAccount.accountNumber").value(financialTransfer.getDestinationAccount().getAccountNumber()))
            .andExpect(jsonPath("$.sourceAccount.accountNumber").value(financialTransfer.getSourceAccount().getAccountNumber()))
            .andExpect(jsonPath("$.id").value(financialTransfer.getId()));

        verify(financialTransferRepository, times(1)).findById(financialTransfer.getId());
        verify(financialTransferRepository, never()).findAll();
    }

    @Test
    public void testNotFoundFinancialTransferQuery() throws Exception {

        Long id = 99L;

        when(financialTransferRepository.findById(id)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/financialTransfer/{id}", id)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.id").doesNotExist())
            .andExpect(jsonPath("$.value").doesNotExist());

        verify(financialTransferRepository, times(1)).findById(id);
        verify(financialTransferRepository, never()).findAll();
    }

    @Test
    public void testFinancialTransferList() throws Exception {

        FinancialTransfer financialTransfer1 = TestUtils.getStandardTestFinancialTransfer();
        FinancialTransfer financialTransfer2 = TestUtils.getStandardTestFinancialTransfer();

        when(financialTransferRepository.findAll()).thenReturn(Arrays.asList(financialTransfer1, financialTransfer2));

        this.mockMvc.perform(get("/financialTransfer")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].value").value(financialTransfer1.getValue()))
            .andExpect(jsonPath("$[0].fee").value(financialTransfer1.getFee()))
            .andExpect(jsonPath("$[0].id").value(financialTransfer1.getId()))
            .andExpect(jsonPath("$[1].fee").value(financialTransfer2.getFee()))
            .andExpect(jsonPath("$[1].value").value(financialTransfer2.getValue()))
            .andExpect(jsonPath("$[1].id").value(financialTransfer2.getId()));

        verify(financialTransferRepository, times(1)).findAll();
        verify(financialTransferRepository, never()).findById(any());
    }

    @Test
    public void testFinancialTransferCancel() throws Exception {

        FinancialTransfer financialTransfer = TestUtils.getStandardTestFinancialTransfer();

        when(financialTransferRepository.findById(financialTransfer.getId())).thenReturn(Optional.of(financialTransfer));

        this.mockMvc.perform(delete("/financialTransfer/{id}", financialTransfer.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.value").value(financialTransfer.getValue()))
            .andExpect(jsonPath("$.fee").value(financialTransfer.getFee()))
            .andExpect(jsonPath("$.id").value(financialTransfer.getId()));

        verify(financialTransferRepository, times(1)).findById(financialTransfer.getId());
        verify(financialTransferRepository, times(1)).deleteById(financialTransfer.getId());
        verify(financialTransferRepository, never()).findAll();

    }

    @Test
    public void testFinancialTransferCancelError() throws Exception {

        FinancialTransfer financialTransfer = TestUtils.getStandardTestFinancialTransfer();
        financialTransfer.setTransferDate(ZonedDateTime.now().minus(Duration.ofDays(1)));

        when(financialTransferRepository.findById(financialTransfer.getId())).thenReturn(Optional.of(financialTransfer));

        this.mockMvc.perform(delete("/financialTransfer/{id}", financialTransfer.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is(422));

        verify(financialTransferRepository, times(1)).findById(financialTransfer.getId());
        verify(financialTransferRepository, never()).deleteById(financialTransfer.getId());
        verify(financialTransferRepository, never()).findAll();

    }

}