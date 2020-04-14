package fr.slixe.exchange.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import fr.slixe.exchange.http.controller.ServiceException;
import fr.slixe.exchange.structure.User;

@Singleton
public class AuthService {

	private static final Logger log = LoggerFactory.getLogger("Auth Service");
	
	//private final Argon2 argon2 = Argon2Factory.create();
	
	private int iterations = 10;
	
	@Inject
	private ArangoDatabaseService db;

	public void start()
	{
		//this.iterations = Argon2Helper.findIterations(argon2, 500, 65536, 1);
		log.info("Argon2id iterations found: {}", this.iterations);
	}

	public User login(User user, String password)
	{
		if (user == null) {
			return null;
		}

		String hashedPassword = hash(password);

		if (!user.getHashedPassword().equals(hashedPassword)) {
			return null;
		}

		return user;
	}

	public User loginUsername(String username, String password)
	{
		return login(db.getUserByUsername(username), password);
	}

	public User loginEmail(String email, String password)
	{
		return login(db.getUserByEmail(email), password);
	}
	
	public User register(String username, String email, String password) throws ServiceException
	{
		if (db.usernameExist(username)) {
			throw new ServiceException("Username '" + username + "' already exist.");
		}

		if (db.emailExist(email)) {
			throw new ServiceException("Email '" + email + "' already exist.");
		}

		String hashedPassword = hash(password);
		return db.createNewUser(username, email, hashedPassword);
	}
	
	public String hash(String password)
	{
		return Hashing.sha256().hashString(password, Charsets.UTF_8).toString();
		/*char[] charArray = password.toCharArray();
		String hash = argon2.hash(this.iterations, 65536, 1, charArray);
		argon2.wipeArray(charArray);
		return hash;*/
	}
}