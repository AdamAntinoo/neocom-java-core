package org.dimensinfin.eveonline.neocom.support.adapters;

@FunctionalInterface
public interface Converter<S, T> {
	T convert( S source );
}
