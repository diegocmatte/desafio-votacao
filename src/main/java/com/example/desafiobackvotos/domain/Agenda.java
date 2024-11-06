package com.example.desafiobackvotos.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.Hibernate;

import java.util.List;
import java.util.Objects;

@Table(name = "AGENDA")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer agendaId;

    @OneToMany(mappedBy = "agenda")
    private List<Associate> associate;

    private String subject;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Agenda that = (Agenda) o;
        return getAgendaId() != null && Objects.equals(getAgendaId(), that.getAgendaId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
