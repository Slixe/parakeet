package fr.slixe.exchange.structure;

import java.util.HashMap;
import java.util.Map;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

public class UserAddress {

	@DocumentField(Type.KEY)
	private String key;
	private Map<Currency, Address> addresses;
	
	public UserAddress()
	{
		this.addresses = new HashMap<>();
	}
	
	public UserAddress(Map<Currency, Address> addresses)
	{
		this.addresses = addresses;
	}

	public void addAddress(Currency currency, Address address)
	{
		this.addresses.put(currency, address);
	}

	public Address removeAddress(Currency currency)
	{
		return addresses.remove(currency);
	}

	public Address getAddress(Currency currency)
	{
		return addresses.get(currency);
	}

	public Map<Currency, Address> getAddresses()
	{
		return addresses;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}
}
