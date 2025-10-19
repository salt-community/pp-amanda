package se.salt.lobby.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.salt.lobby.domain.Session;
import se.salt.lobby.domain.service.SessionService;
import se.salt.lobby.http.dto.SessionRequest;
import se.salt.lobby.http.dto.SessionResponse;

@Slf4j
@RestController
@RequestMapping(value = "/lobby")
@AllArgsConstructor
public class LobbyController {

    private final SessionService sessionService;

    @PostMapping(value = "/create")
    public ResponseEntity<SessionResponse> createRoom(
        @RequestBody SessionRequest req
    ) {
        log.info("Received request to create session: {}", req.sessionName());
        Session session = sessionService.createSession(req);
        log.debug("Created session object: {}", session);
        SessionResponse response = new SessionResponse(session.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

// TODO
    // Get Session
    // View all available rooms ? or only access through sessionId?
    // Join Session


}
