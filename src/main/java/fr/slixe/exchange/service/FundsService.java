package fr.slixe.exchange.service;

import java.math.BigDecimal;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import fr.slixe.exchange.structure.Currency;
import fr.slixe.exchange.structure.User;

@Singleton
public class FundsService {

	@Inject
	private ArangoDatabaseService db;

	public boolean hasEnoughFunds(User user, Currency currency, BigDecimal amount, boolean frozenFunds)
	{
		return db.getBalanceFor(user, currency, frozenFunds).compareTo(amount) != -1;
	}

	public BigDecimal addFunds(User user, Currency currency, BigDecimal amount, boolean frozenFunds)
	{
		BigDecimal currentBalance = db.getBalanceFor(user, currency, frozenFunds);
		BigDecimal newBalance = currentBalance.add(amount);

		db.updateBalance(user, currency, newBalance, frozenFunds);

		return newBalance;
	}

	public BigDecimal removeFunds(User user, Currency currency, BigDecimal amount, boolean frozenFunds)
	{
		BigDecimal currentBalance = db.getBalanceFor(user, currency, frozenFunds);
		BigDecimal newBalance = currentBalance.subtract(amount);

		db.updateBalance(user, currency, newBalance, frozenFunds);

		return newBalance;
	}

	public void freezeFunds(User user, Currency currency, BigDecimal amount)
	{
		removeFunds(user, currency, amount, false);
		addFunds(user, currency, amount, true);
	}

	public void unfreezeFunds(User user, Currency currency, BigDecimal amount)
	{
		removeFunds(user, currency, amount, true);
		addFunds(user, currency, amount, false);
	}
}
