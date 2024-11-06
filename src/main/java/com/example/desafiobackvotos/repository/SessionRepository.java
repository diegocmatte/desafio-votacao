package com.example.desafiobackvotos.repository;

import com.example.desafiobackvotos.domain.SessionVote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionVote, Integer> {

    SessionVote findBySessionVoteId(Integer sessionId);

}
