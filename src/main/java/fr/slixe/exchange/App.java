package fr.slixe.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import fr.litarvan.paladin.OnStart;
import fr.litarvan.paladin.OnStop;
import fr.litarvan.paladin.PaladinApp;
import fr.slixe.exchange.service.ArangoDatabaseService;
import fr.slixe.exchange.service.AuthService;
import spark.Spark;

@PaladinApp(name = "Exchange", version = App.VERSION, author = "Slixe")
public class App
{
	public static final String VERSION = "0.0.1";

	private static final Logger log = LoggerFactory.getLogger("Exchange");
	
	@Inject
	private ArangoDatabaseService db;
	
	@Inject
	private AuthService auth;
	
	@OnStart
	public void start()
	{
		log.info("Starting Database Service...");
		db.start();
		
		log.info("Starting Auth Service...");
		auth.start();
	}

	@OnStop
	public void stop()
	{
		log.info("Shutting down http service...");
		Spark.stop();
		
		log.info("Shutting down Database Service...");
		db.stop();
	}
}
