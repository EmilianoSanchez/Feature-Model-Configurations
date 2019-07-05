package com.emiliano.fmframework.core.constraints.globalConstraints;

import java.util.Map;

import com.emiliano.fmframework.core.Configuration;
import com.emiliano.fmframework.core.constraints.Constraint;

public interface GlobalConstraint extends Constraint {

	public Map.Entry<Integer, Boolean>[] constraintPropagation(Configuration conf);

	public GlobalConstraint clone();

}
