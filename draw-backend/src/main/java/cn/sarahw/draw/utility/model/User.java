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
@Document(collection = "user")
public class User {

    @Id
    @JsonIgnore
    private ObjectId _id;

    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("name")
    private String name;

    @Field("avatar")
    private String avatar;

    @Field("room")
    private String roomId;

//    @Field("roomId")
//    private String roomId;

//    @Field("score")
//    private int score;
}
