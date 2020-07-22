package br.com.henry.finrule.repository;

import br.com.henry.finrule.model.entity.BankAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

}