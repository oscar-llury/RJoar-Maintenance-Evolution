package jroar.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jroar.web.model.MountRating;

public interface MountRatingRepository extends JpaRepository<MountRating, Long>{

	@Query("SELECT COUNT(r) FROM MountRating r WHERE r.name=?1 AND r.islike=true")
	public int countLikes(String name);
	
	@Query("SELECT COUNT(r) FROM MountRating r WHERE r.name=?1 AND r.islike=false")
	public int countDisLikes(String name);
	
	public List<MountRating> findByName(String name);
	public List<MountRating> findByNameAndUserEmail(String name, String email);
}
