package com.example.cablink.repositories;

import com.example.cablink.models.User;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    @Query("{email:'?0'}")
    User findByEmail(String email);

    @Query("{name:'?0'}")
    User findByName(String name);

}
