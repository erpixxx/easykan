package dev.erpix.easykan.server.domain.comment.repository;

import dev.erpix.easykan.server.domain.comment.model.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

}
