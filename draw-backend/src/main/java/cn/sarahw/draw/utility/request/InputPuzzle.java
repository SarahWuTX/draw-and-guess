package cn.sarahw.draw.utility.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InputPuzzle {
    String tip;

    List<String> answers;
}
