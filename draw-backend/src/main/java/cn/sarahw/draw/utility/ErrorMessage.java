package cn.sarahw.draw.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorMessage {
    public static final ResponseEntity<?> objectNotFound = new ResponseEntity<>("对象不存在", HttpStatus.NOT_FOUND);
    public static final ResponseEntity<?> userExist = new ResponseEntity<>("邮箱已被使用", HttpStatus.BAD_REQUEST);
    public static final ResponseEntity<?> roomExist = new ResponseEntity<>("房间名已被使用", HttpStatus.BAD_REQUEST);
    public static final ResponseEntity<?> roomFull = new ResponseEntity<>("房间人数已满", HttpStatus.BAD_REQUEST);
    public static final ResponseEntity<?> userNotInRoom = new ResponseEntity<>("用户不在房间内", HttpStatus.BAD_REQUEST);
    public static final ResponseEntity<?> userAlreadyInRoom = new ResponseEntity<>("用户已在房间内", HttpStatus.BAD_REQUEST);
}
