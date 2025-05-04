package api.giybat.uz.repository;

import api.giybat.uz.entity.SmsProviderTokenHolder;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsProviderTokenRepository extends CrudRepository<SmsProviderTokenHolder,Integer> {
    Optional<SmsProviderTokenHolder> findTop1By();
}
