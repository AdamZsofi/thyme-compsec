package hu.bme.crysys.server.server.repository;

import hu.bme.crysys.server.server.domain.database.CaffComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaffCommentRepository extends JpaRepository<CaffComment, Integer> {}
