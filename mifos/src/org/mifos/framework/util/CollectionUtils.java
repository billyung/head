package org.mifos.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.framework.util.helpers.Predicate;

public class CollectionUtils {

	// Same as commons-collections but with generics
	public static <T> T find(Collection<T> collections, Predicate<T> predicate)
			throws Exception {
		if (collections != null && predicate != null) {
			for (T item : collections) {
				if (predicate.evaluate(item))
					return item;
			}
		}
		return null;
	}

	public static<T> List<T> asList(
			T element) {
		List<T> list = new ArrayList<T>();
		list.add(element);
		return list;
	}

	public static<T> Set<T> asSet(
			T element) {
		Set<T> collectionSheetsForMeetingDate = new HashSet<T>();
		collectionSheetsForMeetingDate.add(element);
		return collectionSheetsForMeetingDate;
	}
}
