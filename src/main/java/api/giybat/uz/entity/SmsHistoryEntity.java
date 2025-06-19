package api.giybat.uz.entity;

import api.giybat.uz.enums.SmsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sms_history")
public class SmsHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="phone")
    private String phone;

    @Column(name="code")
    private String code;

    @Column(name="created_date")
    private LocalDateTime createdDate;

    @Column(name="message", columnDefinition = "text")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name="sms_type")
    private SmsType smsType;

    @Column(name="attempt_count")
    private Integer attemptCount=0;

}
