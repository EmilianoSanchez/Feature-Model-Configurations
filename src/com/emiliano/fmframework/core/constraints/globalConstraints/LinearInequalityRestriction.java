package com.emiliano.fmframework.core.constraints.globalConstraints;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.emiliano.fmframework.core.Configuration;
import com.emiliano.fmframework.core.FeatureModel;

public class LinearInequalityRestriction implements GlobalConstraint {

	public double[] attributes;
	public double restrictionLimit;

	public LinearInequalityRestriction(double[] attributes, double restrictionLimit) {
		this.attributes = attributes;
		this.restrictionLimit = restrictionLimit;
	}

	@Override
	public boolean isSatisfied(Configuration configuration) {
		double sum = 0.0;
		for (int i = 0; i < configuration.getNumFeatures(); i++) {
			if (configuration.isFeatureSelected(i))
				sum += attributes[i];
		}
		if (sum <= restrictionLimit)
			return true;
		else
			return false;
	}

	@Override
	public Map.Entry<Integer, Boolean>[] constraintPropagation(Configuration configuration) {
		double sum = 0.0;
		Set<Integer> unselecteds = new TreeSet<Integer>();
		for (int i = 0; i < configuration.getNumFeatures(); i++) {
			switch (configuration.getFeatureState(i)) {
			case SELECTED:
				sum += attributes[i];
				break;
			case UNSELECTED:
				unselecteds.add(i);
				break;
			}
		}
		if (sum > restrictionLimit)
			return null;
		else {
			double resto = restrictionLimit - sum;

			for (Iterator<Integer> iter = unselecteds.iterator(); iter.hasNext();) {
				int unselected = iter.next();

				if (attributes[unselected] <= resto) {
					iter.remove();
				}
			}

			Map.Entry<Integer, Boolean>[] result = new Map.Entry[unselecteds.size()];
			int pos = 0;
			for (int unselected : unselecteds) {
				result[pos] = new AbstractMap.SimpleEntry(unselected, false);
				pos++;
			}
			return result;
		}

	}

	@Override
	public GlobalConstraint clone() {
		return new LinearInequalityRestriction(attributes, restrictionLimit);
	}

}
