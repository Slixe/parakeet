package fr.slixe.exchange.structure;

import java.math.BigDecimal;
import java.util.UUID;

public class Deposit {

	private UUID uuid;
	private String txHash;
	private BigDecimal amount;
	private long timestamp;
	private long blockHeight;
	private byte confirmations;
	private Status status;
	
	public Deposit() {}
	
	public Deposit(UUID uuid, String txHash, BigDecimal amount, long timestamp, long blockHeight, byte confirmations, Status status)
	{
		this.uuid = uuid;
		this.txHash = txHash;
		this.amount = amount;
		this.timestamp = timestamp;
		this.blockHeight = blockHeight;
		this.confirmations = confirmations;
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

	public long getBlockHeight()
	{
		return blockHeight;
	}

	public void setBlockHeight(long blockHeight)
	{
		this.blockHeight = blockHeight;
	}

	public byte getConfirmations()
	{
		return confirmations;
	}

	public void setConfirmations(byte confirmations)
	{
		this.confirmations = confirmations;
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
