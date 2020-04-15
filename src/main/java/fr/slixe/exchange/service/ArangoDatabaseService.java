package fr.slixe.exchange.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.model.DocumentCreateOptions;
import com.arangodb.model.DocumentDeleteOptions;
import com.arangodb.util.MapBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import fr.litarvan.paladin.PaladinConfig;
import fr.slixe.exchange.structure.ActiveOrder;
import fr.slixe.exchange.structure.Address;
import fr.slixe.exchange.structure.Balance;
import fr.slixe.exchange.structure.CompletedOrder;
import fr.slixe.exchange.structure.Currency;
import fr.slixe.exchange.structure.Deposit;
import fr.slixe.exchange.structure.Market;
import fr.slixe.exchange.structure.OrderMap;
import fr.slixe.exchange.structure.OrderType;
import fr.slixe.exchange.structure.User;
import fr.slixe.exchange.structure.UserAddress;
import fr.slixe.exchange.structure.UserDeposit;
import fr.slixe.exchange.structure.UserWithdraw;
import fr.slixe.exchange.structure.Withdraw;
import fr.slixe.exchange.structure.adapter.UserWithdrawAdapter;

@Singleton
public class ArangoDatabaseService {

	private static final Logger log = LoggerFactory.getLogger("ArangoDB Service");

	@Inject
	private PaladinConfig config;

	private ArangoDB arango;
	private ArangoDatabase db;
	private ArangoCollection users;
	private ArangoCollection funds;
	private ArangoCollection addresses;
	private ArangoCollection withdrawals;
	private ArangoCollection deposits;
	private ArangoCollection orders;
	private ArangoCollection activeOrders;
	private ArangoCollection completedOrders;

	public void start()
	{
		String host = config.get("arangodb-host", String.class);
		int port = config.get("arangodb-port", int.class);

		log.info("Connecting to ArangoDB at {}:{}...", host, port);

		this.arango = new ArangoDB.Builder()
				.host(host, port)
				.user(config.get("arangodb-user", String.class))
				.password(config.get("arangodb-password", String.class))
				.registerDeserializer(UserWithdraw.class, new UserWithdrawAdapter())
				.build();

		this.db = this.arango.db(config.get("arangodb-database", String.class));
		this.db.drop(); //DEBUG
		if (!this.db.exists()) {
			this.db.create();
		}

		this.users = this.db.collection("users");
		this.funds = this.db.collection("funds");
		this.addresses = this.db.collection("addresses");
		this.withdrawals = this.db.collection("withdrawals");
		this.deposits = this.db.collection("deposits");
		this.orders = this.db.collection("orders");
		this.activeOrders = this.db.collection("activeOrders");
		this.completedOrders = this.db.collection("completedOrders");

		if (!this.users.exists()) {
			this.users.create();
		}
		if (!this.funds.exists()) {
			this.funds.create();
		}
		if (!this.addresses.exists()) {
			this.addresses.create();
		}
		if (!this.withdrawals.exists()) {
			this.withdrawals.create();
		}
		if (!this.deposits.exists()) {
			this.deposits.create();
		}
		if (!this.orders.exists()) {
			this.orders.create();
		}
		if (!this.activeOrders.exists()) {
			this.activeOrders.create();
		}
		if (!this.completedOrders.exists()) {
			this.completedOrders.create();
		}

		log.info("Connected to ArangoDB");
	}

	public void stop()
	{
		this.arango.shutdown();
	}

	private <T> T first(String query, Class<T> type, Map<String, Object> vars)
	{
		ArangoCursor<T> cursor = db.query(query, vars, null, type);

		if (cursor.hasNext()) {
			return cursor.next();
		}

		return null;
	}

	private <T> T first(String query, Map<String, Object> vars, T def)
	{
		ArangoCursor<T> cursor = db.query(query, vars, null, (Class<T>) def.getClass());

		if (cursor.hasNext()) {
			return cursor.next();
		}

		return def;
	}

	private <T> List<T> all(String query, Class<T> type, Map<String, Object> vars)
	{
		return db.query(query, vars, null, type).asListRemaining();
	}

	@Deprecated
	private <T> T insertNew(ArangoCollection collection, T object)
	{
		return collection.insertDocument(object, new DocumentCreateOptions().returnNew(true)).getNew();
	}

	private <T> String getKeyFromInsert(ArangoCollection collection, T object)
	{
		return collection.insertDocument(object, new DocumentCreateOptions().returnNew(false).returnOld(false)).getKey();
	}

	// USER

	public User getUserFromKey(String userKey)
	{
		return this.users.getDocument(userKey, User.class);
	}

