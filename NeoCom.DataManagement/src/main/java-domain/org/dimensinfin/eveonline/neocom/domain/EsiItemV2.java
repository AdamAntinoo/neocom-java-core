package org.dimensinfin.eveonline.neocom.domain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.core.EEvents;
import org.dimensinfin.eveonline.neocom.core.EventEmitter;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOkDogmaAttributes;
import org.dimensinfin.eveonline.neocom.services.DataDownloaderService;

public class EsiItemV2 implements IEsiItemDownloadCallback, IItemFacet, Serializable {
	private static final long serialVersionUID = 9062458781449599566L;
	private static DataDownloaderService downloaderService;

	public static void injectDownloaderService( final DataDownloaderService newDownloaderService ) {
		Objects.requireNonNull(newDownloaderService);
		downloaderService = newDownloaderService;
	}

	private final int typeId;
	private GetUniverseTypesTypeIdOk item;
	private EventEmitter emitter = new EventEmitter();
	private double price = -1.0;

	// - C O N S T R U C T O R S
	public EsiItemV2( final int typeId ) {
		this.typeId = typeId;
	}

	public EsiItemV2( final GetUniverseTypesTypeIdOk item ) {
		this.item = item;
		this.typeId = this.item.getTypeId();
	}

	public String getName() {
		if (null == this.item) {
			downloaderService.accessEveItem(this, DataDownloaderService.EsiItemSections.ESIITEM_DATA);
			return "-";
		}
		return item.getName();
	}

	public double getVolume() {
		if (null == this.item) {
			downloaderService.accessEveItem(this, DataDownloaderService.EsiItemSections.ESIITEM_DATA);
			return 0.0;
		}
		return item.getVolume();
	}

	public double getPrice() {
		if (this.price < 0.0) {
			downloaderService.accessItemPrice(this, DataDownloaderService.EsiItemSections.ESIITEM_PRICE);
		}
		return this.price;
	}

	public String getURLForItem() {
		return "http://image.eveonline.com/Type/" + this.typeId + "_64.png";
	}

	public Integer getGroupId() {
		return this.item.getGroupId();
	}

	public List<GetUniverseTypesTypeIdOkDogmaAttributes> getDogmaAttributes() {
		return this.item.getDogmaAttributes();
	}

	public Float getCapacity() {
		return item.getCapacity();
	}

	// - D E L E G A T E   E M I T T E R
	public void addPropertyChangeListener( final PropertyChangeListener listener ) {
		this.emitter.addPropertyChangeListener(listener);
	}

	//	public void removePropertyChangeListener( final PropertyChangeListener listener ) {
	//		this.emitter.removePropertyChangeListener(listener);
	//	}

	// - I E S I I T E M D O W N L O A D C A L L B A C K
	@Override
	public int getTypeId() {
		return typeId;
	}

	@Override
	public void signalCompletion( final DataDownloaderService.EsiItemSections section, final Object completedData ) {
		switch (section) {
			case ESIITEM_DATA:
				this.item = ((GetUniverseTypesTypeIdOk) completedData);
				this.emitter.sendChangeEvent(new PropertyChangeEvent(this
						, EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name()
						, null, completedData));
				break;
			case ESIITEM_PRICE:
				this.price = ((Double) completedData);
				this.emitter.sendChangeEvent(new PropertyChangeEvent(this
						, EEvents.EVENTCONTENTS_ACTIONMODIFYDATA.name()
						, null, completedData));
				break;
		}
	}
}
