package com.project.market.modules.chat.repository;

import com.project.market.modules.chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByRoomName(String roomName);

//    @Query("select r from room r where r.userAId = ?1 or r.userBId = ?1")
//    List<Room> findRoomsByParticipantId(Long participantId);
}
