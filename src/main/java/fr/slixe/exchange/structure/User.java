package fr.slixe.exchange.structure;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

public class User {

	@DocumentField(Type.KEY)
	private String key;
	private String balanceKey;
	private String addressesKey;
	private String withdrawKey;
	private String depositKey;
	private String username;
	private String email;
	private String hashedPassword;
	private long createdAt;

	public User() {}

	public User(String username, String email, String hashedPassword, long createdAt)
	{
		this.username = username;
		this.email = email;
		this.hashedPassword = hashedPassword;
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

	public String getBalanceKey()
	{
		return balanceKey;
	}

	public void setBalanceKey(String balanceKey)
	{
		this.balanceKey = balanceKey;
	}

	public String getAddressesKey()
	{
		return addressesKey;
	}

	public void setAddressesKey(String addressesKey)
	{
		this.addressesKey = addressesKey;
	}

	public String getWithdrawKey()
	{
		return withdrawKey;
	}

	public void setWithdrawKey(String withdrawKey)
	{
		this.withdrawKey = withdrawKey;
	}

	public String getDepositKey()
	{
		return depositKey;
	}

	public void setDepositKey(String depositKey)
	{
		this.depositKey = depositKey;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getHashedPassword()
	{
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword)
	{
		this.hashedPassword = hashedPassword;
	}
	
	public long createdAt()
	{
		return createdAt;
	}

	public void setCreatedAt(long createdAt)
	{
		this.createdAt = createdAt;
	}
}
