package sparta.code3line.domain.follow.dto;

import lombok.Data;
import sparta.code3line.domain.user.entity.User;

@Data
public class TopFollowerProfileResponseDto {
    private String nickname;
    private String profileImg;
    private String email;
    private Integer followerCount;

    public TopFollowerProfileResponseDto(User user, Integer followerCount) {
        this.nickname = user.getNickname();
        this.profileImg = user.getProfileImg();
        this.email = user.getEmail();
        this.followerCount = followerCount;
    }
}
