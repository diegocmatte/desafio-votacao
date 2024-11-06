package com.example.desafiobackvotos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "SESSION_VOTE")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sessionVoteId;

    @OneToOne
    @JoinColumn(name = "agenda_id")
    private Agenda agenda;

    private Boolean isSessionOpen;

    private String startedTime;

    private Integer votedYes;

    private Integer votedNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SessionVote that = (SessionVote) o;
        return getSessionVoteId() != null && Objects.equals(getSessionVoteId(), that.getSessionVoteId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