	public User getUserByEmail(String email)
	{
		String query = "FOR u IN users FILTER u.email == @email LIMIT 1 RETURN u";
		Map<String, Object> vars = new MapBuilder()
				.put("email", email)
				.get();

		return first(query, User.class, vars);
	}

	public User getUserByUsername(String username)
	{
		String query = "FOR u IN users FILTER u.username == @username LIMIT 1 RETURN u";
		Map<String, Object> vars = new MapBuilder()
				.put("username", username)
				.get();

		return first(query, User.class, vars);
	}

	public boolean usernameExist(String username)
	{
		String query = "FOR u IN users FILTER u.username == @username LIMIT 1 RETURN true";
		Map<String, Object> vars = new MapBuilder()
				.put("username", username)
				.get();

		return first(query, vars, false);
	}

	public boolean emailExist(String email)
	{
		String query = "FOR u IN users FILTER u.email == @email LIMIT 1 RETURN true";
		Map<String, Object> vars = new MapBuilder()
				.put("email", email)
				.get();

		return first(query, vars, false);
	}	

	public User createNewUser(String username, String email, String hashedPassword)
	{
		User user = new User(username, email, hashedPassword, System.currentTimeMillis());
		Balance balance = createBalance();
		UserAddress userAddress = createUserAddress();
		UserWithdraw userWithdraw = createUserWithdraw();
		UserDeposit userDeposit = createUserDeposit();
		OrderMap orderMap = createOrderMap();

		user.setBalanceKey(balance.getKey());
		user.setAddressesKey(userAddress.getKey());
		user.setWithdrawKey(userWithdraw.getKey());
		user.setDepositKey(userDeposit.getKey());
		user.setOrderMapKey(orderMap.getKey());

		String userKey = getKeyFromInsert(this.users, user);
		user.setKey(userKey);
		
		return user;
	}

	public void updateUser(User user)
	{
		this.users.updateDocument(user.getKey(), user);
	}

	public void updateUser(User user, String field, Object value)
	{
		String query = "UPDATE @key WITH { @field: @value } IN users";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getKey())
				.put("field", field)
				.put("value", value)
				.get();

