package fr.slixe.exchange.structure;

import java.math.BigDecimal;
import java.util.UUID;

public class Withdraw {

	private UUID uuid;
	private String txHash;
	private BigDecimal amount;
	private long timestamp;
	private Status status;
	
	public Withdraw() {}
	
	public Withdraw(UUID uuid, String txHash, BigDecimal amount, long timestamp, Status status)
	{
		this.uuid = uuid;
		this.txHash = txHash;
		this.amount = amount;
		this.timestamp = timestamp;
		this.status = status;
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public String getTxHash()
	{
		return txHash;
	}

	public void setTxHash(String txHash)
	{
		this.txHash = txHash;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}
}
