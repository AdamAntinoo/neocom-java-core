package org.dimensinfin.eveonline.neocom.test.support.converters;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CucumberTableConverter<T> implements IConverter<List<Map<String, String>>, List<T>> {

	private static final String LIST_SEPARATOR = ",";

	@Override
	public List<T> convert( List<Map<String, String>> dataTable ) {
		return Stream.of(dataTable)
				       .map(row -> convert(row))
				       .collect(Collectors.toList());
	}

	public abstract T convert( Map<String, String> cucumberRow );

	protected boolean containsAnyField( Set<String> keys, Map<String, String> cucumberRow ) {
		for (String key : keys) {
			if (StringUtils.isNotEmpty(cucumberRow.get(key))) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	protected List<String> getListFromCucumberValue( String value ) {
		return StringUtils.isNotEmpty(value) ?
				       Stream.of(value.split(LIST_SEPARATOR)).map(String::trim).collect(Collectors.toList()) : null;
	}
}
