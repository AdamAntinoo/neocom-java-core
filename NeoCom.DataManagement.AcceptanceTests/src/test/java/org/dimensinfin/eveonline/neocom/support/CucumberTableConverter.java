package org.dimensinfin.eveonline.neocom.support;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CucumberTableConverter<T> implements Converter<List<Map<String, String>>, List<T>> {

	private static final String LIST_SEPARATOR = ",";

	@Override
	public List<T> convert( List<Map<String, String>> dataTable ) {
		return dataTable.stream()
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
				       Arrays.stream(value.split(LIST_SEPARATOR)).map(String::trim).collect(Collectors.toList()) : null;
	}
}
