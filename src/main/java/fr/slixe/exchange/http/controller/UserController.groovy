package fr.slixe.exchange.http.controller;

import java.util.Map.Entry

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import fr.litarvan.paladin.http.Controller;
import fr.litarvan.paladin.http.routing.RequestParams
import fr.slixe.exchange.service.ArangoDatabaseService
import fr.slixe.exchange.structure.Balance
import fr.slixe.exchange.structure.Currency
import fr.slixe.exchange.structure.Status
import fr.slixe.exchange.structure.User
import fr.slixe.exchange.structure.UserAddress
import fr.slixe.exchange.structure.UserWithdraw
import fr.slixe.exchange.structure.Withdraw

public class UserController extends Controller {

	private static final Logger log = LoggerFactory.getLogger("HTTP User Controller")

	@Inject
	private ArangoDatabaseService db

	def balances(User user)
	{
		Balance balance = db.getUserBalance(user)
		
		[
			balances: [
				funds: balance.funds,
				frozenFunds: balance.frozenFunds
			]
		]
	}

	def debug(User user)
	{
		db.updateWithdraw(user, Currency.BTC, new Withdraw(UUID.randomUUID(), "Hash", new BigDecimal("50"), System.currentTimeMillis(), Status.SUCCESS))
		db.updateWithdraw(user, Currency.BTC, new Withdraw(UUID.randomUUID(), "H4sh", new BigDecimal("10"), System.currentTimeMillis(), Status.PENDING))
		db.updateWithdraw(user, Currency.DERO, new Withdraw(UUID.randomUUID(), "H4sh", new BigDecimal("5000"), System.currentTimeMillis(), Status.PENDING))

		
		[
			message: "OK"
		]
	}

	def info(User user)
	{
		[
			username: user.username,
			email: user.email,
			createdAt: user.createdAt
		]
	}

	def addresses(User user)
	{
		UserAddress ua = db.getUserAddress(user)

		[
			addresses: ua.addresses
		]
	}

	@RequestParams(required = [], optional = ["fr.slixe.exchange.structure.Currency:currency"])
	def withdrawals(Optional<Currency> optCurrency, User user)
	{
		if (optCurrency.isPresent()) {
			Currency currency = optCurrency.get();
			List<Withdraw> withdrawals = db.getUserWithdrawCurrency(user, currency)

			[
				withdrawals: withdrawals
			]
		} else {
			UserWithdraw userWithdraw = db.getUserWithdraw(user)
			Map<Currency, List<Withdraw>> response = new HashMap<>()

			for (Entry<Currency, Map<UUID, Withdraw>> e : userWithdraw.map.entrySet())
			{
				List<Withdraw> list = new ArrayList<>()
				Map<UUID, Withdraw> m = e.getValue()

				for (Withdraw w : m.values())
				{
					list.add(w)
				}

				response.put(e.getKey(), list)	
			}

			[
				withdrawals: response //userWithdraw.map
			]
		}
	}
}
