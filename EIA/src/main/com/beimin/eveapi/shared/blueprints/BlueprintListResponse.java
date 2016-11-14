package com.beimin.eveapi.shared.blueprints;

import java.util.LinkedHashSet;
import java.util.Set;

import com.beimin.eveapi.core.ApiResponse;

public class BlueprintListResponse extends ApiResponse {
	private static final long													serialVersionUID	= 1L;
	private final Set<EveBlueprint<EveBlueprint<?>>>	blueprints				= new LinkedHashSet<EveBlueprint<EveBlueprint<?>>>();

	public void add(final EveBlueprint<EveBlueprint<?>> blueprint) {
		blueprints.add(blueprint);
	}

	public Set<EveBlueprint<EveBlueprint<?>>> getAll() {
		return blueprints;
	}
}