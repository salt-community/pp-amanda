package se.salt.lobby.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.salt.lobby.domain.Room;
import se.salt.lobby.http.dto.RoomRequest;

import java.util.Random;

@Slf4j
@Service
public class RoomService {

    public Room createRoom(RoomRequest roomName) {
        String roomId = generateRandomId();
        log.info("Creating new room '{}' with ID: {}", roomName.roomName(), roomId);
        return new Room(roomId, roomName.roomName());
    }

    private String generateRandomId() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder id = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(characters.length());
            id.append(characters.charAt(index));
        }

        return id.toString();
    }
}
