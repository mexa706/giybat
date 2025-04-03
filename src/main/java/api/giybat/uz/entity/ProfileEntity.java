package api.giybat.uz.entity;

import api.giybat.uz.enums.GeneralStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Table(name = "profile")
@Entity
@Setter
@Getter
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="name")
    private String name;
    @Column(name="username")
    private String username;
    @Column(name="password")
    private String password;
    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private GeneralStatus status;
    @Column(name="created_date")
    private LocalDateTime createdDate;
    @Column(name="visible")
    private Boolean visible=Boolean.TRUE;

}
