package jroar.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jroar.web.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{
	public List<Comment> findByMountPoint(String Mountpoint);
}
