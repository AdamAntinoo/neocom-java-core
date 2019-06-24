package org.dimensinfin.eveonline.neocom.domain;

public interface IScheduler {
	boolean needsNetwork();
	boolean needsSDCard();
	void execute();
}
