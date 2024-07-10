package com.example.cablink.repositories;

import com.example.cablink.models.Ride;
import com.example.cablink.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, ObjectId> {

    @Query("{host: ObjectId('?0')}")
    List<User> findRidesByHost(ObjectId id);
}

