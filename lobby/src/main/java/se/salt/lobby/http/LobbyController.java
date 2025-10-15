package se.salt.lobby.http;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.salt.lobby.domain.Room;
import se.salt.lobby.domain.service.RoomService;
import se.salt.lobby.http.dto.RoomIdResponse;
import se.salt.lobby.http.dto.RoomRequest;

@RestController
@RequestMapping(value = "/lobby")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class LobbyController {

    private final RoomService roomService;

    @PostMapping(value = "/create")
    public ResponseEntity<RoomIdResponse> createRoom(
        @RequestBody RoomRequest roomName
    ) {
        Room room = roomService.createRoom(roomName);
        RoomIdResponse response = new RoomIdResponse(room.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

// TODO
    // Get Room
    // View all available rooms ? or only access through roomId?
    // Join Room


}
