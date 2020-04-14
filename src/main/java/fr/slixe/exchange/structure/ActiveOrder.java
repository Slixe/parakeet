package fr.slixe.exchange.structure;

import java.math.BigDecimal;

import com.arangodb.entity.DocumentField;

public class ActiveOrder {

	@DocumentField(DocumentField.Type.KEY)
	private String key;
	private Market market;
	private BigDecimal amount;
	private BigDecimal price;
	private Type type;
	private String userKey;
	private long createdAt;

	public ActiveOrder() {}

	public ActiveOrder(Market market, BigDecimal amount, BigDecimal price, Type type, String userKey, long createdAt)
	{
		this.market = market;
		this.amount = amount;
		this.price = price;
		this.type = type;
		this.userKey = userKey;
		this.createdAt = createdAt;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public Market getMarket()
	{
		return market;
	}

	public void setMarket(Market market)
	{
		this.market = market;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	public BigDecimal getPrice()
	{
		return price;
	}

	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}

	public BigDecimal getTotal()
	{
		return price.multiply(amount);
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public String getUserKey()
	{
		return userKey;
	}

	public void setUserKey(String userKey)
	{
		this.userKey = userKey;
	}

	public long createdAt()
	{
		return createdAt;
	}

	public void setCreatedAt(long createdAt)
	{
		this.createdAt = createdAt;
	}

	public Currency getCurrency()
	{
		return type == Type.BUY ? market.getFirst() : market.getSecond();
	}

	public static enum Type {
		BUY,
		SELL
	}
}
