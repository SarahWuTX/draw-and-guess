package cn.sarahw.draw.dao;

import cn.sarahw.draw.utility.model.Puzzle;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuzzleRepository extends MongoRepository<Puzzle, ObjectId> {
    List<Puzzle> findAll();

    @Override
    <S extends Puzzle> S save(S s);
}
