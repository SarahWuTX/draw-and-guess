package cn.sarahw.draw.dao;

import cn.sarahw.draw.utility.model.Room;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends MongoRepository<Room, ObjectId> {
    Room findRoomById(String id);

    List<Room> findAllByIdContainsOrNameContains(String k1, String k2);

    @Override
    <S extends Room> S save(S s);
}
