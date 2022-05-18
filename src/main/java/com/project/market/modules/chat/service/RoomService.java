package com.project.market.modules.chat.service;

import com.project.market.modules.chat.entity.QRoom;
import com.project.market.modules.chat.entity.Room;
import com.project.market.modules.chat.repository.RoomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.PriorityQueue;

import static com.project.market.modules.chat.entity.QRoom.room;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final JPAQueryFactory queryFactory;

    public Room findPreviousRoom(Long userAId, Long userBId) {
        PriorityQueue<Long> Q = new PriorityQueue<>();
        Q.add(userAId);
        Q.add(userBId);
        return queryFactory.select(room)
                .from(room)
                .where(room.userAId.eq(Q.poll()),
                        room.userBId.eq(Q.poll()))
                .fetchOne();
    }

    public void createRoom(String roomName, Long userAId, Long userBId) {
        PriorityQueue<Long> Q = new PriorityQueue<>();
        Q.add(userAId);
        Q.add(userBId);
        Room room = new Room(roomName, Q.poll(), Q.poll());
        roomRepository.save(room);
    }

    public List<Room> findRoomsByUserId(Long userId) {
        return queryFactory.selectFrom(room)
                .where(room.userAId.eq(userId).or(room.userBId.eq(userId)))
                .fetch();
    }
}
