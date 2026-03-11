package api.giybat.uz.service;

import api.giybat.uz.dto.FilterResultDTO;
import api.giybat.uz.dto.post.*;
import api.giybat.uz.dto.profile.ProfileDTO;
import api.giybat.uz.entity.PostEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.mapper.PostDetailMapper;
import api.giybat.uz.repository.CustomPostRepository;
import api.giybat.uz.repository.PostRopsitory;
import api.giybat.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRopsitory postRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private CustomPostRepository customRepository;

    public PostDTO createPost(PostCreateDTO postDTO) {

        PostEntity entity = new PostEntity();
        entity.setTitle(postDTO.getTitle());
        entity.setContent(postDTO.getContent());
        entity.setPhotoId(postDTO.getPhoto().getId());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setVisible(true);
        entity.setProfileId(SpringSecurityUtil.getCurrentUserId());
        postRepository.save(entity);

        return toInfoDTO(entity);
    }

    public Page<PostDTO> getProfilePostList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Integer profileId = SpringSecurityUtil.getCurrentUserId();

        Page<PostEntity> list = postRepository.getAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(profileId, pageRequest);
        List<PostDTO> dtoList = list.stream().map(this::toInfoDTO).toList();

        return new PageImpl<>(dtoList, pageRequest, list.getTotalElements());

    }

    public PostDTO getById(String postId) {
        PostEntity entity = get(postId);
        return toDTO(entity);
    }

    public PostDTO updatePost(String id, PostCreateDTO dto) {
        PostEntity entity = get(id);
        if (!SpringSecurityUtil.hasRole(ProfileRole.ROLE_ADMIN) &&
                !entity.getProfileId().equals(SpringSecurityUtil.getCurrentUserId())) {
            throw new AppBadException("You do not have the permission to change the post");
        }
        String oldPhotoId = null;
        if (entity.getPhoto() != null && dto.getPhoto() != null &&
                !entity.getPhoto().getId().equals(dto.getPhoto().getId())) {
            oldPhotoId = entity.getPhoto().getId();
        }

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setPhotoId(dto.getPhoto() != null ? dto.getPhoto().getId() : null);
        postRepository.updatePost(entity.getId(), entity.getTitle(), entity.getContent(), entity.getPhotoId());

        if (oldPhotoId != null) {
            attachService.delete(oldPhotoId);
        }
        return toDTO(entity);
    }

    public Boolean deletePost(String id) {

        Integer profileId = postRepository.findProfileIdByPostId(id);
        if (!SpringSecurityUtil.hasRole(ProfileRole.ROLE_ADMIN) &&
                !profileId.equals(SpringSecurityUtil.getCurrentUserId())) {
            throw new AppBadException("You do not have the permission to change the post");
        }
        postRepository.delete(id);
        return true;
    }


    public Page<PostDTO> filter(PostFilterDTO dto, int page, int pageSize) {
        FilterResultDTO<PostEntity> resultDTO = customRepository.filter(dto, page, pageSize);

        List<PostDTO> postDTOList = resultDTO.getList().stream()
                .map(this::toInfoDTO).toList();
        return new PageImpl<>(postDTOList, PageRequest.of(page, pageSize), resultDTO.getTotalCount());

    }


    public PostEntity get(String postId) {
        return postRepository.findByIdAndVisibleTrue(postId).orElseThrow(() ->
                new AppBadException("Post id not found  id : " + postId)
        );
    }

    public List<PostDTO> getSimilarPostList(SimilarPostListDTO dto) {


        List<PostEntity> posts = postRepository.getSimilarPostList(dto.getExceptId());
        return posts.stream().map(this::toDTO).toList();

    }

    public Page<PostDTO> adminFilter(PostAdminFilterDTO dto, AppLanguage language, Integer page, Integer size) {
        FilterResultDTO<Object[]> resultDTO = customRepository.filter(dto, page, size);

        List<PostDTO> postDTOList = resultDTO.getList().stream()
                .map(this::toDTO).toList();


        return new PageImpl<>(postDTOList, PageRequest.of(page, size), resultDTO.getTotalCount());
    }

    public PostDTO toDTO(PostEntity postEntity) {
        PostDTO dto = new PostDTO();
        dto.setId(postEntity.getId());
        dto.setTitle(postEntity.getTitle());
        dto.setContent(postEntity.getContent());
        dto.setCreatedDate(postEntity.getCreatedDate());
        dto.setPhoto(attachService.attachDTO(postEntity.getPhotoId()));
        return dto;
    }

    public PostDTO toDTO(Object[] obj) {
        PostDTO post = new PostDTO();
        post.setId((String)obj[0]);
        post.setTitle((String)obj[2]);
        post.setCreatedDate((LocalDateTime) obj[3]);
        if (obj[1]!=null) {
            post.setPhoto(attachService.attachDTO((String)obj[1]));
        }


        ProfileDTO profile = new ProfileDTO();
        profile.setId((Integer)obj[4]);
        profile.setName((String)obj[5]);
        profile.setUsername((String)obj[6]);

        post.setProfile(profile);
        return post ;
    }

    public PostDTO toInfoDTO(PostEntity postEntity) {
        PostDTO dto = new PostDTO();
        dto.setId(postEntity.getId());
        dto.setTitle(postEntity.getTitle());
        dto.setCreatedDate(postEntity.getCreatedDate());
        dto.setPhoto(attachService.attachDTO(postEntity.getPhotoId()));
        return dto;
    }
}
