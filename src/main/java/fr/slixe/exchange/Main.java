package fr.slixe.exchange;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.litarvan.paladin.Paladin;
import fr.litarvan.paladin.PaladinBuilder;
import fr.litarvan.paladin.PaladinConfig;
import fr.slixe.exchange.http.SparkHttpServer;

public class Main
{
	private static final Logger log = LoggerFactory.getLogger("Main");

	public static void main(String[] args)
	{
		Paladin paladin = PaladinBuilder.create(App.class)
							 			.addModule(new MyModule())
										.loadCommandLineArguments(args)
										.build();

		PaladinConfig config = paladin.getConfig();
		SparkHttpServer httpServer = new SparkHttpServer(paladin, config.get("port", int.class));
		
		if (config.get("enableSSL", boolean.class))
		{
			log.info("Enabling SSL...");

			String filePath = config.get("keystoreFile");
			char[] secret = config.get("secret").toCharArray();
			File file = new File(filePath);

			if (!file.exists()) {
				log.error("Keystore file at '{}' not found. Skipping SSL...", file.getAbsolutePath());
			}
			else {
				try {
					httpServer.loadSSLCert(file, secret);
					log.info("SSL is now enabled!");
				} catch (GeneralSecurityException | IOException e) {
					log.error(e.getLocalizedMessage());
				}
			}
		}

		paladin.start(httpServer);
	}
}