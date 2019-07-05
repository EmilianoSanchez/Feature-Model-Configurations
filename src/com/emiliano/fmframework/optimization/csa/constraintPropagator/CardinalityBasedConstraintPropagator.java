package com.emiliano.fmframework.optimization.csa.constraintPropagator;

import java.util.LinkedList;
import java.util.List;

import com.emiliano.fmframework.core.Configuration;
import com.emiliano.fmframework.core.FeatureModel;
import com.emiliano.fmframework.core.FeatureState;
import com.emiliano.fmframework.core.constraints.Clause;
import com.emiliano.fmframework.core.constraints.treeConstraints.TreeConstraint;
import com.emiliano.fmframework.core.constraints.treeConstraints.TreeConstraint.TreeConstraintType;

public class CardinalityBasedConstraintPropagator implements ConstraintPropagator{

	@Override
	public boolean assignFeature(Configuration conf, int featureId, FeatureState state) {
		
		if (conf.getFeatureState(featureId) == FeatureState.UNSELECTED) {
			conf.setFeatureState(featureId, state);

			FeatureModel model=conf.getModel();
			for (Clause clause : model.getClauses(featureId)) {
				int numUnselected = 0;
				int unselectedLiteral = 0;
				boolean unselectedLiteralValue=false;
				boolean atLeastOneLiteralTrue = false;

				for (int i=0;i<clause.literalIds.length;i++) {
					if (conf.getFeatureState(clause.literalIds[i]) == FeatureState.UNSELECTED) {// isLiteralUndefinned
						numUnselected++;
						unselectedLiteral = clause.literalIds[i];
						unselectedLiteralValue=clause.literalValues[i];
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
			
			//Manage Cardinality-Based trees
			List<TreeConstraint> cardinalityTrees=new LinkedList<TreeConstraint>();
			TreeConstraint parentTree=model.getParentTree(featureId);
			if(parentTree!=null && parentTree.getType()==TreeConstraintType.CARDINALITY_GROUP && conf.getFeatureState(parentTree.getParent())== FeatureState.SELECTED ){
				cardinalityTrees.add(parentTree);
			}
			if(state==FeatureState.SELECTED){
				for(TreeConstraint childTree:model.getChildrenTrees(featureId)){
					if(childTree.getType()==TreeConstraintType.CARDINALITY_GROUP){
						cardinalityTrees.add(childTree);
					}
				}
			}
			for(TreeConstraint tree:cardinalityTrees){
				int[] countSibling=new int[3];
				List<Integer> unselecteds=new LinkedList<>();
				for(int siblingId : tree.getChildren()){
					FeatureState siblingState=conf.getFeatureState(siblingId);
					countSibling[siblingState.ordinal()]++;
					if(siblingState==FeatureState.UNSELECTED)
						unselecteds.add(siblingId);
				}
				
				if(countSibling[1]==tree.getMaxCardinality()){
					for(int unselectedSibling:unselecteds){
						if (!assignFeature( conf,unselectedSibling,
								FeatureState.DESELECTED))
							return false;
					}
				}else{
					if(countSibling[1]>tree.getMaxCardinality() || countSibling[1]+countSibling[2]<tree.getMinCardinality()){
						return false;
					}else{
						if(countSibling[1]+countSibling[2]==tree.getMinCardinality()){
							for(int unselectedSibling:unselecteds){
								if (!assignFeature( conf,unselectedSibling,
										FeatureState.SELECTED))
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

}
