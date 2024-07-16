package com.oops.cablink.services;

import com.oops.cablink.repositories.RideRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RideService {

    @Autowired
    RideRepository rideRepository;

    public void removeUserFromRideByID(ObjectId id) {
    }

}
