package se.salt.lobby.http;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.salt.lobby.domain.Session;
import se.salt.lobby.domain.service.SessionService;
import se.salt.lobby.http.dto.JoinSessionRequest;
import se.salt.lobby.http.dto.SessionIdResponse;
import se.salt.lobby.http.dto.SessionRequest;
import se.salt.lobby.http.dto.SessionResponse;

@Slf4j
@RestController
@RequestMapping(value = "/lobby")
@AllArgsConstructor
public class LobbyController {

    private final SessionService sessionService;

    @PostMapping(value = "/create")
    public ResponseEntity<SessionIdResponse> createSession(
        @RequestBody SessionRequest req
    ) {
        log.info("Received request to create session: {}", req.sessionName());
        Session session = sessionService.createSession(req);
        log.debug("Created session object: {}", session);
        SessionIdResponse response = new SessionIdResponse(session.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/join/{sessionId}")
    public ResponseEntity<SessionResponse> joinSession(
        @PathVariable @Valid JoinSessionRequest sessionId
    ) {
        log.info("Received request to join session with ID: {}", sessionId);
        Session session = sessionService.joinSession(sessionId);
        SessionResponse response = SessionResponse.fromSession(session);
        return ResponseEntity.ok(response);
    }

}
