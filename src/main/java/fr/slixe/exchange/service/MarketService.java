package fr.slixe.exchange.service;

import java.math.BigDecimal;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import fr.slixe.exchange.http.controller.ServiceException;
import fr.slixe.exchange.structure.ActiveOrder;
import fr.slixe.exchange.structure.CompletedOrder;
import fr.slixe.exchange.structure.Currency;
import fr.slixe.exchange.structure.Market;
import fr.slixe.exchange.structure.OrderType;
import fr.slixe.exchange.structure.User;

@Singleton
public class MarketService {

	@Inject
	private ArangoDatabaseService db;

	@Inject
	private FundsService funds;

	private void exchange(ActiveOrder taker, ActiveOrder maker)
	{
		final User userTaker = db.getUserFromKey(taker.getUserKey());
		final User userMaker = db.getUserFromKey(maker.getUserKey());
		final Currency takerCurrency = taker.getCurrency();
		final Currency makerCurrency = maker.getCurrency();

		BigDecimal rest = BigDecimal.ZERO;
		BigDecimal quantity = BigDecimal.ZERO;

		switch (taker.getAmount().compareTo(maker.getTotal())) {
		case -1: // taker < maker
			rest = taker.getTotal(maker.getPrice()); 
			quantity = taker.getAmount();

			db.updateActiveOrder(maker.getKey(), "amount", maker.getAmount().subtract(taker.getTotal(maker.getPrice())));
			taker.setAmount(BigDecimal.ZERO);
			break;
		case 0: // taker == maker
			rest = taker.getTotal(maker.getPrice());
			quantity = taker.getAmount();

			taker.setAmount(BigDecimal.ZERO);
			maker.setAmount(BigDecimal.ZERO);
			db.removeActiveOrder(maker.getKey());
			db.removeActiveOrderFromMap(userMaker, maker);
			break;
		case 1: // taker > maker
			rest = maker.getAmount();//maker.getTotal();
			quantity = maker.getTotal();//maker.getAmount();

			taker.setAmount(taker.getAmount().subtract(maker.getTotal()));

			db.removeActiveOrder(maker.getKey());
			db.removeActiveOrderFromMap(userMaker, maker);
			break;
		}

		completeOrder(userTaker, maker.getMarket(), maker.getPrice(), quantity, taker.getType());
		completeOrder(userMaker, maker.getMarket(), maker.getPrice(), rest, maker.getType());

		funds.removeFunds(userTaker, takerCurrency, quantity, true);
		funds.removeFunds(userMaker, makerCurrency, rest, true);

		funds.addFunds(userTaker, makerCurrency, rest, false);
		funds.addFunds(userMaker, takerCurrency, quantity, false);
	}

	public ActiveOrder createActiveOrder(User user, Market market, BigDecimal amount, BigDecimal price, OrderType type) throws ServiceException
	{
		Currency currency = type == OrderType.BUY ? market.getFirst() : market.getSecond();

		if (!funds.hasEnoughFunds(user, currency, amount, false)) {
			throw new ServiceException("Not enough funds!");
		}

		funds.freezeFunds(user, currency, amount);

		ActiveOrder order = new ActiveOrder(market, amount, price, type, user.getKey(), System.currentTimeMillis());
		List<ActiveOrder> orders = db.getInverseActiveOrders(market, type, price);
		int i = 0;

		while (order.getAmount() != BigDecimal.ZERO && i < orders.size()) {
			exchange(order, orders.get(i));
			i++;
		}

		if (order.getAmount() != BigDecimal.ZERO)
		{
			order = db.addActiveOrder(order);
			db.addActiveOrderToMap(user, market, order);
		}

		return order;
	}

	public void cancelOrder(User user, Market market, String orderKey) throws ServiceException
	{
		if (!db.hasActiveOrder(user, market, orderKey)) {
			throw new ServiceException("This order doesn't exist!");
		}

		ActiveOrder ao = db.removeActiveOrder(orderKey);
		db.removeActiveOrderFromMap(user, ao);
		funds.unfreezeFunds(user, ao.getCurrency(), ao.getAmount());
	}

	public void completeOrder(User user, Market market, BigDecimal price, BigDecimal amount, OrderType type)
	{
		CompletedOrder co = new CompletedOrder(market, amount, price, type, user.getKey(), System.currentTimeMillis());
		co = db.addCompletedOrder(co);
		db.addCompletedOrderToMap(user, market, co);
	}
}
