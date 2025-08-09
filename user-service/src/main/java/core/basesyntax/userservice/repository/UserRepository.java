package core.basesyntax.userservice.repository;

import core.basesyntax.userservice.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select exists(select 1 from User where email = :email)")
    boolean existsAnyByEmail(@Param("email") String email);

    @Modifying
    @Query("update User set isDeleted = false where email = :email")
    int restoreUserByEmail(@Param("email") String email);
}
