package fr.slixe.exchange.http;

import fr.litarvan.paladin.http.routing.RequestException;

public class InvalidParameterException extends RequestException
{
	public InvalidParameterException(String message)
	{
		super("Invalid parameter : " + message);
	}
}
