package sparta.code3line.domain.user.dto;

import lombok.Data;
import sparta.code3line.domain.user.entity.User;

import java.util.List;

@Data
public class UserResponseDto {

    private String username;
    private String roleName;
    private String nickname;
    private String email;
    private String profileImg;
    private List<User> allUsers;
    private Long likeBoardCount;
    private Long likeCommentCount;

    public UserResponseDto(User user){
        this.username = user.getUsername();
        this.roleName = user.getRole().getRoleName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.profileImg = user.getProfileImg();
        this.allUsers = null;
    }

    public void updateLikeCount(Long boardLikes, Long commentLikes) {
        this.likeBoardCount = boardLikes;
        this.likeCommentCount = commentLikes;
    }

}
