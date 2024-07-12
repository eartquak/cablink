package com.oops.cablink.repositories;

import com.oops.cablink.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    @Query("{email:'?0'}")
    User findByEmail(String email);

    @Query("{name:'?0'}")
    User findByName(String name);

}
