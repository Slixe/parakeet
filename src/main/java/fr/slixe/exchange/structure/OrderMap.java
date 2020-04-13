package fr.slixe.exchange.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;

public class OrderMap {

	@DocumentField(Type.KEY)
	private String key;
	private Map<Market, List<String>> completedOrders;
	private Map<Market, List<String>> activeOrders;
	
	public OrderMap()
	{
		this(new HashMap<>(), new HashMap<>());
	}

	public OrderMap(Map<Market, List<String>> completedOrders, Map<Market, List<String>> activeOrders)
	{
		this.completedOrders = completedOrders;
		this.activeOrders = activeOrders;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public Map<Market, List<String>> getActiveOrders()
	{
		return activeOrders;
	}

	public void setActiveOrders(Map<Market, List<String>> orders)
	{
		this.activeOrders = orders;
	}

	public boolean addActiveOrder(Market market, String orderKey)
	{
		if (!this.activeOrders.containsKey(market)) {
			this.activeOrders.put(market, new ArrayList<>());
		}

		return this.activeOrders.get(market).add(orderKey);
	}

	public boolean removeActiveOrder(Market market, String orderKey)
	{
		return this.activeOrders.get(market).remove(orderKey);
	}

	public Map<Market, List<String>> getCompletedOrders()
	{
		return completedOrders;
	}

	public void setCompletedOrders(Map<Market, List<String>> orders)
	{
		this.completedOrders = orders;
	}

	public boolean addCompletedOrder(Market market, String orderKey)
	{
		if (!this.completedOrders.containsKey(market)) {
			this.completedOrders.put(market, new ArrayList<>());
		}

		return this.completedOrders.get(market).add(orderKey);
	}

	public boolean removeCompletedOrder(Market market, String orderKey)
	{
		return this.completedOrders.get(market).remove(orderKey);
	}
}
