package cn.sarahw.draw.dao;

import cn.sarahw.draw.utility.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findUserByEmail(String email);

    void deleteByEmail(String email);

    @Override
    <S extends User> S save(S s);
}
