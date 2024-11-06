package com.example.desafiobackvotos.repository;

import com.example.desafiobackvotos.domain.Associate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssociateRepository extends JpaRepository<Associate, Integer> {

    Associate getAssociateByCpf(String document);
}
