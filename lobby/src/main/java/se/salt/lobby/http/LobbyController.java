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
import se.salt.lobby.http.dto.SessionResponse;

@Slf4j
@RestController
@RequestMapping("/lobby")
@AllArgsConstructor
public class LobbyController {

    private final SessionService sessionService;

    @GetMapping("/session")
    public ResponseEntity<SessionResponse> createSession(
    ) {
        log.info("Received call to create session");
        Session session = sessionService.generateSession();
        log.debug("Created session object: {}", session);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(SessionResponse.fromSession(session));
    }

    @PostMapping("/join")
    public ResponseEntity<SessionResponse> joinSession(
        @Valid @RequestBody JoinSessionRequest request
    ) {
        log.info("Received request to join session with ID: {}", request.sessionId());
        Session session = sessionService.joinSession(request);
        return ResponseEntity.ok(SessionResponse.fromSession(session));
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(
        @PathVariable String sessionId
    ) {
        log.info("Received request to get session with ID: {}", sessionId);
        Session session = sessionService.findById(sessionId);
        return ResponseEntity.ok(SessionResponse.fromSession(session));
    }
}
