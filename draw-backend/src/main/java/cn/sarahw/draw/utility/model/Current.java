package cn.sarahw.draw.utility.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "current")
public class Current {

    @Id
    @JsonIgnore
    private ObjectId _id;

    @Field("roomId")
    private String roomId;

    @Field("round")
    private int round;

    @Field("drawer")
    private String drawer;

    @Field("puzzle")
    private Puzzle puzzle;

    @Field("mark")
    private List<Map<String, Object>> mark;

    @Field("count")
    private List<String> count;

    public Current(String roomId) {
        this.roomId = roomId;
        this.round = 1;
        this.count = new ArrayList<>();
    }
}
