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
import fr.slixe.exchange.structure.Deposit;
import fr.slixe.exchange.structure.UserDeposit;

public class UserDepositAdapter implements VPackDeserializer<UserDeposit> {

	@Override
	public UserDeposit deserialize(VPackSlice parent, VPackSlice vpack, VPackDeserializationContext context) throws VPackException
	{
		Iterator<Entry<String, VPackSlice>> it = vpack.get("map").objectIterator();
		
		Map<Currency, Map<UUID, Deposit>> depositMap = new HashMap<>();
		
		while (it.hasNext()) {
			Entry<String, VPackSlice> e = it.next();
			Currency currency = Currency.valueOf(e.getKey());
			Map<UUID, Deposit> map = new HashMap<>();
			
			Iterator<Entry<String, VPackSlice>> it2 = e.getValue().objectIterator();

			while (it2.hasNext()) {
				Entry<String, VPackSlice> e2 = it2.next();
				UUID uuid = UUID.fromString(e2.getKey());
				Deposit deposit = context.deserialize(e2.getValue(), Deposit.class);
				map.put(uuid, deposit);
			}

			depositMap.put(currency, map);
		}

		return new UserDeposit(depositMap);
	}
}
