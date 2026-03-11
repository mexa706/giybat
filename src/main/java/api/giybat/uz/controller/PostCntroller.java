package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.post.*;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.service.PostService;
import api.giybat.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@Tag(name = "PostController", description = "API set for working with post")
public class PostCntroller {
    @Autowired
    private PostService postService;

    @PostMapping("")
    @Operation(summary = "Post create", description = "Api used for post creation")
    public ResponseEntity<PostDTO> creat(@Valid @RequestBody PostCreateDTO dto) {
        return ResponseEntity.ok().body(postService.createPost(dto));
    }


    @GetMapping("/profile")
    @Operation(summary = "Profile post list", description = "Get all profile post list")
    public ResponseEntity<Page<PostDTO>> profilePostList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                         @RequestParam(value = "size", defaultValue = "9") int size) {
        return ResponseEntity.ok().body(postService.getProfilePostList(PageUtil.page(page), size));
    }


    @GetMapping("/public/{id}")
    @Operation(summary = "Get post by id ", description = "Api returns post by id")
    public ResponseEntity<PostDTO> byID(@PathVariable String id) {
        return ResponseEntity.ok().body(postService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update post", description = "Api used for update post")
    public ResponseEntity<PostDTO> update(@PathVariable String id,
                                          @Valid @RequestBody PostCreateDTO dto) {
        return ResponseEntity.ok().body(postService.updatePost(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post by id", description = "Api used for deleting post")
    public ResponseEntity<Boolean> delete(@PathVariable String id) {
        return ResponseEntity.ok().body(postService.deletePost(id));
    }

    @PostMapping("/public/filter")
    @Operation(summary = "Post public filter", description = "Api used for post  filtering")
    public ResponseEntity<Page<PostDTO>> filter(@RequestBody PostFilterDTO dto,
                                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                @RequestParam(value = "size", defaultValue = "6") Integer size) {
        return ResponseEntity.ok().body(postService.filter(dto, PageUtil.page(page), size));
    }


    @PostMapping("/public/similar")
    @Operation(summary = "similar post list", description = "Getting similar profile post list")
    public ResponseEntity<List<PostDTO>> similarPostList(@Valid @RequestBody SimilarPostListDTO dto) {
        return ResponseEntity.ok().body(postService.getSimilarPostList(dto));
    }

    @PostMapping("/filter")
    @Operation(summary = "Post filter fot Admin ", description = "Api used for filtering post list fot admin ")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<PostDTO>> filter(@RequestHeader(value = "Accept-Language", defaultValue = "RU") AppLanguage language,
                                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "9") Integer size,
                                                      @RequestBody PostAdminFilterDTO dto) {
        return ResponseEntity.ok().body(postService.adminFilter(dto, language, PageUtil.page(page), size));
    }

}
