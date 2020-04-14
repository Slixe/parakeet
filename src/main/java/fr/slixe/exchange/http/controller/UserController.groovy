package fr.slixe.exchange.http.controller;

import java.util.Map.Entry

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import fr.litarvan.paladin.http.Controller;
import fr.litarvan.paladin.http.routing.RequestParams
import fr.slixe.exchange.service.ArangoDatabaseService
import fr.slixe.exchange.service.FundsService
import fr.slixe.exchange.service.MarketService
import fr.slixe.exchange.structure.ActiveOrder.Type
import fr.slixe.exchange.structure.Balance
import fr.slixe.exchange.structure.Currency
import fr.slixe.exchange.structure.Deposit
import fr.slixe.exchange.structure.Market
import fr.slixe.exchange.structure.User
import fr.slixe.exchange.structure.UserAddress
import fr.slixe.exchange.structure.UserDeposit
import fr.slixe.exchange.structure.UserWithdraw
import fr.slixe.exchange.structure.Withdraw

public class UserController extends Controller {

	private static final Logger log = LoggerFactory.getLogger("HTTP User Controller")

	@Inject
	private ArangoDatabaseService db

	@Inject
	private FundsService funds
	
	@Inject
	private MarketService market
	
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
		funds.addFunds(user, Currency.DERO, new BigDecimal("500"), false)
		funds.addFunds(user, Currency.BTC, new BigDecimal("500"), false)
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

	@RequestParams(required = [], optional = ["fr.slixe.exchange.structure.Currency:currency"])
	def deposits(Optional<Currency> optCurrency, User user)
	{
		if (optCurrency.isPresent()) {
			Currency currency = optCurrency.get();
			List<Deposit> deposits = db.getUserDepositCurrency(user, currency)

			[
				deposits: deposits
			]
		} else {
			UserDeposit userDeposit = db.getUserDeposit(user)
			Map<Currency, List<Deposit>> response = new HashMap<>()
s
			for (Entry<Currency, Map<UUID, Deposit>> e : userDeposit.map.entrySet())
			{
				List<Deposit> list = new ArrayList<>()
				Map<UUID, Deposit> m = e.getValue()

				for (Deposit w : m.values())
				{
					list.add(w)
				}

				response.put(e.getKey(), list)
			}

			[
				deposits: response //userWithdraw.map
			]
		}
	}
}
