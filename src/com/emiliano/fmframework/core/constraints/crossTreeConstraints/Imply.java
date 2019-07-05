package com.emiliano.fmframework.core.constraints.crossTreeConstraints;

import java.util.Set;

import com.emiliano.fmframework.core.constraints.Clause;
import com.emiliano.fmframework.core.constraints.crossTreeConstraints.BinaryConstraint.BinaryConstraintType;

public class Imply extends BinaryConstraint {

	public Imply(int leftFeature, int rightFeature) {
		super(leftFeature, rightFeature);
	}

	@Override
	protected void generateClauses(Set<Clause> clauses) {
		Clause clause = new Clause(new int[] { this.leftFeature, this.rightFeature }, new boolean[] { false, true });
		clauses.add(clause);
	}

	@Override
	public String toString() {
		return "\"" + this.leftFeature + "\"->\"" + this.rightFeature + "\"";
	}

	@Override
	public BinaryConstraintType getType() {
		return BinaryConstraintType.IMPLY;
	}

	@Override
	public Imply clone() {
		Imply clone = new Imply(this.leftFeature, this.rightFeature);
		return clone;
	}
}
