//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.core;

import java.util.Comparator;
// - IMPORT SECTION .........................................................................................
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.eveonline.neocom.enums.EComparatorField;
import org.dimensinfin.eveonline.neocom.industry.JobQueue;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.interfaces.IWeigthedNode;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
// - CLASS IMPLEMENTATION ...................................................................................
public class ComparatorFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("ComparatorFactory");
	public static Comparator<AbstractPropertyChanger> createComparator(EComparatorField code) {
		Comparator<AbstractPropertyChanger> comparator = new Comparator<AbstractPropertyChanger>() {
			public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
				return 0;
			}
		};
		switch (code) {
//			case NAME:
//				comparator = new Comparator<AbstractPropertyChanger>() {
//					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
//						String leftField = null;
//						String rightField = null;
//						if (left instanceof INamedPart) {
//							leftField = ((INamedPart) left).getName();
//						}
//						if (right instanceof INamedPart) {
//							rightField = ((INamedPart) right).getName();
//						}
//						if (left instanceof INamed) {
//							leftField = ((INamed) left).getOrderingName();
//						}
//						if (right instanceof INamed) {
//							rightField = ((INamed) right).getOrderingName();
//						}
//
//						if (null == leftField) return 1;
//						if (null == rightField) return -1;
//						if ("" == leftField) return 1;
//						if ("" == rightField) return -1;
//						return leftField.compareTo(rightField);
//					}
//				};
//				break;
			case ASSET_COUNT:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						long leftField = -1;
						long rightField = -1;
						if (left instanceof NeoComAsset) {
							final NeoComAsset intermediate = (NeoComAsset) left;
							leftField = intermediate.getQuantity();
						}

						if (right instanceof NeoComAsset) {
							final NeoComAsset intermediate = (NeoComAsset) right;
							rightField = intermediate.getQuantity();
						}
						if (leftField < rightField) return 1;
						if (leftField > rightField) return -1;
						return 0;
					}
				};
				break;
//			case AppWideConstants.comparators.COMPARATOR_ITEM_TYPE:
//				comparator = new Comparator<AbstractPropertyChanger>() {
//					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
//						int leftField = -1;
//						int rightField = -1;
//						// if (left instanceof BlueprintPart) leftField = 100;
//						if (left instanceof AssetPart) {
//							leftField = 0;
//						}
//						if (left instanceof ShipPart) {
//							leftField = 200;
//						}
//						if (left instanceof ContainerPart) {
//							leftField = -300;
//						}
//
//						// if (right instanceof BlueprintPart) rightField = 100;
//						if (right instanceof AssetPart) {
//							rightField = 0;
//						}
//						if (right instanceof ShipPart) {
//							rightField = 200;
//						}
//						if (right instanceof ContainerPart) {
//							rightField = -300;
//						}
//
//						if (leftField < rightField) return -1;
//						if (leftField > rightField) return 1;
//						return 0;
//					}
//				};
//				break;
			case RESOURCE_TYPE:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						int leftField = -1;
						int rightField = -1;
						if (left instanceof Resource) {
							final Resource resource =(Resource) left;
							leftField = 0;
							if (resource.getCategory().equalsIgnoreCase("Material")) {
								leftField = -300;
							}
							if (resource.getCategory().equalsIgnoreCase("Module")) {
								leftField = -200;
							}
							if (resource.getCategory().equalsIgnoreCase("Blueprint")) {
								leftField = -100;
							}
							if (resource.getName().contains("Datacore")) {
								leftField = 100;
							}
						}

						if (right instanceof Resource) {
							final Resource resource = (Resource) right;
							rightField = 0;
							if (resource.getCategory().equalsIgnoreCase("Material")) {
								rightField = -300;
							}
							if (resource.getCategory().equalsIgnoreCase("Module")) {
								rightField = -200;
							}
							if (resource.getCategory().equalsIgnoreCase("Blueprint")) {
								rightField = -100;
							}
							if (resource.getName().contains("Datacore")) {
								rightField = 100;
							}
						}

						if (leftField < rightField) return -1;
						if (leftField > rightField) return 1;
						return 0;
					}
				};
				break;
