package com.emiliano.fmframework.optimization.csa.constraintPropagator;

import com.emiliano.fmframework.core.Configuration;
import com.emiliano.fmframework.core.FeatureState;

public interface ConstraintPropagator {
	
	default public boolean assignFeature(Configuration conf, int featureId, boolean selected) {
		if (selected)
			return assignFeature(conf, featureId, FeatureState.SELECTED);
		else
			return assignFeature(conf, featureId, FeatureState.DESELECTED);
	}
	
	public boolean assignFeature(Configuration conf, int featureId, FeatureState state);
	
}
