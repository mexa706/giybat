package api.giybat.uz.service;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.exps.AppBadExeptions;
import api.giybat.uz.repository.ProfileRopsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private ProfileRopsitory profileRopsitory;

    public ProfileEntity getById(Integer id) {

//        Optional<ProfileEntity> optional = profileRopsitory.findByIdAndVisibleTrue(id);
//        if (optional.isEmpty()) {
//            throw new AppBadExeptions("Profile not found");
//        }
//        return optional.get();

        return profileRopsitory.findByIdAndVisibleTrue(id).orElseThrow(() ->
        new AppBadExeptions("Profile not found")
        );

    }




}