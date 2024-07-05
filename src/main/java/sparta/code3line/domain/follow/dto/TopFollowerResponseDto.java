package sparta.code3line.domain.follow.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class TopFollowerResponseDto {
    private Long userId;
    private Integer followerCount;

    @QueryProjection
    public TopFollowerResponseDto(Long userId, Integer followerCount) {
        this.userId = userId;
        this.followerCount = followerCount;
    }
}
