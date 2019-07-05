package com.emiliano.fmframework.core.constraints.treeConstraints;

import java.util.Set;

import com.emiliano.fmframework.core.constraints.Clause;

public class OptionalFeature extends TreeConstraint {

	public OptionalFeature(int parent, int child) {
		super(0, 1, parent, child);
	}

	@Override
	protected void generateClauses(Set<Clause> clauses) {
		Clause clause = new Clause(new int[] { this.children[0], this.parent }, new boolean[] { false, true });
		clauses.add(clause);

	}

	@Override
	public TreeConstraintType getType() {
		return TreeConstraintType.OPTIONAL;
	}

	public int getChild() {
		return this.children[0];
	}

	@Override
	public OptionalFeature clone() {
		OptionalFeature clone = new OptionalFeature(this.parent, this.children[0]);
		return clone;
	}
}
