package org.dimensinfin.eveonline.neocom.model;

import java.util.Objects;

import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.eveonline.neocom.database.EntityManager;
import org.dimensinfin.eveonline.neocom.database.ISDEDBHelper;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.interfaces.IDatabaseEntity;
import org.dimensinfin.eveonline.neocom.interfaces.IGlobalConnector;

/**
 * This utility class is responsible to connect the Model classes that require database access and that are
 * considered <b>Entities</b> to the <b>Global</b> data management structures but broking a direct compilation module
 * dependency that should result into a circular dependency or the complete integration of modules.
 * <p>
 * During initialization the Global structures should connect the SDE and NeoCom databases to this utility class so
 * all Entities can access the databases maintaining module isolation.
 *
 * @author Adam Antinoo
 */
public abstract class ANeoComEntity extends NeoComNode implements IDatabaseEntity {
	@Deprecated
	private static ISDEDBHelper SDEHelper;
	@Deprecated
	private static IGlobalConnector globalConnector;
	private static EntityManager entityManager;

	public static void connectEntityManager( final EntityManager newEntityManager ) {
		Objects.requireNonNull(newEntityManager);
		entityManager = newEntityManager;
	}

	protected EntityManager accessEntityManager() {
		Objects.requireNonNull(entityManager);
		return entityManager;
	}

	/**
	 * Required initialization step to connect the Model classes to the Global application connector source for all the
	 * external functionality implemented on the core Application.
	 *
	 * @param global connection to the Global connector.
	 */
	@Deprecated
	public static void connectGlobal( final IGlobalConnector global ) {
		globalConnector = global;
	}

	@Deprecated
	public static IGlobalConnector accessGlobal() throws NeoComRuntimeException {
		if (null != globalConnector) return globalConnector;
		else
			throw new NeoComRuntimeException("[ANeoComEntity.accessGlobal]> Global connector not connected to Model. Database " +
					                                 "disabled as other application functionality.");
	}

	/**
	 * Required initialization step to connect the Model classes to the SDE database helper.
	 *
	 * @param helper connection to the SDE database.
	 */
	@Deprecated
	public static void connectSDEHelper( final ISDEDBHelper helper ) {
		SDEHelper = helper;
	}

	@Deprecated
	public static ISDEDBHelper accessSDEDBHelper() throws NeoComRuntimeException {
		if (null != SDEHelper) return SDEHelper;
		else
			throw new NeoComRuntimeException("[ANeoComEntity.accessSDEDBHelper]> Database Helper not connected to Model. Database " +
					                                 "disabled.");
	}
}
