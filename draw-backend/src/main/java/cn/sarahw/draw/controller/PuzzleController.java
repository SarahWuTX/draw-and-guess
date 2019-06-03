package cn.sarahw.draw.controller;

import cn.sarahw.draw.dao.PuzzleRepository;
import cn.sarahw.draw.utility.model.Puzzle;
import cn.sarahw.draw.utility.model.Room;
import cn.sarahw.draw.utility.request.InputPuzzle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@Slf4j
@RequestMapping("/puzzle")
@Validated
public class PuzzleController {
    private final PuzzleRepository puzzleRepository;

    @Autowired
    public PuzzleController(PuzzleRepository puzzleRepository) {
        this.puzzleRepository = puzzleRepository;
    }

    @PostMapping
    public ResponseEntity<?> generatePuzzle(@RequestBody @NotNull InputPuzzle inputPuzzle) {
        inputPuzzle.getAnswers().forEach(
                ans -> puzzleRepository.save(new Puzzle(null, ans, inputPuzzle.getTip(), ans.length()))
        );
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
