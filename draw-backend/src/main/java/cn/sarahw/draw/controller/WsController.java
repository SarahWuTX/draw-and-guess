package cn.sarahw.draw.controller;

import cn.sarahw.draw.dao.CurrentRepository;
import cn.sarahw.draw.dao.PuzzleRepository;
import cn.sarahw.draw.dao.RoomRepository;
import cn.sarahw.draw.utility.model.*;
import cn.sarahw.draw.utility.request.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@Slf4j
@RestController
public class WsController {

    private final SimpMessagingTemplate template;
    private final RoomRepository roomRepository;
    private final PuzzleRepository puzzleRepository;
    private final CurrentRepository currentRepository;

    @Autowired
    public WsController(SimpMessagingTemplate template, RoomRepository roomRepository,
                        PuzzleRepository puzzleRepository, CurrentRepository currentRepository) {
        this.template = template;
        this.roomRepository = roomRepository;
        this.puzzleRepository = puzzleRepository;
        this.currentRepository = currentRepository;
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public Message chat(@DestinationVariable String roomId, Message message) throws Exception {
        log.info(message.getSender().toString() + " : " + message.getMessage());
        Current current = currentRepository.findByRoomId(roomId);
        if (current == null) {
            log.info(message.getMessage());
            return message;
        }
        LinkedHashMap<String, String> user = (LinkedHashMap<String, String>)message.getSender();
        if (message.getMessage().equals(current.getPuzzle().getAnswer())) {
            if (!current.getCount().contains(user.get("email"))) {
                log.info(current.toString());
                current.getCount().add(user.get("email"));
                int mark = current.getCount().size() > 3 ? 1 : 6 - current.getCount().size();
                current.setMark(addMark(current.getMark(), user.get("email"), mark));
                log.info(current.toString());
                currentRepository.save(current);
                template.convertAndSend("/topic/current/" + current.getRoomId(), current);
                message.setMessage("答对了！🎉");
                log.info(message.getMessage());
                return message;
            }
        }
        if (current.getCount().contains(user.get("email"))) {
            message.setMessage(message.getMessage().replaceAll(current.getPuzzle().getAnswer(), "***"));
        }
        log.info(message.getMessage());
        return message;
    }

    private List<Map<String, Object>> addMark(List<Map<String, Object>> marks, String email, int newMark){
        marks.forEach(user -> {
            if (user.get("email").equals(email)) {
                user.compute("mark", (k, v) -> v == null ? newMark : (Integer)v + newMark);
            }
            user.remove("rank");
        } );

        marks.sort((a, b) -> (Integer) b.get("mark") - (Integer) a.get("mark"));
        marks.get(0).put("rank", 1);
        for (int i = 1; i < marks.size(); i++) {
            int rank = (Integer) marks.get(i-1).get("rank");
            rank = marks.get(i).get("mark").equals(marks.get(i-1).get("mark")) ? rank : rank + 1;
            marks.get(i).put("rank", rank);
        }
        return marks;
    }

    @PutMapping(path = "/current/test")
    public void test() {
        log.error("it is a test");
    }


    @MessageMapping("/canvas/{roomId}")
    @SendTo("/topic/canvas/{roomId}")
    public String canvas(@DestinationVariable String roomId, String canvas) throws Exception {
        return canvas;
    }


    @MessageMapping("/room/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Room room(@DestinationVariable String roomId, Room room) throws Exception {
        return room;
    }


    @MessageMapping("/current/{roomId}")
    @SendTo("/topic/current/{roomId}")
    public Current current(@DestinationVariable String roomId, Current current) throws Exception {
        currentRepository.save(current);
        return current;
    }


    @MessageMapping("/order/{roomId}/start")
    public void order(@DestinationVariable String roomId, Map<String, Object> obj) {
        // game start
        Map<String, Object> result = new HashMap<>();
        List<Puzzle> puzzles = puzzleRepository.findAll();
        Room room = roomRepository.findRoomById(roomId);
        Current current = new Current(roomId);
        List<Map<String, Object>> emptyMark = new ArrayList<>();
        current.setMark(emptyMark);
        room.getUsers().forEach(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("email", user.getEmail());
            map.put("name", user.getName());
            map.put("mark", 0);
            map.put("rank", 1);
            emptyMark.add(map);
        });

        synchronized (currentRepository) {
            if (currentRepository.findByRoomId(roomId) != null) {
                template.convertAndSend("/topic/chat/" + roomId,
                        new Message("system", "房间游戏未结束而再次接收到开始请求"));
                log.error("房间游戏未结束而再次接收到开始请求");
                return;
            }
            result.put("order", "start");
            log.warn(result.toString());
            template.convertAndSend("/topic/order/" + roomId , result);
            template.convertAndSend("/topic/chat/" + roomId, new Message("system", "- 游戏开始 -"));
            currentRepository.save(current);
        }

        // rounds loop
        int puzzleIndex = new Random().nextInt(puzzles.size());
        for (int round = 1; round <= 2; round++) {
            for (User user : room.getUsers()) {
                current = currentRepository.findByRoomId(roomId);
                current.setCount(new ArrayList<>());
                current.setPuzzle(puzzles.get(puzzleIndex));
                current.setDrawer(user.getEmail());
                current.setRound(round);
                currentRepository.save(current);
                template.convertAndSend("/topic/current/" + current.getRoomId(), current);
                log.info(current.toString());

                result.compute("order", (k, v) -> "drawer");
                template.convertAndSend("/topic/order/" + room.getId() , result);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // get ready
                template.convertAndSend("/topic/chat/" + roomId,
                        new Message("system",
                                "本轮提示: " + current.getPuzzle().getTip() + ", "
                                        + current.getPuzzle().getLength() + "个字"));
                result.compute("order", (k, v) -> "ready");
                template.convertAndSend("/topic/order/" + room.getId() , result);
                try {
                    Thread.sleep(4500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // start timer
                result.compute("order", (k, v) -> "timer");
                for (int i = 59; i >= 0; i--) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (currentRepository.findByRoomId(roomId).getCount().size() == room.getUsers().size()) {
                        result.compute("order", (k, v) -> "timesUp");
                        template.convertAndSend("/topic/order/" + room.getId() , result);
                        break;
                    }
                    result.put("timer", i);
                    System.out.print(i + " ");
                    template.convertAndSend("/topic/order/" + room.getId() , result);
                    result.remove("timer");
                }
                result.compute("order", (k, v) -> "timesUp");
                template.convertAndSend("/topic/order/" + room.getId() , result);

                // refresh puzzle
                puzzleIndex = (++puzzleIndex + new Random().nextInt(3)) % puzzles.size();
                // clean canvas
                template.convertAndSend("/topic/canvas/" + roomId, "{\"type\":\"clean\"}");

                // have a rest
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // game stop
        currentRepository.deleteByRoomId(roomId);
        result.compute("order", (k, v) -> "stop");
        log.warn(result.toString());
        template.convertAndSend("/topic/order/" + roomId , result);
        template.convertAndSend("/topic/chat/" + roomId, new Message("system", "- 游戏结束 -"));

    }


    @MessageMapping({"/order/{roomId}/comment"})
    public void comment(@DestinationVariable String roomId, Message message) {
        Map<String, Object> result = new HashMap<>();
        String msg;
        result.put("order", "comment");
        if (message.getMessage().equals("true")) {
            result.put("isEgg", true);
            msg = "\"" + message.getSender().toString() + "\"扔了个鸡蛋🥚";
        } else {
            result.put("isEgg", false);
            msg = "\"" + message.getSender().toString() + "\"奖励小花花🌹";
        }
        template.convertAndSend("/topic/order/" + roomId, result);
        template.convertAndSend("/topic/chat/" + roomId, new Message("system", msg));
    }
}
