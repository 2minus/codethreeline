> 🛠️ **들어가며**
> 
>테스트 코드 작성과 추가 기능 구현이 거의 되지 않은 상태입니다. 주말 동안 열심히 시도해 볼테니 혹시 이 메세지를 보신다면 아직 열심히 수정 중인 상태이니 조금만 대충 봐주시길 바랍니다ㅠ

> 🥔**진행상황**
> 
>  ❗***필수 구현 기능***❗
> - [X] **❗좋아요 추가하기**
> - [X]  **❗내가 좋아하는 게시글 목록 조회기능 추가하기**
> - [X]  **❗내가 좋아하는 댓글 목록 조회기능 추가하기**
> - [ ]  **❗프로필에 내가 좋아요한 게시글 수/댓글 수 응답필드 추가하기** 
>
> 
>  ✨***추가 구현 기능***✨
> - [X]  **✨팔로우 기능 추가**
> - [X]  **✨팔로워 게시글 목록 조회기능 추가**
> - [X]  **✨팔로워 게시글 목록 조회 기능에 정렬 기준 추가**
> 
> 
> 🏆 ***명예의 전당***🏆
> - [ ]  **🏆팔로워 TOP 10 목록 조회기능 추가**
> - [ ]  **🏆팔로워 게시글 목록 조회 기능에 필터 추가**


> 🚩 **Requirement:  과제에 요구되는 사항이에요**

- **최대한 QueryDSL 를 사용하여 구현해주세요**

  nativeQuery 사용은 최대한 지양 해주시고 QueryDSL 을 적극적으로 사용해서 가독성과 코드 재사용성을 높여주세요!

> ⚠️  Warning : 꼭 지켜야 할 것!
<details>
<summary> ⚠️  </summary>

- ⚠️  Pageable 꼭 사용하기
    - 목록 조회는 기본적으로 페이징 조회가 기본입니다.
    - 쿼리를 수행하실때 꼭 Pageable 을 사용하셔서 페이징 조회가 가능하도록 해주세요.
    - PageRequest.of() 메서드를 사용해서 손쉽게 만들어보실 수 있습니다.


- ⚠️ TestCode 꼭 작성하기
    - 쿼리와 기능이 복잡할수록 TestCode 의 역할 더 중요해집니다!
    - 각 기능의 상황별 TestCode 를 정의함으로써 기능이 잘 동작하는 것을 보장해주세요.
    - 슬라이싱 테스트로 작성하는걸 추천드립니다.
</details>


> ❗ 필수 구현 기능

<details>
<summary> ❗ </summary>

- [X] **🆕 좋아요 추가하기**
  - **게시글 및 댓글 좋아요 / 좋아요 취소 기능**
    - (지난번 명예의 전당 좋아요 기능과 동일합니다.)
    - 사용자가 게시물이나 댓글에 좋아요를 남기거나 취소할 수 있습니다.
    - 본인이 작성한 게시물과 댓글에 좋아요를 남길 수 없습니다.
    - 같은 게시물에는 사용자당 한 번만 좋아요가 가능합니다.
  
    ```java
        public LikeResponseDto createLikeBoard(Long id, User user) {
        
                Board board = boardRepository.findById(id).orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND)
                );
                
                // 본인이 작성한 게시물에 대한 예외처리
                if (Objects.equals(board.getUser().getId(), user.getId())) {
                    throw new CustomException(ErrorCode.LIKE_ME);
                } 
                
                // 이미 좋아요한 게시물에 대한 예외처리
                if (likeBoardRepository.findByUserIdAndBoardId(user.getId(), board.getId()).isPresent()) {
                    throw new CustomException(ErrorCode.ALREADY_LIKE);
                }
                
                // 좋아요 게시물 엔티티 생성
                LikeBoard likeBoard = LikeBoard.builder()
                        .user(user)
                        .board(board)
                        .build();
                
                // 저장 및 게시물 좋아요 정보 업데이트
                likeBoardRepository.save(likeBoard);
                board.updateLikesCount();
                boardRepository.save(board);
                return new LikeResponseDto(likeBoard);
        
            }
    ```

  - **게시글 및 댓글 단건조회 응답에 좋아요 개수 추가**
    - 게시글 단건 정보 조회시 게시글의 좋아요 개수필드를 추가합니다.
    - 댓글 단건 정보 조회시 댓글의 좋아요 개수필드를 추가합니다.
    ```java
    // board.java
    private int likeCount;
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeBoard> likes = new ArrayList<>();
    
    public void updateLikesCount() {
        this.likeCount = this.likes.size();
    }
    ``` 

