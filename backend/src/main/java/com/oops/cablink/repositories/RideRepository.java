package com.oops.cablink.repositories;

import com.oops.cablink.models.Ride;
import com.oops.cablink.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends MongoRepository<Ride, ObjectId> {

    @Query("{ riders: ObjectId('?0')}")
    List<Ride> findRidesByHost(ObjectId id);

    @Query("{ _id: ObjectId('?0') , riders: ObjectId('?1') }")
    Optional<Ride> isUserInRide(ObjectId id, ObjectId riders);

    @Query("{ '_id': ObjectId('?0') }")
    @Update("{ $pull: { riders: ObjectId('?1') } }")
    int removeUserFromRide(ObjectId id, ObjectId riders);
}

