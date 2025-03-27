package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class PointService {
    //private final PointRepository pointRepository;
    private final UserPointTable pointRepository;
    public PointService(UserPointTable pointRepository) {
        this.pointRepository = pointRepository;
    }

    public UserPoint getPointByUserId(long userId) {
        return pointRepository.selectById(userId);
    }

    public UserPoint getPointNotfoundUserId(long userId){
        UserPoint userPoint = pointRepository.selectById(userId);
        if(userPoint == null){
            throw new UserNotFoundException("User not found for id: "+userId);
        }
        return userPoint;
    }

    public UserPoint getIllegalArgErrorUserId(long userId){
        UserPoint userPoint = pointRepository.selectById(userId);

        if(0L > userId){
            throw new IllegalArgumentException("User not found for id: " + userId);
        }
        return userPoint;
    }
}
