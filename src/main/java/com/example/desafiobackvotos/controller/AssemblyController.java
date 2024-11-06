package com.example.desafiobackvotos.controller;

import com.example.desafiobackvotos.domain.Agenda;
import com.example.desafiobackvotos.domain.Associate;
import com.example.desafiobackvotos.domain.SessionVote;
import com.example.desafiobackvotos.service.AssemblyService;
import com.example.desafiobackvotos.util.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.util.Optional;


@RestController
@Tag(name = "AssemblyController", description = "Assembly Controller")
@RequestMapping("/assembly")
public class AssemblyController {

    private final AssemblyService assemblyService;

    public AssemblyController(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AssemblyController.class);

    @PostMapping(value = "/createAssociate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new associate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful POST request - Associate was created", content = {@Content(schema = @Schema(implementation = Associate.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request. Check the response body")
                    })
    public ResponseEntity<?> createAssociate(
            @Valid @RequestBody Associate associate){
        String methodName = "createAssociate";
        if(associate != null) {
            assemblyService.saveAssociate(associate);
            LOGGER.debug("Associate [ID:{},CPF:{}] created", associate.getAssociateId(), associate.getCpf());
            return new ResponseEntity<>("Associate was created", HttpStatus.OK);
        } else {
            LOGGER.error("Error during Associate creation, method [{}}", methodName);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/createAgenda", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new agenda")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful POST request - Agenda was created", content = {@Content(schema = @Schema(implementation = Agenda.class))}),
            @ApiResponse(responseCode = "400", description = "Problem during the process")
    })
    public ResponseEntity<?> createAgenda(
            @Valid @RequestBody Agenda agenda) {
        String methodName = "createAgenda";
        if(agenda != null) {
            assemblyService.saveAgenda(agenda);
            LOGGER.debug("Agenda [ID:{},Subject:{}] created", agenda.getAgendaId(), agenda.getSubject());
            return new ResponseEntity<>("Agenda was created", HttpStatus.OK);
        } else {
            LOGGER.error("Error during Agenda creation, method [{}]", methodName);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/validateDocument")
    @Operation(summary = "Validate the associate document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful GET request", content = {@Content(schema = @Schema(implementation = HttpStatus.class))}),
            @ApiResponse(responseCode = "404", description = "No document was found")
    })
    public ResponseEntity<?> validateDocument(
            @Parameter(description = "Associate document", required = true) @RequestParam String document){
        String methodName = "validateDocument";
        Optional<Associate> associate = assemblyService.getAssociateByCpf(document);
        if(associate.isPresent()){
            LOGGER.debug("Document has been validated");
            return new ResponseEntity<>(Constants.ABLE_TO_VOTE, HttpStatus.OK);
        } else {
            LOGGER.error("The associate [{}] is not able to vote, method [{}]", document, methodName);
            return new ResponseEntity<>(Constants.UNABLE_TO_VOTE, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/createSession")
    @Operation(summary = "Create new session for vote")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful POST request", content = {@Content(schema = @Schema(implementation = SessionVote.class))})
    })
    public ResponseEntity<?> createSession(){
        SessionVote sessionVote = new SessionVote();
        assemblyService.saveSessionVote(sessionVote);
        LOGGER.debug("Session {} created", sessionVote.getSessionVoteId());
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{sessionId}/")
                .buildAndExpand(sessionVote.getSessionVoteId())
                .toUri();
        LOGGER.info("Session [{}] was created", sessionVote.getSessionVoteId());
        return new ResponseEntity<>(uri, HttpStatus.OK);
    }

    @PatchMapping(value = "/openSession/{sessionId}/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Open session for voting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful POST request", content = {@Content(schema = @Schema(implementation = SessionVote.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request. Check the response body"),
            @ApiResponse(responseCode = "404", description = "Session was not found")
    })
    public ResponseEntity<?> openSession(
            @Parameter(description = "Agenda subject to vote", required = true) @RequestParam String agendaSubject,
            @Parameter(description = "Session ID", required = true) @PathVariable Integer sessionId) {
        String methodName = "openSession";
        if (sessionId > 0 && StringUtils.isNotBlank(agendaSubject)) {
            SessionVote sessionVote = assemblyService.openSession(agendaSubject, sessionId);
            if (sessionVote != null){
                LOGGER.debug("The session [{}] was opened at [{}]", sessionVote.getSessionVoteId(), sessionVote.getStartedTime());
                return new ResponseEntity<>(sessionVote, HttpStatus.OK);
            } else {
                LOGGER.error("The session was not found");
                return new ResponseEntity<>(Constants.SESSION_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } else {
            LOGGER.error("Error during opening session, method [{}]", methodName);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(value = "/closeSession/{sessionId}/")
    @Operation(summary = "Close the vote's session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful PATCH request", content = {@Content(schema = @Schema(implementation = SessionVote.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request. Check the response body"),
            @ApiResponse(responseCode = "404", description = "Session was not found")
    })
    public ResponseEntity<?> closeSession(
            @Parameter(description = "Session ID", required = true) @PathVariable Integer sessionId){
        String methodName = "closeSession";
        if (sessionId > 0){
            SessionVote sessionVote = assemblyService.findBySessionVoteId(sessionId);
            if(sessionVote != null) {
                LOGGER.debug("The session [{}] has been closed", sessionVote.getSessionVoteId());
                assemblyService.closeSession(sessionVote);
                return new ResponseEntity<>("The session " + sessionVote.getSessionVoteId() + " has been closed", HttpStatus.OK);
            } else {
                LOGGER.error("The session was not found");
                return new ResponseEntity<>(Constants.SESSION_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } else {
            LOGGER.error("Error during opening session, method [{}]", methodName);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/registerVote/{sessionId}/")
    @Operation(summary = "Register associate's vote")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful POST request", content = {@Content(schema = @Schema(implementation = SessionVote.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request. Check the response body"),
            @ApiResponse(responseCode = "403", description = "The associate is not allowed to vote"),
    })
    public ResponseEntity<?> registerVote(
            @Parameter(description = "Session ID", required = true) @PathVariable Integer sessionId,
            @Parameter(description = "Agenda subject to vote", required = true) @RequestParam String agendaSubject,
            @Parameter(description = "Associate's vote", required = true) @RequestParam String voteContent,
            @Parameter(description = "Associate's document") @RequestParam String document){
        String methodName = "registerVote";
        if(assemblyService.isSessionOpen(sessionId)) {
            SessionVote sessionVote = assemblyService.findBySessionVoteId(sessionId);
            if(sessionVote != null) {
                assemblyService.registerVote(sessionId, agendaSubject, voteContent, document);
                LOGGER.debug("The vote from [{}] has been registered", document);
                return new ResponseEntity<>("The vote has been registered", HttpStatus.OK);
            } else {
                LOGGER.error("The vote was not able to be registered by this associate [{}]", document);
                return new ResponseEntity<>("The associate is not allowed to vote", HttpStatus.FORBIDDEN);
            }
        }
        LOGGER.error("Error during registering the vote, method [{}]", methodName);
        return new ResponseEntity<>("Problem during registering the vote", HttpStatus.BAD_REQUEST);
    }
}