- [X]  **🆕 내가 좋아하는 게시글 목록 조회기능 추가하기**
  - **좋아요 한 게시글 목록 조회 기능**
    - 사용자가 좋아요 했던 게시글 목록을 조회할 수 있습니다.
    - 응답정보는 기존 게시글 목록 조회기능 응답정보와 동일합니다.
    - 기본 정렬은 **생성일자 기준으로 최신순**으로 정렬합니다.
    - 페이지네이션
      - 페이지네이션하여 각 페이지 당 게시물 데이터가 5개씩 나오게 합니다.
    ```java
    @Override
    public List<LikeBoard> getLikeBoardsbyUserId(Long userId, long offset, int pagesize) {
    
            User user = userRepository.findUserById(userId).orElseThrow(
                    ()-> new CustomException(ErrorCode.USER_DIFFERENT)
            );
    
            OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, likeBoard.board.createdAt);
    
            return jpaQueryFactory.selectFrom(likeBoard)
                    .where(likeBoard.user.eq(user))
                    .leftJoin(likeBoard.board).fetchJoin()
                    .offset(offset)
                    .limit(pagesize)
                    .orderBy(orderSpecifier)
                    .fetch();
        }
    ```     
    ```json
    {
        "msg": "좋아요 게시글 조회 성공 (QueryDSL) 🎉",
        "status": 200,
        "result": [
                    {
                    "nickname": "박셋",
                    "boardId": 9,
                    "title": "개웃긴 제목",
                    "contents": "개웃긴 내용",
                    "likeCount": 1
                    },
                    {
                    "nickname": "박셋",
                    "boardId": 6,
                    "title": "신선한 제목",
                    "contents": "신선한 내용",
                    "likeCount": 1
                    },
                    {
                    "nickname": "이둘",
                    "boardId": 5,
                    "title": "재미없는 제목",
                    "contents": "재미없는 내용",
                    "likeCount": 1
                    }
                ]
    }   
    ``` 

- [X]  **🆕 내가 좋아하는 댓글 목록 조회기능 추가하기**
  - **좋아요 한 댓글 목록 조회 기능**
    - 사용자가 좋아요 했던 게시글 목록을 조회할 수 있습니다.
    - 응답정보는 기존 댓글의 단건 조회기능 응답정보를 목록으로 응답합니다.
    - 기본 정렬은 **생성일자 기준으로 최신순**으로 정렬합니다.
    - 페이지네이션
      - 페이지네이션하여 각 페이지 당 게시물 데이터가 5개씩 나오게 합니다.


- [X]  **🆕 프로필에 내가 좋아요한 게시글 수/댓글 수 응답필드 추가하기**
  - **프로필 조회응답에 필드 추가**
    - 프로필 조회시 응답필드에 내가 좋아요한 게시글 수 필드를 추가합니다.
    - 프로필 조회시 응답필드에 내가 좋아요한 댓글 수 필드를 추가합니다.
```java
@Override
    public Long getLikeBoardCount(Long userId) {
        return jpaQueryFactory.select(likeBoard.count())
                .from(likeBoard)
                .where(likeBoard.user.id.eq(userId))
                .fetchOne();
    }
```
> 의도한 sql문
>```sql
>select count(*) from like_board
>where user_id = 1
>```
```json
{
  "msg": "프로필 조회 성공 🎉",
  "status": 200,
  "result": [
    {
      "username": "username1",
      "roleName": "NORMAL",
      "nickname": "김하나",
      "email": "user1@email.com",
      "profileImg": null,
      "allUsers": null,
      "likeBoardCount": null,
      "likeCommentCount": null
    }
  ]
}
```
- 왜인지 알 수 없지만 제대로 값이 들어가지 않는 것 같다.

</details>

> ✨ 추가 구현 기능 - 이것들까지 구현하면 너무 좋아요! 👍🏻

<details>
<summary> ✨ </summary>

- [X]  **팔로우 기능 추가**
  - (지난번 명예의 전당 팔로우 기능과 동일합니다.)
  - 사용자가 다른 사용자에게 팔로우를 하거나 팔로우 취소를 할 수 있습니다.
  - 본인 자신에게는 팔로우를 할 수 없습니다.
  - 한명의 사용자에게는 한번의 팔로우만 할 수 있습니다.
```java
// 팔로우 기능
    public void followUser(Long followingUserId, User follower) {
        
        // 사용자가 유효하지 않을때의 예외처리
        if (followingUserId == null) {
            throw new CustomException(NOT_FOLLOWED_ID);
        }
        if (followingUserId.equals(follower.getId())) {
            throw new CustomException(NOT_FOLLOW);
        }

        User followingUser = findUser(followingUserId);
        
        // 이미 팔로우 했을 경우 예외처리
        if (isAlreadyFollowing(followingUserId, follower.getId())) {
            throw new CustomException(ALREADY_FOLLOW);
        }

        Follow follow = new Follow(followingUser, follower);
        followRepository.save(follow);

    }
```
```json
{
  "msg": "팔로우 성공 🎉",
  "status": 200,
  "result": {
    "createdAt": "2024-07-05 09:41:51",
    "modifiedAt": "2024-07-05 09:41:51",
    "following_user_id": 3,
    "follower_user_id": 1
  }
}
```



