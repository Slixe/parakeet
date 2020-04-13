package fr.slixe.exchange.structure;

import java.math.BigDecimal;

import com.arangodb.entity.DocumentField;

public class ActiveOrder {

	@DocumentField(DocumentField.Type.KEY)
	private String key;
	private Market market;
	private BigDecimal amount; // 15 DERO
	private BigDecimal price; // 0.5 BTC per DERO
	private BigDecimal total; // 0.5 * 15 = 7.5
	private Type type;

	public ActiveOrder() {}

	public ActiveOrder(Market market, BigDecimal amount, BigDecimal price, BigDecimal total, Type type)
	{
		this.market = market;
		this.amount = amount;
		this.price = price;
		this.total = total;
		this.type = type;
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
		return total;
	}

	public void setTotal(BigDecimal total)
	{
		this.total = total;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public static enum Type {
		BUY,
		SELL
	}
}
