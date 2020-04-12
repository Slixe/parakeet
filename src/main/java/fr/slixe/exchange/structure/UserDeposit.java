package fr.slixe.exchange.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

public class UserDeposit {

	@DocumentField(Type.KEY)
	private String key;
	private Map<Currency, Map<UUID, Deposit>> map;

	public UserDeposit()
	{
		this(new HashMap<>());
	}

	public UserDeposit(Map<Currency, Map<UUID, Deposit>> map)
	{
		this.map = map;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	public Map<Currency, Map<UUID, Deposit>> getMap()
	{
		return map;
	}

	public void setMap(Map<Currency, Map<UUID, Deposit>> map)
	{
		this.map = map;
	}
}
