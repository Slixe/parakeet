package fr.slixe.exchange.structure;

import java.math.BigDecimal;

import com.arangodb.entity.DocumentField;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CompletedOrder {

	@DocumentField(DocumentField.Type.KEY)
	private String key;
	private Market market;
	private BigDecimal amount;
	private BigDecimal price;
	private OrderType type;
	@JsonIgnore
	private String userKey;
	private long completedAt;

	public CompletedOrder(Market market, BigDecimal amount, BigDecimal price, OrderType type, String userKey, long completedAt)
	{
		this.market = market;
		this.amount = amount;
		this.price = price;
		this.type = type;
		this.userKey = userKey;
		this.completedAt = completedAt;
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
		return type == OrderType.BUY ? amount.divide(price) : price.multiply(amount);
	}

	public OrderType getType()
	{
		return type;
	}

	public void setType(OrderType type)
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

	public long completedAt()
	{
		return completedAt;
	}

	public void setCompletedAt(long completedAt)
	{
		this.completedAt = completedAt;
	}

	public Currency getCurrency()
	{
		return type == OrderType.BUY ? market.getFirst() : market.getSecond();
	}
}