- [X]  **팔로워 게시글 목록 조회기능 추가**
  - 자신이 팔로우 하고 있는 유저들의 게시글만 목록으로 조회 할 수 있습니다.
  - 응답정보는 기존 게시글 목록 조회기능 응답정보와 동일합니다.
  - 기본 정렬은 **생성일자 기준으로 최신순**으로 정렬합니다.
  - 페이지네이션
    - 페이지네이션하여 각 페이지 당 게시물 데이터가 5개씩 나오게 합니다.


- [X]  **팔로워 게시글 목록 조회 기능에 정렬 기준 추가**
  - 팔로워 게시글 목록 조회 기능에 작성자명 기준 정렬기능을 추가합니다.
  - 응답정보는 기존 게시글 목록 조회기능 응답정보와 동일합니다.
  - 페이지네이션
    - 페이지네이션하여 각 페이지 당 게시물 데이터가 5개씩 나오게 합니다.
```java
// BoardRepositoryImpl.java
@Override
    public List<Board> getfollowingBoardOrderByName(Long userId, long offset) {
        // 생성일자 정렬
        // OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, board.createdAt);
        // 작성자 정렬
        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, board.user.nickname);

        return jpaQueryFactory.select(board)
                .from(board)
                .where(
                        JPAExpressions.select(follow)
                                .from(follow)
                                .where(follow.follower.id.eq(userId)
                                                .and (follow.following.id.eq(board.user.id)))
                                .exists()
                )
                .groupBy(board.id)
                .offset(offset)
                .orderBy(orderSpecifier)
                .limit(5) // 페이지 당 제한이 걸려있으므로 하드코딩 해버림
                .fetch();
    }
}
```
> 생성일자 순 정렬
```json 
{
  "msg": "팔로잉 게시글 조회 완료.",
  "status": 200,
  "result": [
    {
      "nickname": "이둘",
      "boardId": 8,
      "title": "더 신선한 제목",
      "contents": "더 신선한 내용",
      "likeCount": 0
    },
    {
      "nickname": "이둘",
      "boardId": 7,
      "title": "신선한 제목",
      "contents": "신선한 내용",
      "likeCount": 0
    },
    {
      "nickname": "박셋",
      "boardId": 6,
      "title": "신선한 제목",
      "contents": "신선한 내용",
      "likeCount": 1
    },
    {
      "nickname": "이둘",
      "boardId": 5,
      "title": "재미없는 제목",
      "contents": "재미없는 내용",
      "likeCount": 1
    },
    {
      "nickname": "이둘",
      "boardId": 4,
      "title": "따라하는 제목",
      "contents": "그냥 내용",
      "likeCount": 0
    }
  ]
}
```
> 작성자 명 정렬
```json
{
  "msg": "팔로잉 게시글 조회 완료.",
  "status": 200,
  "result": [
    {
      "nickname": "이둘",
      "boardId": 4,
      "title": "따라하는 제목",
      "contents": "그냥 내용",
      "likeCount": 0
    },
    {
      "nickname": "이둘",
      "boardId": 5,
      "title": "재미없는 제목",
      "contents": "재미없는 내용",
      "likeCount": 1
    },
    {
      "nickname": "이둘",
      "boardId": 7,
      "title": "신선한 제목",
      "contents": "신선한 내용",
      "likeCount": 0
    },
    {
      "nickname": "이둘",
      "boardId": 8,
      "title": "더 신선한 제목",
      "contents": "더 신선한 내용",
      "likeCount": 0
    },
    {
      "nickname": "박셋",
      "boardId": 6,
      "title": "신선한 제목",
      "contents": "신선한 내용",
      "likeCount": 1
    }
  ]
}
```

</details>


> 🏆 명예의 전당 - 스스로 나를 뛰어넘어 봅시다! 😎

<details>
<summary> 🏆 </summary>

- [ ]  **팔로워 TOP 10 목록 조회기능 추가**
  - 팔로워를 가장 많이 보유한 상위 10명의 프로필 정보 목록을 조회합니다.
  - 정렬 없이 10명의 프로필 정보가 모두 나오게 합니다.
  - 프로필 정보와 함께 몇명의 팔로워를 가지고 있는지 출력해줍니다.


- [ ]  **팔로워 게시글 목록 조회 기능에 필터 추가**
  - 팔로워 게시글 목록 조회 기능에 작성자 필터 기능을 추가합니다.
  - 응답정보는 기존 게시글 목록 조회기능 응답정보와 동일합니다.
  - XXXSearchCond 클래스를 만들어서 필터 조건들을 명시해서 구현해주세요.
  - 필터 할때도 페이지네이션은 적용되어야 합니다.
    - 페이지네이션하여 각 페이지 당 게시물 데이터가 5개씩 나오게 합니다.

</details>