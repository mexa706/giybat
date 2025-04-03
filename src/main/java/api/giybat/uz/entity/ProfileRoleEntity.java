package api.giybat.uz.entity;

import api.giybat.uz.enums.ProfileRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Table(name = "profile_role")
@Entity
@Setter
@Getter
public class ProfileRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "profile_id")
    private Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    @Column(name = "roles")
    @Enumerated(EnumType.STRING)
    private ProfileRole roles;

    @Column(name="created_date")
    private LocalDateTime createdDate;
}
