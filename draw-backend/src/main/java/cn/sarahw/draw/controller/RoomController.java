package cn.sarahw.draw.controller;

import cn.sarahw.draw.dao.CurrentRepository;
import cn.sarahw.draw.dao.RoomRepository;
import cn.sarahw.draw.dao.UserRepository;
import cn.sarahw.draw.utility.ErrorMessage;
import cn.sarahw.draw.utility.model.Current;
import cn.sarahw.draw.utility.model.Room;
import cn.sarahw.draw.utility.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/room")
@Validated
public class RoomController {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final CurrentRepository currentRepository;
    private static Random random = new Random();
    private final static Integer MAX_ROOM_SIZE = 6;

    @Autowired
    public RoomController(RoomRepository roomRepository, UserRepository userRepository, CurrentRepository currentRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.currentRepository = currentRepository;
    }

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam @NotEmpty String id) {
        Room room = roomRepository.findRoomById(id);
        if (room == null) {
            return ErrorMessage.objectNotFound;
        } else {
            Current current = currentRepository.findByRoomId(id);
            room.setCurrent(current);
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
    }


    @GetMapping(path="/all")
    public ResponseEntity<?> getAll() {
        List<Room> rooms = roomRepository.findAll();
        List<Map<String, Object>> result = formatRooms(rooms);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping(path = "/search")
    public ResponseEntity<?> searchRoom(@RequestParam @NotEmpty String keyword) {
        List<Room> rooms = roomRepository.findAllByIdContainsOrNameContains(keyword, keyword);
        List<Map<String, Object>> result = formatRooms(rooms);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    private List<Map<String, Object>> formatRooms(List<Room> rooms) {
        List<Map<String, Object>> result = new ArrayList<>();
        rooms.forEach(room -> {
            Map<String, Object> map = new HashMap<>();
            map.put("roomId", room.getId());
            map.put("name", room.getName());
            map.put("userCount", room.getUsers().size());
            result.add(map);
        });
        return result;
    }


    @PostMapping
    public ResponseEntity<?> createRoom() {
        Room room = new Room();
        room.setId(Integer.toString(random.nextInt(1000)));
        while (true) {
            try {
                roomRepository.save(room);
                break;
            } catch (Exception e) {
                room.setId(Integer.toString(random.nextInt(1000)));
            }
        }
        room.setName(room.getId());
        return new ResponseEntity<>(room.getId(), HttpStatus.OK);
    }


    @PutMapping(path="/ready")
    public ResponseEntity<?> addUser(@RequestParam @NotEmpty String userId,
                                     @RequestParam @NotEmpty String roomId) {
        Room room = roomRepository.findRoomById(roomId);
        User user = userRepository.findUserByEmail(userId);
        if (room == null || user == null) {
            return ErrorMessage.objectNotFound;
        }
        if (room.getUsers().size() >= MAX_ROOM_SIZE) {
            return ErrorMessage.roomFull;
        }
        if (room.getUsers().indexOf(user) >= 0) {
            return ErrorMessage.userAlreadyInRoom;
        }
        room.getUsers().add(user);
        roomRepository.save(room);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PutMapping(path="/unready")
    public ResponseEntity<?> deleteUser(@RequestParam @NotEmpty String userId,
                                        @RequestParam @NotEmpty String roomId) {
        Room room = roomRepository.findRoomById(roomId);
        User user = userRepository.findUserByEmail(userId);
        if (room == null || user == null) {
            return ErrorMessage.objectNotFound;
        }
        int index = room.getUsers().indexOf(user);
        if (index < 0) {
            return ErrorMessage.userNotInRoom;
        }
        room.getUsers().remove(index);
        roomRepository.save(room);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }


    @PutMapping(path = "/changeName")
    public ResponseEntity<?> changeName(@RequestParam @NotEmpty String roomId,
                                      @RequestParam @NotEmpty String newName) {
        Room room = roomRepository.findRoomById(roomId);
        if (room == null) {
            return ErrorMessage.objectNotFound;
        }
        room.setName(newName);
        roomRepository.save(room);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }


    @GetMapping(path = "/gameOn")
    public ResponseEntity<?> isGameOn(@RequestParam @NotEmpty String roomId) {
        Current current = currentRepository.findByRoomId(roomId);
        return new ResponseEntity<>(current != null, HttpStatus.OK);
    }
}
