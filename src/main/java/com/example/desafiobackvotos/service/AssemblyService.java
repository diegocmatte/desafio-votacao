package com.example.desafiobackvotos.service;

import com.example.desafiobackvotos.domain.Agenda;
import com.example.desafiobackvotos.domain.Associate;
import com.example.desafiobackvotos.domain.SessionVote;
import com.example.desafiobackvotos.exception.DocumentAlreadyExistsException;
import com.example.desafiobackvotos.exception.NoDocumentFoundException;
import com.example.desafiobackvotos.exception.NoSessionFoundException;
import com.example.desafiobackvotos.exception.NoSubjectFoundException;
import com.example.desafiobackvotos.exception.SubjectAlreadyExistsException;
import com.example.desafiobackvotos.exception.VoteContentException;
import com.example.desafiobackvotos.exception.VoteNotAllowedException;
import com.example.desafiobackvotos.repository.AgendaRepository;
import com.example.desafiobackvotos.repository.AssociateRepository;
import com.example.desafiobackvotos.repository.SessionRepository;
import com.example.desafiobackvotos.util.Constants;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Service
public class AssemblyService {

    private final AgendaRepository agendaRepository;
    private final AssociateRepository associateRepository;
    private final SessionRepository sessionRepository;

    private static Integer COUNT_FOR_YES = 0;
    private static Integer COUNT_FOR_NO = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(AssemblyService.class);

    public AssemblyService(AgendaRepository agendaRepository, AssociateRepository associateRepository, SessionRepository sessionRepository) {
        this.agendaRepository = agendaRepository;
        this.associateRepository = associateRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public void saveAssociate(Associate associate){
        if(StringUtils.isNotBlank(associate.getCpf())){
            Optional<Associate> savedAssociate = Optional.ofNullable(associateRepository.getAssociateByCpf(associate.getCpf()));
            if(savedAssociate.isPresent()){
                throw new DocumentAlreadyExistsException(Constants.DOCUMENT_ALREADY_EXISTS + associate.getCpf());
            }
            LOGGER.debug("Associate created in database");
            associateRepository.save(associate);
        } else {
            LOGGER.error("No CPF was informed");
            throw new NoDocumentFoundException(Constants.DOCUMENT_NOT_FOUND);
        }
    }

    @Transactional
    public void saveAgenda(Agenda agenda) {
        if(StringUtils.isNotBlank(agenda.getSubject())) {
            Optional<Agenda> savedAgenda = Optional.ofNullable(agendaRepository.getAgendaBySubject(agenda.getSubject()));
            if(savedAgenda.isPresent()){
                throw new SubjectAlreadyExistsException(Constants.SUBJECT_ALREADY_EXISTS + agenda.getSubject());
            }
            LOGGER.debug("Agenda created in database");
            agendaRepository.save(agenda);
        } else {
            LOGGER.error("No subject was informed");
            throw new NoSubjectFoundException(Constants.NO_SUBJECT_FOUND);
        }
    }

    public Optional<Associate> getAssociateByCpf(String document){
        if(StringUtils.isNotBlank(document)) {
            LOGGER.info("Associate located in database");
            return Optional.ofNullable(associateRepository.getAssociateByCpf(document));
        } else {
            LOGGER.error("No document was found in database");
            throw new NoDocumentFoundException(Constants.DOCUMENT_NOT_FOUND);
        }
    }

    public Optional<Agenda> getAgendaBySubject(String subject) {
        if (StringUtils.isNotBlank(subject)) {
            LOGGER.info("Agenda located in database");
            return Optional.ofNullable(agendaRepository.getAgendaBySubject(subject));
        } else {
            LOGGER.error("No subject was found in database");
            throw new NoSubjectFoundException(Constants.NO_SUBJECT_FOUND);
        }
    }

    @Transactional
    public void saveSessionVote(SessionVote sessionVote){
        sessionVote.setIsSessionOpen(false);
        sessionRepository.save(sessionVote);
    }

    @Transactional
    public void closeSession(SessionVote sessionVote){
        findBySessionVoteId(sessionVote.getSessionVoteId());
        sessionVote.setIsSessionOpen(false);
        sessionRepository.save(sessionVote);
    }

    @Transactional
    public SessionVote openSession(String agendaSubject, Integer sessionId){
        SessionVote sessionVote = findBySessionVoteId(sessionId);
        Optional<Agenda> agenda = getAgendaBySubject(agendaSubject);
        if (agenda.isPresent()) {
            sessionVote.setAgenda(agenda.get());
            sessionVote.setIsSessionOpen(true);
            String timeStamp = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
            sessionVote.setStartedTime(timeStamp);
            startCountingSessionTime(sessionVote);
            sessionRepository.save(sessionVote);
            LOGGER.info("The session ID {} is opened and started at {}. The subject for voting is: {}", sessionVote.getSessionVoteId(), sessionVote.getStartedTime(), sessionVote.getAgenda().getSubject());
            return sessionVote;
        } else {
            LOGGER.error("No subject was found");
            throw new NoSubjectFoundException(Constants.NO_SUBJECT_FOUND);
        }
    }

    public SessionVote findBySessionVoteId(Integer sessionId){
        if(sessionRepository.existsById(sessionId)){
            return sessionRepository.findBySessionVoteId(sessionId);
        } else {
            LOGGER.error("No ID {} session was found", sessionId);
            throw new NoSessionFoundException(Constants.SESSION_NOT_FOUND);
        }
    }

    private void startCountingSessionTime(SessionVote sessionVote){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                sessionVote.setIsSessionOpen(false);
                sessionVote.setVotedYes(COUNT_FOR_YES);
                sessionVote.setVotedNo(COUNT_FOR_NO);
                sessionRepository.save(sessionVote);
                LOGGER.debug("The time of the session [{}] run up. Ended at [{}]", sessionVote.getSessionVoteId(), new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
                LOGGER.debug("Results: [{}] votes for YES; [{}] votes for NO", sessionVote.getVotedYes(), sessionVote.getVotedNo());
                COUNT_FOR_YES = 0;
                COUNT_FOR_NO = 0;
            }
        };
        timer.schedule(timerTask, TimeUnit.MINUTES.toMillis(1));
    }

