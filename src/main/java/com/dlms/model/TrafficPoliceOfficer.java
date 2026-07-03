package com.dlms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TRAFFIC_POLICE_OFFICER")
@Data
public class TrafficPoliceOfficer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "police_seq")
    @SequenceGenerator(name = "police_seq", sequenceName = "POLICE_SEQ", allocationSize = 1)
    private Long policeOfficerId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String badgeNumber;
    private String stationName;
    private String rank;
}