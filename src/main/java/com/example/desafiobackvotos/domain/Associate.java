package com.example.desafiobackvotos.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.Hibernate;

import java.util.Objects;

@Table(name = "ASSOCIATE")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Associate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer associateId;

    @Column(unique = true)
    private String cpf;

    @ManyToOne
    @JoinColumn(name = "agenda_id")
    private Agenda agenda;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Associate that = (Associate) o;
        return getAssociateId() != null && Objects.equals(getAssociateId(), that.getAssociateId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
