package com.example.desafiobackvotos;

import com.example.desafiobackvotos.domain.Agenda;
import com.example.desafiobackvotos.domain.Associate;
import com.example.desafiobackvotos.domain.SessionVote;
import com.example.desafiobackvotos.exception.DocumentAlreadyExistsException;
import com.example.desafiobackvotos.exception.NoDocumentFoundException;
import com.example.desafiobackvotos.exception.NoSessionFoundException;
import com.example.desafiobackvotos.exception.NoSubjectFoundException;
import com.example.desafiobackvotos.exception.SubjectAlreadyExistsException;
import com.example.desafiobackvotos.repository.AgendaRepository;
import com.example.desafiobackvotos.repository.AssociateRepository;
import com.example.desafiobackvotos.repository.SessionRepository;
import com.example.desafiobackvotos.service.AssemblyService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AssemblyServiceTests {

    @InjectMocks
    private AssemblyService assemblyService;
    @Mock
    private AssociateRepository associateRepository;
    @Mock
    private AgendaRepository agendaRepository;
    @Mock
    private SessionRepository sessionRepository;

    private Agenda agenda;
    private Associate associate;
    private SessionVote sessionVote;

    @BeforeEach
    void init(){
        associate = Associate.builder()
                .associateId(1)
                .cpf("12345678900")
                .build();

        agenda = Agenda.builder()
                .agendaId(1)
                .subject("test subject")
                .build();

        sessionVote = SessionVote.builder()
                .sessionVoteId(1)
                .build();

    }

    @DisplayName("Test for saveAssociate")
    @Test
    public void givenAssociateObject_whenSaveAssociate_thenReturnAssociateObject(){
        given(associateRepository.getAssociateByCpf(associate.getCpf()))
                .willReturn(associate);

        given(associateRepository.save(associate)).willReturn(associate);

        assertThat(associate).isNotNull();
    }

    @DisplayName("Test for saveAssociate which throws exception DocumentAlreadyExistsException")
    @Test
    public void givenAssociateObject_whenSaveAssociate_thenReturnDocumentAlreadyExistsException(){
        given(associateRepository.getAssociateByCpf(associate.getCpf()))
                .willReturn(associate);

        assertThrows(DocumentAlreadyExistsException.class, () -> assemblyService.saveAssociate(associate));

        verify(associateRepository, never()).save(any(Associate.class));
    }

    @DisplayName("Test for getAssociateByCpf")
    @Test
    public void givenAssociateObject_whenGetByCpf_thenReturnAssociateObject(){
        given(associateRepository.getAssociateByCpf(associate.getCpf()))
                .willReturn(associate);

        given(associateRepository.save(associate)).willReturn(associate);

        Optional<Associate> getAssociateByCpf = assemblyService.getAssociateByCpf(associate.getCpf());

        assertThat(getAssociateByCpf.isPresent()).isNotNull();
    }

    @DisplayName("Test for getAssociateByCpf when CPF is blank or not exists then throw NoDocumentFoundException")
    @Test
    public void givenAssociateObject_whenGetByCpf_thenReturnNoDocumentFoundException(){
        given(associateRepository.getAssociateByCpf(associate.getCpf()))
                .willReturn(associate);

        assertThrows(NoDocumentFoundException.class, () -> assemblyService.getAssociateByCpf(""));

        verify(associateRepository, never()).save(any(Associate.class));
    }

    @DisplayName("Test for saveAgenda")
    @Test
    public void givenAgendaObject_whenSaveAgenda_thenReturnAgendaObject(){
        given(agendaRepository.getAgendaBySubject(agenda.getSubject()))
                .willReturn(agenda);

        given(agendaRepository.save(agenda)).willReturn(agenda);

        assertThat(agenda).isNotNull();
    }

    @DisplayName("Test for saveAgenda which throws exception SubjectAlreadyExistsException")
    @Test
    public void givenAgendaObject_whenSaveAgenda_thenReturnSubjectAlreadyExistsException(){
        given(agendaRepository.getAgendaBySubject(agenda.getSubject()))
                .willReturn(agenda);

        given(agendaRepository.save(agenda)).willReturn(agenda);

        assertThrows(SubjectAlreadyExistsException.class, () -> assemblyService.saveAgenda(agenda));

        verify(agendaRepository, never()).save(any(Agenda.class));
    }

    @DisplayName("Test for getAgendaBySubject")
    @Test
    public void givenAgendaObject_whenGetBySubject_thenReturnAgendaObject(){
        given(agendaRepository.getAgendaBySubject(agenda.getSubject()))
                .willReturn(agenda);

        given(agendaRepository.save(agenda)).willReturn(agenda);

        Optional<Agenda> getAgendaBySubject = assemblyService.getAgendaBySubject(agenda.getSubject());

        assertThat(getAgendaBySubject.isPresent()).isNotNull();
    }

    @DisplayName("Test for getAgendaBySubject when subject is blank or does not exist and should throw NoSubjectFoundException")
    @Test
    public void givenAgendaObject_whenGetBySubject_thenReturnNoSubjectFoundException(){
        given(agendaRepository.getAgendaBySubject((agenda.getSubject())))
                .willReturn(agenda);

        given(agendaRepository.save(agenda)).willReturn(agenda);

        assertThrows(NoSubjectFoundException.class, () -> assemblyService.getAgendaBySubject(""));

        verify(agendaRepository, never()).save(any(Agenda.class));
    }

    @DisplayName("Test for saveSession")
    @Test
    public void givenSessionObject_whenSaveSession_thenReturnSessionObject(){
        given(sessionRepository.findBySessionVoteId(sessionVote.getSessionVoteId()))
                .willReturn(sessionVote);

        given(sessionRepository.save(sessionVote)).willReturn(sessionVote);

        assertThat(sessionVote).isNotNull();

    }

    @DisplayName("Test for findBySessionVoteId")
    @Test
    public void givenSessionObject_whenFindBySessionVoteId_thenReturnSessionObject(){
        given(sessionRepository.findBySessionVoteId(sessionVote.getSessionVoteId()))
                .willReturn(sessionVote);

        given(sessionRepository.save(sessionVote)).willReturn(sessionVote);

        SessionVote sessionVoteTest = sessionRepository.findBySessionVoteId(sessionVote.getSessionVoteId());

        assertThat(sessionVoteTest).isNotNull();
    }

    @DisplayName("Test for findBySessionVoteId when sessionId is blank or does not exist then should return NoSessionFoundException")
    @Test
    public void givenSessionObject_whenFindBySessionVoteId_thenNoSessionFoundException(){
        given(sessionRepository.findBySessionVoteId(sessionVote.getSessionVoteId()))
                .willReturn(sessionVote);

        given(sessionRepository.save(sessionVote)).willReturn(sessionVote);

        assertThrows(NoSessionFoundException.class, () -> assemblyService.findBySessionVoteId(2));

        verify(sessionRepository, never()).save(any(SessionVote.class));
    }
}
