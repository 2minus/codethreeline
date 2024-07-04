package sparta.code3line.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.code3line.common.CommonResponse;
import sparta.code3line.domain.board.dto.BoardResponseDto;
import sparta.code3line.domain.comment.dto.CommentResponseDto;
import sparta.code3line.domain.like.dto.LikeResponseDto;
import sparta.code3line.domain.like.service.LikeService;
import sparta.code3line.security.UserPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/boards/{id}/likes")
    public ResponseEntity<CommonResponse<LikeResponseDto>> createLikeBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        LikeResponseDto responseDto = likeService.createLikeBoard(id, principal.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(
                "게시글 좋아요 성공 🎉",
                HttpStatus.OK.value(),
                responseDto));

    }

    @GetMapping("/boards/likes")
    public ResponseEntity<CommonResponse<List<BoardResponseDto>>> readLikeBoard(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<BoardResponseDto> responseDtoList = likeService.readLikeBoard(principal.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(
                "좋아요 게시글 조회 성공 🎉",
                HttpStatus.OK.value(),
                responseDtoList));
    }

    @GetMapping("/boards/likes/Q")
    public ResponseEntity<CommonResponse<List<BoardResponseDto>>> readLikeBoardQ(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        List<BoardResponseDto> responseDtoList = likeService.readLikeBoardQWithPage(principal.getUser(), page, size);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(
                "좋아요 게시글 조회 성공 (QueryDSL) 🎉",
                HttpStatus.OK.value(),
                responseDtoList));
    }

    @DeleteMapping("/boards/{id}/likes")
    public ResponseEntity<CommonResponse<LikeResponseDto>> deleteLikeBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        LikeResponseDto responseDto = likeService.deleteLikeBoard(id, principal.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(
                "게시글 좋아요 취소 성공 🎉",
                HttpStatus.OK.value(),
                responseDto));

    }

    @PostMapping("/comments/{id}/likes")
    public ResponseEntity<CommonResponse<LikeResponseDto>> createLikeComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        LikeResponseDto responseDto = likeService.createLikeComment(id, principal.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(
                "댓글 좋아요 성공 🎉",
                HttpStatus.OK.value(),
                responseDto));

    }

    @GetMapping("/comments/likes")
    public ResponseEntity<CommonResponse<List<CommentResponseDto>>> readLikeComment(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<CommentResponseDto> responseDtoList = likeService.readLikeComment(principal.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(
                "좋아요 댓글 조회 성공 🎉",
                HttpStatus.OK.value(),
                responseDtoList));
    }

    @GetMapping("/comments/likes/Q")
    public ResponseEntity<CommonResponse<List<CommentResponseDto>>> readLikeCommentQ(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        List<CommentResponseDto> responseDtoList = likeService.readLikeCommentQWithPage(principal.getUser(), page, size);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(
                "좋아요 댓글 조회 성공 (QueryDSL) 🎉",
                HttpStatus.OK.value(),
                responseDtoList));
    }

    @DeleteMapping("/comments/{id}/likes")
    public ResponseEntity<CommonResponse<LikeResponseDto>> deleteLikeComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        LikeResponseDto responseDto = likeService.deleteLikeComment(id, principal.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse<>(
                "댓글 좋아요 취소 성공 🎉",
                HttpStatus.OK.value(),
                responseDto));

    }

}