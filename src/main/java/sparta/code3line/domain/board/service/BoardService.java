package sparta.code3line.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sparta.code3line.common.exception.CustomException;
import sparta.code3line.common.exception.ErrorCode;
import sparta.code3line.domain.board.dto.BoardRequestDto;
import sparta.code3line.domain.board.dto.BoardResponseDto;
import sparta.code3line.domain.board.dto.BoardUpdateRequestDto;
import sparta.code3line.domain.board.entity.Board;
import sparta.code3line.domain.board.entity.BoardFiles;
import sparta.code3line.domain.board.repository.BoardFilesRepository;
import sparta.code3line.domain.board.repository.BoardRepository;
import sparta.code3line.domain.file.FileService;
import sparta.code3line.domain.follow.entity.Follow;
import sparta.code3line.domain.follow.repository.FollowRepository;
import sparta.code3line.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j(topic = "BoardService")
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final FollowRepository followRepository;
    private final FileService fileService;
    private final BoardFilesRepository boardFilesRepository;

    // USER에 해당하는 게시글 찾아오기
    public Board getBoard(User user, Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(()
                -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
        );
        if (!board.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.USER_DIFFERENT);
        }
        if (board.getType() != Board.BoardType.NORMAL) {
            throw new CustomException(ErrorCode.NOT_AUTHORIZED);
        }

        return board;

    }

    public List<BoardResponseDto> getFollowBoard(User user, Long offset) {

        List<Board> boards = boardRepository.getfollowingBoard(user.getId(), offset);
        List<BoardResponseDto> response = new ArrayList<>();

        for (Board board : boards) {
            response.add(new BoardResponseDto(board));
        }

        return response;
    }

    public List<BoardResponseDto> getFollowBoardOrderByName(User user, Long offset) {

        List<Board> boards = boardRepository.getfollowingBoardOrderByName(user.getId(), offset);
        List<BoardResponseDto> response = new ArrayList<>();

        for (Board board : boards) {
            response.add(new BoardResponseDto(board));
        }

        return response;
    }

    // 게시글 추가 메서드.
    public BoardResponseDto addBoard(
            User user,
            BoardRequestDto requestDto,
            List<MultipartFile> fileList) {

        log.info("addBoard 메서드 실행");

        Board board = Board.builder()
                .user(user)
                .title(requestDto.getTitle())
                .contents(requestDto.getContents())
                .type(Board.BoardType.NORMAL)
                .build();

        if (fileList != null) {
            addFileonBoard(board, fileList);
        }

        Board addBoard = boardRepository.save(board);

        return new BoardResponseDto(addBoard);
    }

    private void addFileonBoard(Board board, List<MultipartFile> fileList) {
        if (!fileList.isEmpty()) {
            List<BoardFiles> boardFiles = new ArrayList<>();
            List<String> urls = fileService.uploadFile(fileList);

            for (String url : urls) {
                BoardFiles boardFile = BoardFiles.builder()
                        .file(url)
                        .board(board)
                        .build();
                boardFiles.add(boardFile);
            }
            boardFilesRepository.saveAll(boardFiles);
        }
    }

    // 팔로우 조회
    public List<BoardResponseDto> getFollowBoard(User user) {

        if (user == null) {
            throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        }

        List<Follow> followingList = followRepository.findAllByFollowerId(user.getId());
        if (followingList.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOLLOW_POST);
        }

        List<Long> followingUserIds = new ArrayList<>();
        for (Follow follow : followingList) {
            followingUserIds.add(follow.getFollowing().getId());
        }

        List<Board> boards = boardRepository.findAllByUserIdInOrderByCreatedAtDesc(followingUserIds);
        List<BoardResponseDto> boardResponseDto = new ArrayList<>();

        for (Board board : boards) {
            boardResponseDto.add(new BoardResponseDto(board));
        }

        return boardResponseDto;

    }

    // 조회 : 일반 + 공지 게시글
    public Page<BoardResponseDto> getAllBoards(int page, int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Optional<Board> pickBoardOptional = boardRepository.findByType(Board.BoardType.PICK);

        List<BoardResponseDto> boardResponseDtoList = new ArrayList<>();
        pickBoardOptional.ifPresent(pickBoard -> boardResponseDtoList.add(new BoardResponseDto(pickBoard)));

        Page<Board> boardPage = boardRepository.findAllByTypeNot(Board.BoardType.PICK, (PageRequest) pageable);

        for (Board board : boardPage.getContent()) {
            boardResponseDtoList.add(new BoardResponseDto(board));
        }

        return new PageImpl<>(boardResponseDtoList, pageable, boardPage.getTotalElements());

    }

    // 조회 : 공지 게시글
    public Page<BoardResponseDto> getAllNoticeBoards(int page, int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Board> boardPage = boardRepository.findAllByType(Board.BoardType.NOTICE, pageable);

        return boardPage.map(BoardResponseDto::new);
    }

    // 조회 : 일반 게시글
    public Page<BoardResponseDto> getAllNormalBoards(int page, int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Board> boardPage = boardRepository.findAllByType(Board.BoardType.NORMAL, pageable);

        return boardPage.map(BoardResponseDto::new);

    }

    // 조회 : 부분 게시글
    public BoardResponseDto getOneBoard(Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(()
                -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        return new BoardResponseDto(board);

    }

    // 게시글 수정
    public BoardResponseDto updateBoard(
            User user,
            Long boardId,
            BoardUpdateRequestDto requestDto,
            List<MultipartFile> fileList) {

        Board board = getBoard(user, boardId);

        if (requestDto.getTitle() != null) {
            board.updateTitle(requestDto.getTitle());
        }

        if (requestDto.getContent() != null) {
            board.updateContents(requestDto.getContent());
        }

        if (fileList != null) {
            addFileonBoard(board, fileList);
        }

        boardRepository.save(board);

        return new BoardResponseDto(board);

    }

    // 게시글 삭제
    public void deleteBoard(User user, Long boardId) {

        Board board = getBoard(user, boardId);

        boardRepository.delete(board);

    }

}

