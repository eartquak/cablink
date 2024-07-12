package com.oops.cablink.repositories;

import com.oops.cablink.models.Ride;
import com.oops.cablink.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface RideRepository extends ReactiveMongoRepository<Ride, ObjectId> {

    @Query("{host: ObjectId('?0')}")
    Flux<User> findRidesByHost(ObjectId id);
}