		db.query(query, vars, null, null);
	}

	// BALANCE
	
	public Balance getUserBalance(User user)
	{
		return this.funds.getDocument(user.getBalanceKey(), Balance.class);
	}

	public BigDecimal getBalanceFor(User user, Currency currency, boolean frozenFunds)
	{
		String query = "RETURN DOCUMENT('funds', @key).@type.@currency";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getBalanceKey())
				.put("type", frozenFunds ? "frozenFunds" : "funds")
				.put("currency", currency)
				.get();

		return first(query, vars, BigDecimal.ZERO);
	}

	public Balance createBalance()
	{
		Balance balance = new Balance();
		balance.setKey(getKeyFromInsert(this.funds, balance));

		return balance;
	}
	
	public void updateBalance(Balance balance)
	{
		this.funds.updateDocument(balance.getKey(), balance);
	}

	public void updateBalance(User user, Currency currency, BigDecimal newAmount, boolean frozenFunds)
	{
		String query = "UPDATE @key WITH { @type: { @currency: @amount } } IN funds";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getBalanceKey())
				.put("type", frozenFunds ? "frozenFunds" : "funds")
				.put("currency", currency)
				.put("amount", newAmount)
				.get();

		db.query(query, vars, null, null);
	}

	// USER ADDRESSES

	public UserAddress getUserAddress(User user)
	{
		return this.addresses.getDocument(user.getAddressesKey(), UserAddress.class);
	}

	public UserAddress createUserAddress()
	{
		UserAddress userAddress = new UserAddress();
		userAddress.setKey(getKeyFromInsert(this.addresses, userAddress));

		return userAddress;
	}

	public void updateUserAddress(UserAddress userAddress)
	{
		this.addresses.updateDocument(userAddress.getKey(), userAddress);
	}

	public void updateUserAddress(User user, Currency currency, Address address)
	{
		String query = "UPDATE @key WITH { addresses: { @currency: @address } } IN addresses";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getAddressesKey())
				.put("currency", currency)
				.put("address", address)
				.get();

		db.query(query, vars, null, null);
	}

	// WITHDRAWALS

	/* Current format for UserWithdraw is
	 * map: {
	 * 	@currency: {
	 * 		@id: {
	 * 			...
	 * 		},
	 * 		...
  	 * 	}
  	 * }
	 */
	
	public UserWithdraw getUserWithdraw(User user)
	{
		return this.withdrawals.getDocument(user.getWithdrawKey(), UserWithdraw.class);
	}

	public List<Withdraw> getUserWithdrawCurrency(User user, Currency currency)
	{
		final List<Withdraw> list = new ArrayList<>();

		String query = "RETURN DOCUMENT('withdrawals', @key).map.@currency";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getWithdrawKey())
				.put("currency", currency)
				.get();
		
		ArangoCursor<Map> cursor = db.query(query, vars, null, Map.class);
		
		if (cursor.hasNext()) {
			Map<String, Withdraw> map = cursor.next();

			if (map != null) {
				list.addAll(map.values());
			}
		}

		return list;
	}

	public UserWithdraw createUserWithdraw()
	{
		UserWithdraw map = new UserWithdraw();
		map.setKey(getKeyFromInsert(this.withdrawals, map));

		return map;
	}

	public void updateUserWithdraw(UserWithdraw withdraw)
	{
		this.withdrawals.updateDocument(withdraw.getKey(), withdraw);
	}

	public void updateWithdraw(User user, Currency currency, Withdraw withdraw)
	{
		String query = "UPDATE @key WITH { map: { @currency: { @id: @withdraw } } } IN withdrawals";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getWithdrawKey())
				.put("currency", currency)
				.put("id", withdraw.getUUID())
				.put("withdraw", withdraw)
				.get();

		db.query(query, vars, null, null);
	}

	// DEPOSITS (same as withdrawals)

	public UserDeposit getUserDeposit(User user)
	{
		return this.deposits.getDocument(user.getDepositKey(), UserDeposit.class);
	}

	public List<Deposit> getUserDepositCurrency(User user, Currency currency)
	{
		final List<Deposit> list = new ArrayList<>();

		String query = "RETURN DOCUMENT('deposits', @key).map.@currency";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getDepositKey())
				.put("currency", currency)
				.get();
		
		ArangoCursor<Map> cursor = db.query(query, vars, null, Map.class);
		
		if (cursor.hasNext()) {
			Map<String, Deposit> map = cursor.next();

			list.addAll(map.values());
		}

		return list;
	}

	public UserDeposit createUserDeposit()
	{
		UserDeposit userDeposit = new UserDeposit();
		userDeposit.setKey(getKeyFromInsert(this.deposits, userDeposit));

		return userDeposit;
	}

	public void updateUserDeposit(UserDeposit deposit)
	{
		this.deposits.updateDocument(deposit.getKey(), deposit);
	}

	public void updateDeposit(User user, Currency currency, Deposit deposit)
	{
		String query = "UPDATE @key WITH { map: { @currency: { @id: @deposit } } } IN deposits";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getWithdrawKey())
				.put("currency", currency)
				.put("id", deposit.getUUID())
				.put("deposit", deposit)
				.get();

		db.query(query, vars, null, null);
	}

	// ORDERS

	/*

	activeOrders && completedOrders: { 
		BTC_DERO: ["order1", "order2", "order3"],
		BTC_ETH: ["order5", "order10"]
	}

	 */

	private OrderMap createOrderMap()
	{
		OrderMap orderMap = new OrderMap();
		orderMap.setKey(getKeyFromInsert(this.orders, orderMap));

		return orderMap;
	}

	public OrderMap getOrderMap(User user)
	{		
		return this.orders.getDocument(user.getOrderMapKey(), OrderMap.class);
	}

	public void addActiveOrderToMap(User user, Market market, ActiveOrder order)
	{
		String query = "LET array = DOCUMENT('orders', @key).activeOrders.@market "
				+ "UPDATE @key WITH { activeOrders: { @market: APPEND(array, [@orderKey]) } } IN orders";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getOrderMapKey())
				.put("market", market)
				.put("orderKey", order.getKey())
				.get();

		db.query(query, vars, null, null);
	}

	public void removeActiveOrderFromMap(User user, ActiveOrder order)
	{
		String query = "LET array = DOCUMENT('orders', @key).activeOrders.@market "
				+ "UPDATE @key WITH { activeOrders: { @market: REMOVE_VALUE(array, @orderKey) } } IN orders";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getOrderMapKey())
				.put("market", order.getMarket())
				.put("orderKey", order.getKey())
				.get();

		db.query(query, vars, null, null);
	}

	public ActiveOrder getActiveOrder(String orderKey)
	{
		return this.activeOrders.getDocument(orderKey, ActiveOrder.class);
	}

	public ActiveOrder addActiveOrder(ActiveOrder order)
	{
		order.setKey(getKeyFromInsert(this.activeOrders, order));

		return order;
	}

	public void updateActiveOrder(ActiveOrder ao)
	{
		this.activeOrders.updateDocument(ao.getKey(), ao);
	}

	public void updateActiveOrder(String orderKey, String field, Object value)
	{
		String query = "UPDATE @key WITH { @field: @value } IN activeOrders";
		Map<String, Object> vars = new MapBuilder()
				.put("key", orderKey)
				.put("field", field)
				.put("value", value)
				.get();

		db.query(query, vars, null, null);
	}

	public ActiveOrder removeActiveOrder(String orderKey)
	{
		return this.activeOrders.deleteDocument(orderKey, ActiveOrder.class, new DocumentDeleteOptions().returnOld(true)).getOld();
	}

	public ActiveOrder getActiveOrder(User user, String orderKey)
	{
		String query = "LET ok = POSITION(DOCUMENT('orders', @key).activeOrders.@market, @orderKey) "
				+ "FILTER ok RETURN DOCUMENT('activeOrders', @orderKey)";

		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getOrderMapKey())
				.put("orderKey", orderKey)
				.get();

		return first(query, ActiveOrder.class, vars);
	}

	public boolean hasActiveOrder(User user, Market market, String orderKey)
	{
		String query = "RETURN POSITION(DOCUMENT('orders', @key).activeOrders.@market, @orderKey)";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getOrderMapKey())
				.put("market", market)
				.put("orderKey", orderKey)
				.get();

		return first(query, vars, false);
	}

	public List<ActiveOrder> getActiveOrders(User user, Market market)
	{
		List<ActiveOrder> orders = new ArrayList<>();
		String query = "LET orders = DOCUMENT('orders', @key).activeOrders.@market "
				+ "FILTER orders != null FOR o IN orders RETURN DOCUMENT('activeOrders', o)";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getOrderMapKey())
				.put("market", market)
				.get();

		List<ActiveOrder> list = db.query(query, vars, null, ActiveOrder.class).asListRemaining();

		if (list != null) {
			orders.addAll(list);
		}

		return orders;
	}

	public List<ActiveOrder> getActiveOrders(User user)
	{
		List<ActiveOrder> orders = new ArrayList<>();
		String query = "LET markets = DOCUMENT('orders', @key).activeOrders "
				+ "FOR m IN VALUES(markets) FOR o IN m RETURN DOCUMENT('activeOrders', o)";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getOrderMapKey())
				.get();

		List<ActiveOrder> list = db.query(query, vars, null, ActiveOrder.class).asListRemaining();

		if (list != null) {
			orders.addAll(list);
		}

		return orders;
	}

	public List<ActiveOrder> getInverseActiveOrders(Market market, OrderType type, BigDecimal price)
	{
		List<ActiveOrder> orders = new ArrayList<>();
		String query = "FOR o IN activeOrders FILTER o.market == @market FILTER o.type != @type FILTER o.price "+ (type == OrderType.SELL ? ">=" : "<=") + " @price SORT o.createdAt RETURN o";
		Map<String, Object> vars = new MapBuilder()
				.put("market", market)
				.put("type", type)
				.put("price", price)
				.get();

		List<ActiveOrder> list = db.query(query, vars, null, ActiveOrder.class).asListRemaining();

		if (list != null) {
			orders.addAll(list);
		}

		return orders;
	}

	public CompletedOrder addCompletedOrder(CompletedOrder order)
	{
		order.setKey(getKeyFromInsert(this.completedOrders, order));

		return order;
	}

	public List<CompletedOrder> getCompletedOrders(User user, Market market)
	{
		List<CompletedOrder> orders = new ArrayList<>();
		String query = "LET orders = DOCUMENT('orders', @key).completedOrders.@market "
				+ "FILTER orders != null FOR o IN orders RETURN DOCUMENT('completedOrders', o)";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getOrderMapKey())
				.put("market", market)
				.get();

		List<CompletedOrder> list = db.query(query, vars, null, CompletedOrder.class).asListRemaining();

		if (list != null) {
			orders.addAll(list);
		}

		return orders;
	}

	public List<CompletedOrder> getCompletedOrders(User user)
	{
		List<CompletedOrder> orders = new ArrayList<>();
		String query = "LET markets = DOCUMENT('orders', @key).completedOrders "
				+ "FOR m IN VALUES(markets) FOR o IN m RETURN DOCUMENT('completedOrders', o)";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getOrderMapKey())
				.get();

		List<CompletedOrder> list = db.query(query, vars, null, CompletedOrder.class).asListRemaining();

		if (list != null) {
			orders.addAll(list);
		}

		return orders;
	}

	public void addCompletedOrderToMap(User user, Market market, CompletedOrder order)
	{
		String query = "LET array = DOCUMENT('orders', @key).completedOrders.@market "
				+ "UPDATE @key WITH { completedOrders: { @market: APPEND(array, [@orderKey]) } } IN orders";
		Map<String, Object> vars = new MapBuilder()
				.put("key", user.getOrderMapKey())
				.put("market", market)
				.put("orderKey", order.getKey())
				.get();

		db.query(query, vars, null, null);
	}
}
