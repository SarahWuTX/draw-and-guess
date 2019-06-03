package cn.sarahw.draw.dao;

import cn.sarahw.draw.utility.model.Current;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CurrentRepository extends MongoRepository<Current, ObjectId> {
    Current findByRoomId(String roomId);

    void deleteByRoomId(String roomId);

    @Override
    <S extends Current> S save(S s);
}