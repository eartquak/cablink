package com.oops.cablink.repositories;

import com.oops.cablink.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    @Query("{email:'?0'}")
    User findByEmail(String email);

}
