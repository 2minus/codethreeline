package sparta.code3line.domain.follow.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.code3line.common.Timestamp;
import sparta.code3line.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class Follow extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id", nullable = false)
    private Long id;

    // 상대
    @ManyToOne
    @JoinColumn(name = "following_user_id")
    private User following;

    // 사용자
    @ManyToOne
    @JoinColumn(name = "follower_user_id")
    private User follower;

    @Builder
    public Follow(User followingUser, User followerUser)
    {
        this.follower = followerUser;
        this.following =  followingUser;
    }

}
