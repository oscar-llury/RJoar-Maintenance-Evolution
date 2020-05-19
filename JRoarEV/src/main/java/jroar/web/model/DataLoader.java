package jroar.web.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import jroar.web.repositories.UserRepository;


@Controller
public class DataLoader implements CommandLineRunner{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void run(String... args) throws Exception {
		User user = new User("Admin", "ApellidoAdmin", "admin@gmail.com", "admin");
		user.getRoles().add("ROLE_USER");
		user.getRoles().add("ROLE_ADMIN");

		userRepository.save(user);
		
		User user1 = new User("Jorge", "Molina", "jmol@gmail.com", "pass");
		user1.getRoles().add("ROLE_USER");
		
		userRepository.save(user1);
		
		User user2 = new User("Daniel", "Martin", "dmartin@gmail.com", "1234");
		user2.getRoles().add("ROLE_USER");
		
		userRepository.save(user2);
		
	}

}
