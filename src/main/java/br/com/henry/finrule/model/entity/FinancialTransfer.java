package br.com.henry.finrule.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "financialTransfers")
@DynamicUpdate
@Data
@NoArgsConstructor
public class FinancialTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Nullable
    private Long id;



}
