package fr.slixe.exchange.http;

import fr.litarvan.paladin.AfterEvent;
import fr.litarvan.paladin.BeforeEvent;
import fr.litarvan.paladin.http.Middleware;
import fr.litarvan.paladin.http.Request;
import fr.litarvan.paladin.http.Response;
import fr.litarvan.paladin.http.routing.RequestException;
import fr.litarvan.paladin.http.routing.Route;
import fr.slixe.exchange.structure.User;

public class AuthMiddleware extends Middleware
{
	@Override
	public void before(BeforeEvent event, Request request, Response response, Route route) throws RequestException
	{
		if (request.getSession().get(User.class) == null) {
			throw new UnauthorizedOperationException();
		}
	}

	@Override
	public void after(AfterEvent event, Request request, Response response, Route route) throws RequestException {}
}