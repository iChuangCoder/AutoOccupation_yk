package com.example.autooccupation.bean;

import lombok.Data;

@Data
public class OccupyUser {
    private String name;
    private String seatNum;
    private String seatId;
    private String sqid;
    private Boolean enable;

    public OccupyUser(String name,String sqid) {
        this.name = name;
        this.sqid = sqid;
    }

    public OccupyUser() {
    }
}
