package sparta.code3line.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sparta.code3line.common.exception.CustomException;
import sparta.code3line.common.exception.ErrorCode;
import sparta.code3line.domain.board.dto.BoardResponseDto;
import sparta.code3line.domain.board.entity.Board;
import sparta.code3line.domain.board.repository.BoardRepository;
import sparta.code3line.domain.comment.dto.CommentResponseDto;
import sparta.code3line.domain.comment.entity.Comment;
import sparta.code3line.domain.comment.repository.CommentRepository;
import sparta.code3line.domain.like.dto.LikeResponseDto;
import sparta.code3line.domain.like.entity.LikeBoard;
import sparta.code3line.domain.like.entity.LikeComment;
import sparta.code3line.domain.like.repository.LikeBoardRepository;
import sparta.code3line.domain.like.repository.LikeCommentRepository;
import sparta.code3line.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikeBoardRepository likeBoardRepository;
    private final LikeCommentRepository likeCommentRepository;

    public LikeResponseDto createLikeBoard(Long id, User user) {

        Board board = boardRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        if (Objects.equals(board.getUser().getId(), user.getId())) {
            throw new CustomException(ErrorCode.LIKE_ME);
        }

        if (likeBoardRepository.findByUserIdAndBoardId(user.getId(), board.getId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_LIKE);
        }

        LikeBoard likeBoard = LikeBoard.builder()
                .user(user)
                .board(board)
                .build();

        likeBoardRepository.save(likeBoard);
        board.updateLikesCount();
        boardRepository.save(board);
        return new LikeResponseDto(likeBoard);

    }

    public List<BoardResponseDto> readLikeBoard (User user) {

        List<LikeBoard> LikeBoards = likeBoardRepository.findAllByUserId(user.getId());
        List<BoardResponseDto> boards = new ArrayList<>();

        for (LikeBoard likeBoard : LikeBoards) {
            boards.add(new BoardResponseDto(likeBoard.getBoard()));
        }

        return boards;
    }

    public List<BoardResponseDto> readLikeBoardQWithPage (User user, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        List<BoardResponseDto> response = new ArrayList<>();
        List<LikeBoard> likeBoards = likeBoardRepository.getLikeBoardsbyUserId(user.getId(), pageRequest.getOffset(), pageRequest.getPageSize());

        for (LikeBoard likeBoard : likeBoards) {
            response.add(new BoardResponseDto(likeBoard.getBoard()));
        }

        return response;
    }

    public LikeResponseDto deleteLikeBoard(Long id, User user) {

        Board board = boardRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        LikeBoard likeBoard = likeBoardRepository.findByUserIdAndBoardId(user.getId(), board.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_LIKE)
        );

        likeBoardRepository.delete(likeBoard);
        board.updateLikesCount();
        boardRepository.save(board);
        return new LikeResponseDto(likeBoard);

    }

    public LikeResponseDto createLikeComment(Long id, User user) {

        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        if (Objects.equals(comment.getUser().getId(), user.getId())) {
            throw new CustomException(ErrorCode.LIKE_ME);
        }

        if (likeCommentRepository.findByUserIdAndCommentId(user.getId(), comment.getId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_LIKE);
        }

        LikeComment likeComment = LikeComment.builder()
                .user(user)
                .comment(comment)
                .build();

        likeCommentRepository.save(likeComment);
        comment.updateLikesCount();
        commentRepository.save(comment);
        return new LikeResponseDto(likeComment);

    }

    public List<CommentResponseDto> readLikeComment(User user) {

        List<LikeComment> LikeComments = likeCommentRepository.findAllByUserId(user.getId());
        List<CommentResponseDto> comments = new ArrayList<>();

        for (LikeComment likeComment : LikeComments) {
            comments.add(new CommentResponseDto(likeComment.getComment()));
        }

        return comments;

    }

    public List<CommentResponseDto> readLikeCommentQWithPage(User user, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        List<CommentResponseDto> response = new ArrayList<>();
        List<LikeComment> likeComments = likeCommentRepository.getLikeCommentsbyUserId(user.getId(), pageRequest.getOffset(), pageRequest.getPageSize());

        for (LikeComment likeComment : likeComments) {
            response.add(new CommentResponseDto(likeComment.getComment()));
        }

        return response;
    }

    public LikeResponseDto deleteLikeComment(Long id, User user) {

        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        LikeComment likeComment = likeCommentRepository.findByUserIdAndCommentId(user.getId(), comment.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_LIKE)
        );

        likeCommentRepository.delete(likeComment);
        comment.updateLikesCount();
        commentRepository.save(comment);
        return new LikeResponseDto(likeComment);

    }
}
