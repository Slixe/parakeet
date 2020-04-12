package fr.slixe.exchange.structure.adapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.arangodb.velocypack.VPackDeserializationContext;
import com.arangodb.velocypack.VPackDeserializer;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;

import fr.slixe.exchange.structure.Currency;
import fr.slixe.exchange.structure.UserWithdraw;
import fr.slixe.exchange.structure.Withdraw;

public class UserWithdrawAdapter implements VPackDeserializer<UserWithdraw> {

	@Override
	public UserWithdraw deserialize(VPackSlice parent, VPackSlice vpack, VPackDeserializationContext context) throws VPackException
	{
		Iterator<Entry<String, VPackSlice>> it = vpack.get("map").objectIterator();
		
		Map<Currency, Map<UUID, Withdraw>> withdrawMap = new HashMap<>();

		while (it.hasNext()) {
			Entry<String, VPackSlice> e = it.next();
			Currency currency = Currency.valueOf(e.getKey());
			Map<UUID, Withdraw> map = new HashMap<>();
			
			Iterator<Entry<String, VPackSlice>> it2 = e.getValue().objectIterator();

			while (it2.hasNext()) {
				Entry<String, VPackSlice> e2 = it2.next();
				UUID uuid = UUID.fromString(e2.getKey());
				Withdraw withdraw = context.deserialize(e2.getValue(), Withdraw.class);
				map.put(uuid, withdraw);
			}

			withdrawMap.put(currency, map);
		}
		
		return new UserWithdraw(withdrawMap);
	}
}
