package cn.sarahw.draw.utility.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Document(collection = "room")
public class Room {

    @Id
    @JsonIgnore
    private ObjectId _id;

    @Field("id")
    private String id;

    @Field("name")
    private String name;

    @Field("chat")
    private List<String[]> chat;

    @Field("users")
    private List<User> users;

    public Room() {
        this._id = null;
        this.id = "";
        this.name = "";
        this.chat = new ArrayList<>();
        this.users = new ArrayList<>();
    }

}
