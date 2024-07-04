package sparta.code3line.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sparta.code3line.common.exception.CustomException;
import sparta.code3line.common.exception.ErrorCode;
import sparta.code3line.domain.file.FileService;
import sparta.code3line.domain.like.repository.LikeBoardRepository;
import sparta.code3line.domain.like.repository.LikeCommentRepository;
import sparta.code3line.domain.user.dto.ProfileRequestDto;
import sparta.code3line.domain.user.dto.UserResponseDto;
import sparta.code3line.domain.user.entity.User;
import sparta.code3line.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Logger logger = Logger.getLogger(UserService.class.getName());

    private final FileService fileService;
    private final LikeBoardRepository likeBoardRepository;
    private final LikeCommentRepository likeCommentRepository;


    @Transactional
    public void deleteUser(Long userId, User currentUser) {
        User userToDelete = getUserById(userId);

        if (!currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new CustomException(ErrorCode.NOT_DELETED);
        }

        if (userToDelete.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_DELETED);
        }

        userToDelete.updateStatus(User.Status.DELETED);

    }

    @Transactional
    public void blockUser(Long userId, User currentUser) {
        User userToBlock = getUserById(userId);

        if (!currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new CustomException(ErrorCode.NOT_BLOCK);
        }

        if (userToBlock.isBlock()) {
            throw new CustomException(ErrorCode.ALREADY_BLOCK);
        }

        userToBlock.updateStatus(User.Status.BLOCK);

    }

    @Transactional
    public void adminUser(Long userId, User currentUser) {
        User userToAdmin = getUserById(userId);

        if (!currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new CustomException(ErrorCode.NOT_ADMIN);
        }

        if (userToAdmin.isUserToAdmin()) {
            throw new CustomException(ErrorCode.ALREADY_ADMIN);
        }

        if (userToAdmin.getStatus() == User.Status.DELETED) {
            throw new CustomException(ErrorCode.ALREADY_DELETED);
        }

        if (userToAdmin.getStatus() == User.Status.BLOCK) {
            throw new CustomException(ErrorCode.ALREADY_BLOCK);
        }

        userToAdmin.updateRole(User.Role.ADMIN);
    }

    // 프로필 변경
    @Transactional
    public UserResponseDto updateProfiles(Long userId, ProfileRequestDto profileRequestDto, List<MultipartFile> fileList) {
        User user = getUserById(userId);

        if (profileRequestDto.getNickname() != null && !profileRequestDto.getNickname().isEmpty()) {
            user.setNickname(profileRequestDto.getNickname());
            userRepository.save(user);
            logger.info("닉네임 변경 완료");
        }

        if (fileList != null) {

            if (user.getProfileImg() != null) {
                fileService.deleteFile(user.getProfileImg());
            }

            user.updateProfileImg(fileService.uploadFile(fileList).get(0));
            userRepository.save(user);
            logger.info("프로필 이미지 변경 완료");
        }

        return new UserResponseDto(user);

    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USERNAME_NOT_FOUND));

        UserResponseDto response = new UserResponseDto(user);
        response.updateLikeCount(likeBoardRepository.getLikeBoardCount(userId), likeCommentRepository.getLikeCommentCount(userId));

        return response;

    }

    // 사용자 프로필 가져오기
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUserProfiles(User currentUser) {

        List<UserResponseDto> userResponseDto = new ArrayList<>();

        if (currentUser.getRole() == User.Role.ADMIN) {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                userResponseDto.add(new UserResponseDto(user));
            }
        } else if(currentUser.getRole() == User.Role.NORMAL){
            User user = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USERNAME_NOT_FOUND));
            userResponseDto.add(new UserResponseDto(user));
        }

        return userResponseDto;

    }

    private User getUserById(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );
    }
}
