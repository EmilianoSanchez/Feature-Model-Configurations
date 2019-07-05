package com.emiliano.fmframework.operations;

import java.util.LinkedList;
import java.util.List;

import com.emiliano.fmframework.core.Configuration;
import com.emiliano.fmframework.core.FeatureModel;
import com.emiliano.fmframework.core.FeatureState;
import com.emiliano.fmframework.core.constraints.Clause;
import com.emiliano.fmframework.core.constraints.Constraint;
import com.emiliano.fmframework.core.constraints.treeConstraints.TreeConstraint;
import com.emiliano.fmframework.core.constraints.treeConstraints.TreeConstraint.TreeConstraintType;

public class ConfOperations {

	public static Configuration getPartialConfiguration(FeatureModel fmodel) {
		Configuration conf = new Configuration(fmodel);

		for (int i = 0; i < fmodel.getNumFeatures(); i++) {
			for (Clause clause : fmodel.getClauses(i)) {
				if (clause.literalIds.length == 1) {
					if (!ConfOperations.assignFeature(conf, i, clause.literalValues[0]))
						return null;
				}
			}
		}
		
		if (!ConfOperations.assignFeature(conf, 0, FeatureState.SELECTED))
			return null;
		return conf;
	}

	public static boolean assignFeature(Configuration conf, int featureId, boolean selected) {
		if (selected)
			return assignFeature(conf, featureId, FeatureState.SELECTED);
		else
			return assignFeature(conf, featureId, FeatureState.DESELECTED);
	}

	public static boolean assignFeature(Configuration conf, int featureId, FeatureState state) {

		if (conf.getFeatureState(featureId) == FeatureState.UNSELECTED) {
			conf.setFeatureState(featureId, state);

			FeatureModel model = conf.getModel();
			for (Clause clause : model.getClauses(featureId)) {
				int numUnselected = 0;
				int unselectedLiteral = 0;
				boolean unselectedLiteralValue = false;
				boolean atLeastOneLiteralTrue = false;

				for (int i = 0; i < clause.literalIds.length; i++) {
					if (conf.getFeatureState(clause.literalIds[i]) == FeatureState.UNSELECTED) {// isLiteralUndefinned
						numUnselected++;
						unselectedLiteral = clause.literalIds[i];
						unselectedLiteralValue = clause.literalValues[i];
					} else {
						if (conf.getFeatureState(clause.literalIds[i]).booleanValue() == clause.literalValues[i]) {// isLiteralTrue
							atLeastOneLiteralTrue = true;
							break;
						}
					}
				}

				if (atLeastOneLiteralTrue)
					continue;
				else {
					if (numUnselected == 0)
						return false;
					else
					// propagation
					if (numUnselected == 1 && !assignFeature(conf, unselectedLiteral,
							FeatureState.featureStateValue(unselectedLiteralValue)))
						return false;
				}
			}

			// Manage Cardinality-Based trees
			List<TreeConstraint> cardinalityTrees = new LinkedList<TreeConstraint>();
			TreeConstraint parentTree = model.getParentTree(featureId);
			if (parentTree != null && parentTree.getType() == TreeConstraintType.CARDINALITY_GROUP
					&& conf.getFeatureState(parentTree.getParent()) == FeatureState.SELECTED) {
				cardinalityTrees.add(parentTree);
			}
			if (state == FeatureState.SELECTED) {
				for (TreeConstraint childTree : model.getChildrenTrees(featureId)) {
					if (childTree.getType() == TreeConstraintType.CARDINALITY_GROUP) {
						cardinalityTrees.add(childTree);
					}
				}
			}
			for (TreeConstraint tree : cardinalityTrees) {
				int[] countSibling = new int[3];
				List<Integer> unselecteds = new LinkedList<>();
				for (int siblingId : tree.getChildren()) {
					FeatureState siblingState = conf.getFeatureState(siblingId);
					countSibling[siblingState.ordinal()]++;
					if (siblingState == FeatureState.UNSELECTED)
						unselecteds.add(siblingId);
				}

				if (countSibling[1] == tree.getMaxCardinality()) {
					for (int unselectedSibling : unselecteds) {
						if (!assignFeature(conf, unselectedSibling, FeatureState.DESELECTED))
							return false;
					}
				} else {
					if (countSibling[1] > tree.getMaxCardinality()
							|| countSibling[1] + countSibling[2] < tree.getMinCardinality()) {
						return false;
					} else {
						if (countSibling[1] + countSibling[2] == tree.getMinCardinality()) {
							for (int unselectedSibling : unselecteds) {
								if (!assignFeature(conf, unselectedSibling, FeatureState.SELECTED))
									return false;
							}
						}
					}
				}
			}

			return true;
		} else {
			if (conf.getFeatureState(featureId) == state)
				return true;
			else
				return false;
		}
	}

	// A more efficient version of assignFeature that consider CardinalityGroups
	// public static boolean assignFeatureV2(Configuration conf, int featureId,
	// boolean selected) {
	// if (selected)
	// return assignFeatureV2(conf, featureId, FeatureState.SELECTED);
	// else
	// return assignFeatureV2(conf, featureId, FeatureState.DESELECTED);
	// }
	//
	// public static boolean assignFeatureV2(Configuration conf, int featureId,
	// FeatureState state) {
	// //Copiar de CardinalityBasedConstraintPropagator
	// }

	public static boolean isValid(Configuration conf) {
		return isValid(conf, null);
	};

	public static boolean isValid(Configuration conf, Constraint iconstraint) {

		if (/* conf!=null && conf.getModel()!=null && */ conf.getModel().isSatisfied(conf)) {
			if (iconstraint != null) {
				if (iconstraint.isSatisfied(conf))
					return true;
				else
					return false;
			} else
				return true;
		} else
			return false;
	};

}
