package sparta.code3line.domain.board.controller;


import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparta.code3line.common.CommonResponse;
import sparta.code3line.domain.board.dto.BoardRequestDto;
import sparta.code3line.domain.board.dto.BoardResponseDto;
import sparta.code3line.domain.board.dto.BoardUpdateRequestDto;
import sparta.code3line.domain.board.entity.SortType;
import sparta.code3line.domain.board.service.BoardService;
import sparta.code3line.security.UserPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 게시글 생성
    @PostMapping("/boards")
    public ResponseEntity<CommonResponse<BoardResponseDto>> addBoard(
            @RequestPart(value = "board") BoardRequestDto requestDto,
            @RequestPart(value = "file") List<MultipartFile> fileList,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        BoardResponseDto responseDto = boardService.addBoard(userPrincipal.getUser(), requestDto, fileList);
        CommonResponse<BoardResponseDto> commonResponse = new CommonResponse<>(
                "게시글 등록 성공",
                HttpStatus.CREATED.value(),
                responseDto
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);

    }

    // 조회 : 공지 + 일반 게시글 (최상단 PICK 게시글 + 일반 게시글)
    @GetMapping("/boards")
    public ResponseEntity<CommonResponse<Page<BoardResponseDto>>> getAllBoards(
            @RequestParam(defaultValue = "1") int page
    ) {

        int sizeFixed = 5;
        Page<BoardResponseDto> responseDto = boardService.getAllBoards(page - 1, sizeFixed);
        CommonResponse<Page<BoardResponseDto>> commonResponse = new CommonResponse<>(
                "공지 + 일반 게시글 " + page + "번 페이지 조회 완료",
                HttpStatus.OK.value(),
                responseDto
        );

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);

    }

    // 조회 : 공지 게시글
    @GetMapping("/boards/notice")
    public ResponseEntity<CommonResponse<Page<BoardResponseDto>>> getAllNoticeBoards(
            @RequestParam(defaultValue = "1") int page
    ) {

        int sizeFixed = 5;
        Page<BoardResponseDto> responseDto = boardService.getAllNoticeBoards(page - 1, sizeFixed);
        CommonResponse<Page<BoardResponseDto>> commonResponse = new CommonResponse<>(
                "공지 게시글 " + page + "번 페이지 조회 완료",
                HttpStatus.OK.value(),
                responseDto
        );

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);

    }

    // 조회 : 일반 게시글
    @GetMapping("/boards/normal")
    public ResponseEntity<CommonResponse<Page<BoardResponseDto>>> getAllNormalBoards(
            @RequestParam(defaultValue = "1") int page
    ) {

        int sizeFixed = 5;
        Page<BoardResponseDto> responseDto = boardService.getAllNormalBoards(page - 1, sizeFixed);
        CommonResponse<Page<BoardResponseDto>> commonResponse = new CommonResponse<>(
                "일반 게시글 " + page + "번 페이지 조회 완료",
                HttpStatus.OK.value(),
                responseDto
        );

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);

    }

    // 조회 : 부분 게시글
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<CommonResponse<BoardResponseDto>> getOneBoard(
            @PathVariable Long boardId
    ) {

        BoardResponseDto responseDto = boardService.getOneBoard(boardId);
        CommonResponse<BoardResponseDto> commonResponse = new CommonResponse<>(
                "게시글 단건 조회 완료.",
                HttpStatus.OK.value(),
                responseDto
        );

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);

    }


    @GetMapping("/board/follow")
    public ResponseEntity<CommonResponse<List<BoardResponseDto>>> getFollowBoard (
            @RequestParam("page") long page,
            @AuthenticationPrincipal UserPrincipal principal,
            @Nullable @RequestParam String type
            ) {

        SortType sortType = SortType.bySort(type);
        List<BoardResponseDto> result;

        if(sortType.getSortType().equals("name")){
            result = boardService.getFollowBoardOrderByName(principal.getUser(), page);
        } else {
            result = boardService.getFollowBoard(principal.getUser(), page);
        }

        CommonResponse<List<BoardResponseDto>> response = new CommonResponse<>(
                "팔로잉 게시글 조회 완료.",
                HttpStatus.OK.value(),
                result
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    // 게시글 수정
    @PutMapping("/boards/{boardId}")
    public ResponseEntity<CommonResponse<BoardResponseDto>> updateBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @RequestPart(value = "board") @Valid BoardUpdateRequestDto requestDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> fileList
    ) {

        BoardResponseDto responseDto = boardService.updateBoard(userPrincipal.getUser(), boardId, requestDto, fileList);
        CommonResponse<BoardResponseDto> commonResponse = new CommonResponse<>(
                "게시글 수정 완료",
                HttpStatus.OK.value(),
                responseDto
        );

        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);

    }

    // 게시글 삭제
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<CommonResponse<Void>> deleteBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId
    ) {

        boardService.deleteBoard(userPrincipal.getUser(), boardId);
        CommonResponse<Void> commonResponse = new CommonResponse<>(
                "게시글 삭제 완료",
                HttpStatus.OK.value(),
                null
        );

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(commonResponse);

    }

    // 조회 : 팔로우하는 사용자의 게시글
    @GetMapping("/boards/follows")
    public ResponseEntity<CommonResponse<List<BoardResponseDto>>> getFollowBoard(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {

        if (userPrincipal == null || userPrincipal.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<BoardResponseDto> followBoardList = boardService.getFollowBoard(userPrincipal.getUser());
        CommonResponse<List<BoardResponseDto>> response = new CommonResponse<>(
                "게시글 조회 성공 🎉",
                HttpStatus.OK.value(),
                followBoardList
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

}