    @Transactional
    public void registerVote(Integer sessionId, String agendaSubject, String voteContent, String document) {
        Optional<Agenda> agenda = getAgendaBySubject(agendaSubject);
        Optional<Associate> associate = getAssociateByCpf(document);
        if(agenda.isPresent() && associate.isPresent()) {
            SessionVote sessionVote = findBySessionVoteId(sessionId);
            if (isSessionOpen(sessionVote.getSessionVoteId()) && !isAssociateAllowedToVote(agenda.get(), associate.get())) {
                LOGGER.info("The document [{}] is " + Constants.ABLE_TO_VOTE, document);
                associate.get().setAgenda(agenda.get());
                agenda.get().getAssociate().add(associate.get());
                agendaRepository.save(agenda.get());
                associateRepository.save(associate.get());
                if (sessionVote.getAgenda().getSubject().equalsIgnoreCase(agenda.get().getSubject())) {
                    if (voteContent.equalsIgnoreCase(Constants.VOTED_YES)) {
                        COUNT_FOR_YES++;
                        LOGGER.info("Vote for YES has been registered");
                    } else if (voteContent.equalsIgnoreCase(Constants.VOTED_NO)) {
                        associate.get().setAgenda(agenda.get());
                        COUNT_FOR_NO++;
                        LOGGER.info("Vote for NO has been registered");
                    } else {
                        throw new VoteContentException("The vote content must be YES or NO");
                    }
                } else {
                    throw new NoSubjectFoundException("The subject does not match");
                }
            } else {
                throw new VoteNotAllowedException("The document [{}] is " + Constants.UNABLE_TO_VOTE);
            }
        } else {
            throw new NullPointerException("Agenda/Associate does not exist");
        }
    }

    private Boolean isAssociateAllowedToVote(Agenda agenda, Associate associate){
        return agenda.getAssociate().stream()
                .anyMatch(a -> Objects.equals(a.getAssociateId(), associate.getAssociateId()));
    }

    public Boolean isSessionOpen(Integer sessionId){
        return sessionRepository.findBySessionVoteId(sessionId).getIsSessionOpen();
    }

}
