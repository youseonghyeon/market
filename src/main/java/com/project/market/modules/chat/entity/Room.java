package com.project.market.modules.chat.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id
    private String roomName;

    private Long userAId;
    private Long userBId;

    public Room(String roomName, Long userAId, Long userBId) {
        this.roomName = roomName;
        this.userAId = userAId;
        this.userBId = userBId;
    }

    public boolean includedUserId(Long userId) {
        if (userAId.equals(userId) || userBId.equals(userId)) {
            return true;
        }
        return false;
    }
}
