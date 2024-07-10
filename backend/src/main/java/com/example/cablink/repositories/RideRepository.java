package com.example.cablink.repositories;

import com.example.cablink.models.Ride;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends MongoRepository<Ride, ObjectId> {

    @Query("{name:'?0'}")
    Ride findByName(String name);

}

