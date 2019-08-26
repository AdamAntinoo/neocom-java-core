package org.dimensinfin.eveonline.neocom.test.support.converters;

public interface IConverter<F, T> {
	T convert(F input);
}
