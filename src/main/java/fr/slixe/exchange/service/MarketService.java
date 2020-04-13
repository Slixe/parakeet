package fr.slixe.exchange.service;

import java.math.BigDecimal;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import fr.slixe.exchange.http.InvalidParameterException;
import fr.slixe.exchange.structure.ActiveOrder;
import fr.slixe.exchange.structure.ActiveOrder.Type;
import fr.slixe.exchange.structure.Currency;
import fr.slixe.exchange.structure.Market;
import fr.slixe.exchange.structure.User;

@Singleton
public class MarketService {

	@Inject
	private ArangoDatabaseService db;

	@Inject
	private FundsService funds;

	public ActiveOrder createActiveOrder(User user, Market market, BigDecimal amount, BigDecimal price, Type type) throws InvalidParameterException
	{
		Currency currency = type == Type.BUY ? market.getFirst() : market.getSecond();

		if (!funds.hasEnoughFunds(user, currency, amount, false)) {
			throw new InvalidParameterException("Not enough funds!");
		}

		funds.freezeFunds(user, currency, amount);

		ActiveOrder order = new ActiveOrder(market, amount, price, amount.multiply(price), type);
		order = db.addActiveOrder(order);

		db.addActiveOrderToMap(user, market, order);

		return order;
	}

	public boolean cancelOrder(String orderId)
	{
		return false;
	}
}
