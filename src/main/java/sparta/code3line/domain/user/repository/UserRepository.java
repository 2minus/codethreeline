package sparta.code3line.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.code3line.domain.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String email);

    Optional<User> findBySocialId(String socialId);

    List<User> findAll();

    Optional<User> findByEmail(String email);

    Optional<User> findUserById(Long userId);
}