//			case AppWideConstants.comparators.COMPARATOR_APIID_ASC:
//				comparator = new Comparator<AbstractPropertyChanger>() {
//					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
//						long leftField = -1;
//						long rightField = -1;
//						if (left instanceof APIKeyPart) {
//							final APIKey intermediate = ((APIKeyPart) left).getCastedModel();
//							leftField = intermediate.getKeyID();
//						}
//
//						if (right instanceof APIKeyPart) {
//							final APIKey intermediate = ((APIKeyPart) right).getCastedModel();
//							rightField = intermediate.getKeyID();
//						}
//						if (leftField < rightField) return -1;
//						if (leftField > rightField) return 1;
//						return 0;
//					}
//				};
//				break;
//			case AppWideConstants.comparators.COMPARATOR_APIID_DESC:
//				comparator = new Comparator<AbstractPropertyChanger>() {
//					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
//						long leftField = -1;
//						long rightField = -1;
//						if (left instanceof APIKeyPart) {
//							final APIKey intermediate = ((APIKeyPart) left).getCastedModel();
//							leftField = intermediate.getKeyID();
//						}
//
//						if (right instanceof APIKeyPart) {
//							final APIKey intermediate = ((APIKeyPart) right).getCastedModel();
//							rightField = intermediate.getKeyID();
//						}
//						if (leftField > rightField) return -1;
//						if (leftField < rightField) return 1;
//						return 0;
//					}
//				};
//				break;
//			case AppWideConstants.comparators.COMPARATOR_REQUEST_PRIORITY:
//				comparator = new Comparator<AbstractPropertyChanger>() {
//					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
//						long leftField = -1;
//						long rightField = -1;
//						if (left instanceof PendingRequestEntry) {
//							final PendingRequestEntry intermediate = (PendingRequestEntry) left;
//							leftField = intermediate.getPriority();
//						}
//
//						if (right instanceof PendingRequestEntry) {
//							final PendingRequestEntry intermediate = (PendingRequestEntry) right;
//							rightField = intermediate.getPriority();
//						}
//						if (leftField < rightField) return -1;
//						if (leftField > rightField) return 1;
//						return 0;
//					}
//				};
//				break;
//			case AppWideConstants.comparators.COMPARATOR_PRIORITY:
//				comparator = new Comparator<AbstractPropertyChanger>() {
//					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
//						int leftField = -1;
//						int rightField = -1;
//						if (left instanceof PendingRequestEntry) {
//							final PendingRequestEntry intermediate = (PendingRequestEntry) left;
//							leftField = intermediate.getPriority();
//						}
//
//						if (right instanceof PendingRequestEntry) {
//							final PendingRequestEntry intermediate = (PendingRequestEntry) right;
//							rightField = intermediate.getPriority();
//						}
//						if (leftField < rightField) return 1;
//						if (leftField > rightField) return -1;
//						return 0;
//					}
//				};
//				break;
			case WEIGHT:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						int leftField = -1;
						int rightField = -1;
						if (left instanceof IWeigthedNode) {
							leftField = ((IWeigthedNode) left).getWeight();
						}
						if (right instanceof IWeigthedNode) {
							rightField = ((IWeigthedNode) right).getWeight();
						}
						if (leftField < rightField) return -1;
						if (leftField > rightField) return 1;
						return 0;
					}
				};
				break;
			case TIMEPENDING:
				comparator = new Comparator<AbstractPropertyChanger>() {
					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
						int leftField = -1;
						int rightField = -1;
						if (left instanceof JobQueue) {
							leftField = ((JobQueue) left).getTimeUsed();
						}
						if (right instanceof JobQueue) {
							rightField = ((JobQueue) right).getTimeUsed();
						}

						if (leftField > rightField) return 1;
						if (leftField < rightField) return -1;
						return 0;
					}
				};
				break;
//			case AppWideConstants.comparators.COMPARATOR_CARD_RATIO:
//				comparator = new Comparator<AbstractPropertyChanger>() {
//					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
//						double leftField = 0.0;
//						double rightField = 0.0;
//						// if (left instanceof ModulePart) {
//						// ModuleCard intermediate = ((ModulePart)
//						// left).getCastedModel();
//						// leftField = intermediate.getModuleIndex();
//						// }
//						if (left instanceof BlueprintPart) {
//							leftField = ((BlueprintPart) left).getProfitIndex();
//						}
//
//						// if (right instanceof ModulePart) {
//						// ModuleCard intermediate = ((ModulePart)
//						// right).getCastedModel();
//						// rightField = intermediate.getModuleIndex();
//						// }
//						if (right instanceof BlueprintPart) {
//							rightField = ((BlueprintPart) right).getProfitIndex();
//						}
//
//						if (leftField > rightField)
//							return -1;
//						else if (leftField == rightField) return 0;
//						return 1;
//					}
//				};
//				break;
//			case AppWideConstants.comparators.COMPARATOR_NEWESTDATESORT:
//				comparator = new Comparator<AbstractPropertyChanger>() {
//					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
//						DateTime leftField = new DateTime(DateTimeZone.UTC);
//						DateTime rightField = new DateTime(DateTimeZone.UTC);
//						if (left instanceof IDateTimeComparator) {
//							leftField = ((IDateTimeComparator) left).getComparableDate();
//						}
//						if (right instanceof IDateTimeComparator) {
//							rightField = ((IDateTimeComparator) right).getComparableDate();
//						}
//
//						if (leftField.isAfter(rightField))
//							return -1;
//						else
//							return 1;
//					}
//				};
//				break;
//			case AppWideConstants.comparators.COMPARATOR_OLDESTDATESORT:
//				comparator = new Comparator<AbstractPropertyChanger>() {
//					public int compare(final AbstractPropertyChanger left, final AbstractPropertyChanger right) {
//						DateTime leftField = new DateTime(DateTimeZone.UTC);
//						DateTime rightField = new DateTime(DateTimeZone.UTC);
//						if (left instanceof IDateTimeComparator) {
//							leftField = ((IDateTimeComparator) left).getComparableDate();
//						}
//						if (right instanceof IDateTimeComparator) {
//							rightField = ((IDateTimeComparator) right).getComparableDate();
//						}
//
//						if (leftField.isAfter(rightField))
//							return 1;
//						else
//							return -1;
//					}
//				};
//				break;
		}
		return comparator;
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ComparatorFactory() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
