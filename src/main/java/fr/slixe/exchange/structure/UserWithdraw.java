package fr.slixe.exchange.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

public class UserWithdraw {

	@DocumentField(Type.KEY)
	private String key;
	private Map<Currency, Map<UUID, Withdraw>> map;

	public UserWithdraw()
	{
		this(new HashMap<>());
	}

	public UserWithdraw(Map<Currency, Map<UUID, Withdraw>> map)
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

	public Map<Currency, Map<UUID, Withdraw>> getMap()
	{
		return map;
	}

	public void setMap(Map<Currency, Map<UUID, Withdraw>> map)
	{
		this.map = map;
	}
}
