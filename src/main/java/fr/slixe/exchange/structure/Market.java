package fr.slixe.exchange.structure;

public enum Market {

	BTC_DERO(Currency.BTC, Currency.DERO),
	;

	private Currency first;
	private Currency second;

	private Market(Currency first, Currency second)
	{
		this.first = first;
		this.second = second;
	}

	public Currency getFirst()
	{
		return first;
	}

	public Currency getSecond()
	{
		return second;
	}
}
