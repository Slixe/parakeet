package fr.slixe.exchange.http.controller;

import java.util.regex.Pattern

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.google.inject.Inject

import fr.litarvan.paladin.Session
import fr.litarvan.paladin.http.Controller
import fr.litarvan.paladin.http.routing.JsonBody
import fr.litarvan.paladin.http.routing.RequestParams
import fr.slixe.exchange.http.InvalidParameterException
import fr.slixe.exchange.service.AuthService
import fr.slixe.exchange.structure.User

public class AuthController extends Controller {

	/* Password Policy:
	 * At least 8 chars
	 * Contains at least one digit
	 * Contains at least one lower char and one upper char
	 */
	private static final Pattern passwordRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{8,}\$")
	private static final Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}\$", Pattern.CASE_INSENSITIVE)
	private static final Pattern usernameRegex = Pattern.compile("^[a-z0-9_-]{3,16}\$", Pattern.CASE_INSENSITIVE)
	private static final Logger log = LoggerFactory.getLogger("HTTP Auth Controller")

	@Inject
	private AuthService authService

	@JsonBody
	@RequestParams(required = ["identifier", "password"])
	def login(String identifier, String password, Session session)
	{
		if (password.length() > 64) {
			throw new InvalidParameterException("Password is too long")
		}

		User user
		if (usernameRegex.matcher(identifier).matches()) {
			user = authService.loginUsername(identifier, password)
		} else if (emailRegex.matcher(identifier).matches()) {
			user = authService.loginEmail(identifier, password)
		} else {
			throw new InvalidParameterException("Identifier invalid format")
		}

		if (user == null) {
			throw new InvalidParameterException("Username or password is incorrect")
		}

		log.info("User {} is now logged in.", user.getUsername())

		session[User] = user

		[
			token: session.token
		]
	}

	@JsonBody
	@RequestParams(required = ["username", "email", "password"])
	def register(String username, String email, String password, Session session)
	{
		if (!passwordRegex.matcher(password).matches()) {
			throw new InvalidParameterException("Invalid Password! Please see the Password Policy.")
		}

		if (password.length() > 64) {
			throw new InvalidParameterException("Password is too long!")
		}

		if (!emailRegex.matcher(email).matches()) {
			throw new InvalidParameterException("Email is not valid");
		}

		if (!usernameRegex.matcher(username).matches()) {
			throw new InvalidParameterException("Username is not valid");
		}

		User user = authService.register(username, email.toLowerCase(), password)

		if (user == null) {
			log.error("An error has occured in AuthController#register! User is null!")
			
			throw new InvalidParameterException("An error has occured!")
		}

		[
			message: "You are now registered!"
		]
	}

	//Header: Authorization: Bearer {{token}}
	def validate(Session session)
	{
		[
			token: session.token,
			logged: session[User] != null
		]
	}

	def logout(Session session)
	{
		session[User] = null
	}
}