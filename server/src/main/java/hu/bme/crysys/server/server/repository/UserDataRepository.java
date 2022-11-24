package hu.bme.crysys.server.server.repository;

import hu.bme.crysys.server.server.domain.database.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Integer> {
    @Query("SELECT ud FROM UserData ud WHERE ud.userName = :username")
    public UserData findUserDataByUsername(@Param("username") String username);
}
