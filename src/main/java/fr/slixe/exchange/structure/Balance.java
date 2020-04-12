package fr.slixe.exchange.structure;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

public class Balance {

	@DocumentField(Type.KEY)
	private String key;
	private Map<Currency, BigDecimal> funds;
	private Map<Currency, BigDecimal> frozenFunds;

	public Balance()
	{
		Map<Currency, BigDecimal> map = new HashMap<>();
		
		for (Currency currency : Currency.values())
		{
			map.put(currency, BigDecimal.ZERO);
		}

		this.funds = map;
		this.frozenFunds = map;
	}
	
	public Balance(Map<Currency, BigDecimal> funds, Map<Currency, BigDecimal> frozenFunds)
	{
		this.funds = funds;
		this.frozenFunds = frozenFunds;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	public boolean hasEnoughFunds(Currency currency, BigDecimal amount)
	{
		return funds.getOrDefault(currency, BigDecimal.ZERO).compareTo(amount) != -1;
	}

	public boolean hasEnoughFrozenFunds(Currency currency, BigDecimal amount)
	{
		return frozenFunds.getOrDefault(currency, BigDecimal.ZERO).compareTo(amount) != -1;
	}

	public BigDecimal getFunds(Currency currency)
	{
		return funds.getOrDefault(currency, BigDecimal.ZERO);
	}

	public BigDecimal getFrozenFunds(Currency currency)
	{
		return frozenFunds.getOrDefault(currency, BigDecimal.ZERO);
	}

	public void setFunds(Currency currency, BigDecimal amount)
	{
		funds.put(currency, amount);
	}

	public void setFrozenFunds(Currency currency, BigDecimal amount)
	{
		frozenFunds.put(currency, amount);
	}

	public Map<Currency, BigDecimal> getFunds()
	{
		return funds;
	}

	public Map<Currency, BigDecimal> getFrozenFunds()
	{
		return frozenFunds;
	}
}
