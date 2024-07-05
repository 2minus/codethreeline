package sparta.code3line.domain.follow.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.code3line.common.CommonResponse;
import sparta.code3line.domain.follow.dto.FollowRequestDto;
import sparta.code3line.domain.follow.dto.FollowResponseDto;
import sparta.code3line.domain.follow.dto.TopFollowerProfileResponseDto;
import sparta.code3line.domain.follow.service.FollowService;
import sparta.code3line.domain.user.entity.User;
import sparta.code3line.security.UserPrincipal;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<CommonResponse<FollowResponseDto>> createFollow(
            @RequestBody FollowRequestDto followRequestDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User currentUser = userPrincipal.getUser();
        followService.followUser(followRequestDto.getFollowingUserId(), currentUser);
        FollowResponseDto followResponseDto = new FollowResponseDto(followRequestDto.getFollowingUserId(), currentUser.getId(), LocalDateTime.now(), LocalDateTime.now());
        CommonResponse<FollowResponseDto> response = new CommonResponse<>(
                "팔로우 성공 🎉",
                HttpStatus.OK.value(),
                followResponseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<FollowResponseDto>> deleteFollow(
            @RequestBody FollowRequestDto followRequestDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User currentUser = userPrincipal.getUser();
        followService.unfollowUser(followRequestDto.getFollowingUserId(), currentUser);
        FollowResponseDto followResponseDto = new FollowResponseDto(followRequestDto.getFollowingUserId(), currentUser.getId(), LocalDateTime.now(), LocalDateTime.now());
        CommonResponse<FollowResponseDto> response = new CommonResponse<>(
                "언팔로우 성공 🎉",
                HttpStatus.OK.value(),
                followResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/top")
    public ResponseEntity<CommonResponse<List<TopFollowerProfileResponseDto>>> getTopFollower() {

        CommonResponse<List<TopFollowerProfileResponseDto>> response = new CommonResponse<>(
                "팔로우 랭킹 조회 성공 🎉",
                HttpStatus.OK.value(),
                followService.getTopFollowers()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
