package br.com.henry.finrule.repository;

import br.com.henry.finrule.model.entity.FinancialTransfer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialTransferRepository extends CrudRepository<FinancialTransfer, Long> {

    String CACHE_NAME = "financialTransferCache";

    @CacheEvict(value = CACHE_NAME, key = "#root.args[0].id")
    @Override
    <S extends FinancialTransfer> S save(S entity);

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Override
    <S extends FinancialTransfer> Iterable<S> saveAll(Iterable<S> entities);

    @Cacheable(value = CACHE_NAME)
    @Override
    Optional<FinancialTransfer> findById(Long aLong);

    @Cacheable(value = CACHE_NAME)
    @Override
    boolean existsById(Long aLong);

    @CacheEvict(value = CACHE_NAME)
    @Override
    void deleteById(Long aLong);

    @CacheEvict(value = CACHE_NAME, key = "#root.args[0].id")
    @Override
    void delete(FinancialTransfer entity);

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Override
    void deleteAll(Iterable<? extends FinancialTransfer> entities);

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    @Override
    void deleteAll();

}