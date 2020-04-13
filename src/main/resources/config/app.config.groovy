package config;

import java.util.concurrent.TimeUnit

import fr.litarvan.paladin.http.AcceptCrossOriginRequestsMiddleware
import fr.slixe.exchange.http.AuthMiddleware
import fr.slixe.exchange.http.controller.AuthController
import fr.slixe.exchange.http.controller.MarketController
import fr.slixe.exchange.http.controller.UserController

[
    sessionDuration: TimeUnit.DAYS.toMillis(61), // 2 Months

    /**
     * The app controllers, call them whatever you want to
     */
    controllers: [
        user: UserController,
		auth: AuthController,
		market: MarketController
    ],

	routeMiddlewares: [
		auth: AuthMiddleware
	],
	
    /**
     * Global middlewares (applied on all routes)
     */
    globalMiddlewares: [
        // Remove comment if you want your API to be callable from any website
        AcceptCrossOriginRequestsMiddleware
    ]
]