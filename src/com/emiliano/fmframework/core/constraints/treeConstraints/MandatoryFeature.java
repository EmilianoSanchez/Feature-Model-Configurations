package com.emiliano.fmframework.core.constraints.treeConstraints;

import java.util.Set;

import com.emiliano.fmframework.core.constraints.Clause;

public class MandatoryFeature extends TreeConstraint {

	public MandatoryFeature(int parent, int child) {
		super(1, 1, parent, child);
	}

	@Override
	protected void generateClauses(Set<Clause> clauses) {
		Clause clause = new Clause(new int[] { this.children[0], this.parent }, new boolean[] { false, true });
		clauses.add(clause);

		clause = new Clause(new int[] { this.children[0], this.parent }, new boolean[] { true, false });
		clauses.add(clause);
	}

	@Override
	public TreeConstraintType getType() {
		return TreeConstraintType.MANDATORY;
	}

	public int getChild() {
		return this.children[0];
	}

	@Override
	public MandatoryFeature clone() {
		MandatoryFeature clone = new MandatoryFeature(this.parent, this.children[0]);
		return clone;
	}
}
