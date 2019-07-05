package com.emiliano.fmframework.core.constraints.crossTreeConstraints;

import com.emiliano.fmframework.core.constraints.ClauseBasedConstraint;
import com.emiliano.fmframework.core.constraints.treeConstraints.TreeConstraint;

public abstract class BinaryConstraint extends ClauseBasedConstraint {

	protected int leftFeature, rightFeature;

	public BinaryConstraint(int leftFeature, int rightFeature) {
		this.leftFeature = leftFeature;
		this.rightFeature = rightFeature;
	}

	public abstract BinaryConstraintType getType();

	public static enum BinaryConstraintType {
		EXCLUDE, IMPLY, MUTUAL_EXCLUSION, MUTUAL_IMPLICATION
	}

	public int getLeftFeature() {
		return leftFeature;
	}

	public void setLeftFeature(int leftFeature) {
		this.leftFeature = leftFeature;
	}

	public int getRightFeature() {
		return rightFeature;
	}

	public void setRightFeature(int rightFeature) {
		this.rightFeature = rightFeature;
	}

}
