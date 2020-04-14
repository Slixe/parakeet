package fr.slixe.exchange.http.controller;

import fr.litarvan.paladin.http.routing.RequestException;

public class ServiceException extends RequestException {

	public ServiceException(String message) {
		super(message);
	}

}
