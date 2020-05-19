package jroar.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import jroar.web.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByEmail(String email);
}
