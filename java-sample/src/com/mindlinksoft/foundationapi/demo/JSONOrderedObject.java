package com.mindlinksoft.foundationapi.demo;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Extension to {@link JSONObject} that remembers the order of inserts.
 * 
 * Note: MindLink API is built on top of .NET WCF services. WCF requires
 * that data transfer objects (DTOs) have their '__type' identifier
 * as the first property in the JSON object for successful deserialization
 * to occur. JSONObject is backed by a HashMap which makes no guarantee
 * of the insertion order when iterating over its items. This type was designed to override
 * the behaviour of the iterator by defining the set of keys as a LinkedHashSet instead,
 * which does preserve insertion order.
 */
@SuppressWarnings("rawtypes")
class JSONOrderedObject extends JSONObject {

	private Set<String> keys = new LinkedHashSet<String>();

	@Override
	public  JSONObject put(String key, Object value) throws JSONException {
		this.keys.add(key);
		return super.put(key, value);
	}

	@Override
	public  Iterator keys() {
		return this.keys.iterator();
	}

}