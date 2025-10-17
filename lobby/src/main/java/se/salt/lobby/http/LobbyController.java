package se.salt.lobby.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.salt.lobby.domain.Room;
import se.salt.lobby.domain.service.RoomService;
import se.salt.lobby.http.dto.RoomIdResponse;
import se.salt.lobby.http.dto.RoomRequest;

@Slf4j
@RestController
@RequestMapping(value = "/lobby")
@AllArgsConstructor
public class LobbyController {

    private final RoomService roomService;

    @PostMapping(value = "/create")
    public ResponseEntity<RoomIdResponse> createRoom(
        @RequestBody RoomRequest roomName
    ) {
        log.info("Received request to create room: {}", roomName.roomName());
        Room room = roomService.createRoom(roomName);
        log.debug("Created room object: {}", room);
        RoomIdResponse response = new RoomIdResponse(room.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

// TODO
    // Get Room
    // View all available rooms ? or only access through roomId?
    // Join Room


}
