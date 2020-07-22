package br.com.henry.finrule.repository;

import br.com.henry.finrule.model.entity.FinancialTransfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialTransferRepository extends CrudRepository<FinancialTransfer, Long> {



}