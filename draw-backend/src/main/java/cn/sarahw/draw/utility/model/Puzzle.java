package cn.sarahw.draw.utility.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@Document(collection = "puzzle")
public class Puzzle {
    @Id
    @JsonIgnore
    private ObjectId _id;

    @Field("answer")
    private String answer;

    @Field("tip")
    private String tip;

    @Field("length")
    private int length;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Puzzle) {
            Puzzle p = (Puzzle) obj;
            return this.answer.equals(p.getAnswer());
        }
        return super.equals(obj);
    }
}