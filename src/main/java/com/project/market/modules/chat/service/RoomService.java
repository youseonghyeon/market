package com.project.market.modules.chat.service;

import com.project.market.modules.chat.entity.QRoom;
import com.project.market.modules.chat.entity.Room;
import com.project.market.modules.chat.repository.RoomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.market.modules.chat.entity.QRoom.room;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final JPAQueryFactory queryFactory;

    public Room findPreviousRoom(Long userAId, Long userBId) {
        Long[] list = {userAId, userBId};
        return queryFactory.select(room)
                .from(room)
                .where(room.userAId.in(list),
                        room.userBId.in(list))
                .fetchOne();
    }

    public void createRoom(String roomName, Long userAId, Long userBId) {
        Room room = new Room(roomName, userAId, userBId);
        roomRepository.save(room);
    }

    public List<Room> findRoomsByUserId(Long userId) {
        return queryFactory.selectFrom(room)
                .where(room.userAId.eq(userId).or(room.userBId.eq(userId)))
                .fetch();
    }
}
