package com.example.desafiobackvotos.repository;

import com.example.desafiobackvotos.domain.Agenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Integer> {

    boolean findAgendaBySubject(String subject);
    Agenda getAgendaBySubject(String subject);

}
