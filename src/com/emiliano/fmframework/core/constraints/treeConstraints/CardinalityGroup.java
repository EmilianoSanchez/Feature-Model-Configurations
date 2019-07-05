package com.emiliano.fmframework.core.constraints.treeConstraints;

import java.util.Set;

import com.emiliano.fmframework.core.constraints.Clause;

public class CardinalityGroup extends TreeConstraint {

	public CardinalityGroup(int minCardinality, int maxCardinality, int parent, int... children) {
		super(minCardinality, maxCardinality, parent, children);
	}

	@Override
	public TreeConstraintType getType() {
		return TreeConstraintType.CARDINALITY_GROUP;
	}

	@Override
	protected void generateClauses(Set<Clause> clauses) {
		for (int child : this.children) {
			Clause clause = new Clause(new int[] { child, this.parent }, new boolean[] { false, true });
			clauses.add(clause);
		}
		// TODO Auto-generated method stub
	}

	@Override
	public CardinalityGroup clone() {
		CardinalityGroup clone = new CardinalityGroup(this.minCardinality, this.maxCardinality, this.parent,
				this.children.clone());
		return clone;
	}
}
