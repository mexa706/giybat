package api.giybat.uz.mapper;

import api.giybat.uz.enums.GeneralStatus;

import java.time.LocalDateTime;

public interface PostDetailMapper {

    String getPostId();

    String getPostTitle();

    String getPostPhotoId();

    LocalDateTime getPostCreatedDate();

    String getProfileName();

    String getProfileUsername();

    Integer getProfileId();



}
