package com.emiliano.fmframework.core.constraints.crossTreeConstraints;

import java.util.Set;

import com.emiliano.fmframework.core.constraints.Clause;
import com.emiliano.fmframework.core.constraints.crossTreeConstraints.BinaryConstraint.BinaryConstraintType;

public class MutualExclusion extends BinaryConstraint {

	public MutualExclusion(int leftFeature, int rightFeature) {
		super(leftFeature, rightFeature);
	}

	@Override
	protected void generateClauses(Set<Clause> clauses) {
		Clause clause1 = new Clause(new int[] { this.leftFeature, this.rightFeature }, new boolean[] { false, false });
		Clause clause2 = new Clause(new int[] { this.leftFeature, this.rightFeature }, new boolean[] { true, true });

		clauses.add(clause1);
		clauses.add(clause2);
	}

	@Override
	public String toString() {
		return "\"" + this.leftFeature + "\"<-/>\"" + this.rightFeature + "\"";
	}

	@Override
	public BinaryConstraintType getType() {
		return BinaryConstraintType.MUTUAL_EXCLUSION;
	}

	@Override
	public MutualExclusion clone() {
		MutualExclusion clone = new MutualExclusion(this.leftFeature, this.rightFeature);
		return clone;
	}
}
