package api.giybat.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "sms_provider_token_holder")
public class SmsProviderTokenHolder  {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name="token" , columnDefinition = "text")
    private String token;

    @Column(name="created_date")
    private LocalDateTime createdDate;

    @Column(name="expired_date")
    private LocalDateTime expiredDate;


}
