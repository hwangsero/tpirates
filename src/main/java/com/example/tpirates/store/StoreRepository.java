package com.example.tpirates.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findAllByOrderByLevelAsc();
}
