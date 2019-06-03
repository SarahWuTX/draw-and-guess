package cn.sarahw.draw.controller;

import cn.sarahw.draw.dao.UserRepository;
import cn.sarahw.draw.utility.ErrorMessage;
import cn.sarahw.draw.utility.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@Slf4j
@RequestMapping("/user")
@Validated
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam @NotEmpty String email) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            return ErrorMessage.objectNotFound;
        } else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @PostMapping
    public ResponseEntity<?> postUser(@RequestBody @NotNull User inputUser) {
        try {
            userRepository.save(inputUser);
        } catch (Exception e) {
            return ErrorMessage.userExist;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteById(@RequestParam @NotEmpty String email) {
        User user = userRepository.findUserByEmail(email);
        if (user != null) {
            userRepository.deleteByEmail(email);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
