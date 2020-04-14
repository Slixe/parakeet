package fr.slixe.exchange.http.controller;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.google.inject.Inject

import fr.litarvan.paladin.http.Controller
import fr.litarvan.paladin.http.routing.JsonBody
import fr.litarvan.paladin.http.routing.RequestParams
import fr.slixe.exchange.http.InvalidParameterException
import fr.slixe.exchange.service.ArangoDatabaseService
import fr.slixe.exchange.service.FundsService
import fr.slixe.exchange.service.MarketService
import fr.slixe.exchange.structure.Market
import fr.slixe.exchange.structure.User
import fr.slixe.exchange.structure.ActiveOrder.Type

public class MarketController extends Controller {

	private static final Logger log = LoggerFactory.getLogger("HTTP Market Controller")

	@Inject
	private ArangoDatabaseService db

	@Inject
	private FundsService funds

	@Inject
	private MarketService market

	@RequestParams(required = [], optional = "fr.slixe.exchange.structure.Market:market")
	def activeOrders(Optional<Market> market, User user)
	{
		def orders

		if (market.isPresent()) {
			orders = db.getActiveOrders(user, market.get())
		} else {
			orders = db.getActiveOrders(user)
		}

		[
			activeOrders: orders
		]
	}

	@RequestParams(required = ["market"])
	def details(Market market)
	{
		[
			message: "TODO 24h infos $market"
		]
	}

	@JsonBody
	@RequestParams(required = ["market", "amount", "price", "type"])
	def createOrder(Market market, BigDecimal amount, BigDecimal price, Type type, User user)
	{
		if (amount.signum() != 1 || price.signum() != 1) {
			throw new InvalidParameterException("Values must be positive")
		}

		def order = this.market.createActiveOrder(user, market, amount, price, type)

		[
			order: order
		]
	}

	@JsonBody
	@RequestParams(required = ["market", "orderKey"])
	def cancelOrder(Market market, String orderKey, User user)
	{
		this.market.cancelOrder(user, market, orderKey)

		[
			success: true
		]
	}
}