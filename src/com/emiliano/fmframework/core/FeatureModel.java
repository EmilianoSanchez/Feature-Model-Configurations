package com.emiliano.fmframework.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;

import com.emiliano.fmframework.core.constraints.Clause;
import com.emiliano.fmframework.core.constraints.ClauseBasedConstraint;
import com.emiliano.fmframework.core.constraints.Constraint;
import com.emiliano.fmframework.core.constraints.globalConstraints.GlobalConstraint;
import com.emiliano.fmframework.core.constraints.treeConstraints.AlternativeGroup;
import com.emiliano.fmframework.core.constraints.treeConstraints.CardinalityGroup;
import com.emiliano.fmframework.core.constraints.treeConstraints.MandatoryFeature;
import com.emiliano.fmframework.core.constraints.treeConstraints.OptionalFeature;
import com.emiliano.fmframework.core.constraints.treeConstraints.OrGroup;
import com.emiliano.fmframework.core.constraints.treeConstraints.TreeConstraint;
import com.emiliano.fmframework.core.constraints.treeConstraints.TreeConstraint.TreeConstraintType;

public class FeatureModel extends ClauseBasedConstraint implements Cloneable, Serializable {

	public static final int ROOT_ID = 0;

	private static class FMNode implements Serializable{
		Feature feature;
		TreeConstraint parentTree;
		Vector<TreeConstraint> childrenTrees;
		Vector<ClauseBasedConstraint> involvedCrossTreeConstraints;
		Set<Clause> involvedClauses;

		FMNode(Feature feature) {
			this.feature = feature;
			this.childrenTrees = new Vector<TreeConstraint>();
			this.involvedCrossTreeConstraints = new Vector<ClauseBasedConstraint>();
			this.involvedClauses = new HashSet<Clause>();
		}
	}

	private String name;
	private FMNode[] features;
	private Vector<TreeConstraint> treeConstraints;
	private Vector<ClauseBasedConstraint> crossTreeConstraints;
	private Vector<GlobalConstraint> globalConstraints;

	public FeatureModel(String name) {
		this(name,0);
	}
	
	public FeatureModel() {
		this("",0);
	}
	
	public FeatureModel(int numFeatures) {
		this("",numFeatures);
	}
	
	public FeatureModel(Feature[] features) {
		this(features.length);
		for(int i=0;i<features.length;i++)
			this.addFeature(i, features[i]);
	}
	
	public FeatureModel(String name,int numFeatures) {
		this.name=name;
		this.features = new FMNode[numFeatures];
		this.treeConstraints = new Vector<TreeConstraint>();
		this.crossTreeConstraints = new Vector<ClauseBasedConstraint>();
		this.globalConstraints = new Vector<GlobalConstraint>();
	}

	public String getName() {
		return name;
	}

	public Feature getRoot() {
		return features[0].feature;
//		if (features.length > 0)
//			return features[0].feature;
//		else
//			return null;
	}

	public Feature getFeature(int featureId) {
		return this.features[featureId].feature;
//		FMNode node = this.features[featureId];
//		if (node == null)
//			return null;
//		else
//			return node.feature;
	}

	public int getNumFeatures() {
		return features.length;
	}

	public Feature addFeature(int featureId, Feature feature) {
		if (this.features.length <= featureId) {
			FMNode[] extendedFeatures=new FMNode[featureId+1];
			System.arraycopy(this.features, 0, extendedFeatures, 0, this.features.length);
			this.features=extendedFeatures;
		}
		
		FMNode previous = this.features[featureId];
		this.features[featureId] = new FMNode(feature);
		if (previous != null)
				return previous.feature;
		return null;
	}

	public boolean addCrossTreeConstraint(ClauseBasedConstraint constraint) {

		for (int featureId : constraint.getInvolvedFeatures())
			if (this.features[featureId] == null)
				return false;

		for (int featureId : constraint.getInvolvedFeatures())
			this.features[featureId].involvedCrossTreeConstraints.add(constraint);

		this.crossTreeConstraints.add(constraint);

		for (Clause clause : constraint.getClauses())
			this.addClause(clause);

		return true;
	}
	
	public boolean addGlobalConstraint(GlobalConstraint constraint) {
		this.globalConstraints.add(constraint);
		return true;
	}

	public boolean addTreeConstraint(TreeConstraint treeConstraint) {
		for (int featureId : treeConstraint.getInvolvedFeatures())
			if (this.features[featureId] == null)
				return false;
		for (int featureId : treeConstraint.getChildren())
			if (this.features[featureId].parentTree != null)
				return false;

		this.features[treeConstraint.getParent()].childrenTrees.add(treeConstraint);
		for (int featureId : treeConstraint.getChildren())
			this.features[featureId].parentTree = treeConstraint;
		this.treeConstraints.add(treeConstraint);

		for (Clause clause : treeConstraint.getClauses())
			this.addClause(clause);

		return true;
	}

	public boolean hasChildren(int featureId) {
		Vector<TreeConstraint> childrenTrees = getChildrenTrees(featureId);
		if (childrenTrees == null || childrenTrees.size() == 0)
			return false;
		else
			return true;
	}

	public Vector<TreeConstraint> getChildrenTrees(int featureId) {
		return this.features[featureId].childrenTrees;
//		FMNode node = this.features[featureId];
//		if (node == null)
//			return null;
//		else
//			return node.childrenTrees;
	}

	public int[] getChildren(int featureId) {
		Vector<TreeConstraint> childrenTrees = this.getChildrenTrees(featureId);
		if (childrenTrees == null)
			return null;
		else {
			int[] children = new int[0];
			for (TreeConstraint treeConstraint : childrenTrees)
				children = ArrayUtils.addAll(children, treeConstraint.getChildren());
			return children;
		}
	}

	public TreeConstraint getParentTree(int featureId) {
		return this.features[featureId].parentTree;
//		FMNode node = this.features[featureId];
//		if (node == null)
//			return null;
//		else
//			return node.parentTree;
	}

	public int getParent(int featureId) {
		TreeConstraint parentTree = this.getParentTree(featureId);
		if (parentTree == null)
			return ROOT_ID;
		else {
			return parentTree.getParent();
		}
	}

	public Vector<TreeConstraint> getTreeConstraints() {
		return this.treeConstraints;
	}

	public Vector<ClauseBasedConstraint> getCrossTreeConstraints() {
		return this.crossTreeConstraints;
	}
	
	public Vector<ClauseBasedConstraint> getCrossTreeConstraints(int featureId) {
		return this.features[featureId].involvedCrossTreeConstraints;
//		FMNode node = this.features[featureId];
//		if (node != null)
//			return node.involvedCrossTreeConstraints;
//		else
//			return null;
	}
	
	public Vector<GlobalConstraint>  getGlobalConstraints() {
		return this.globalConstraints;
	}

	public Feature[] getFeatures() {
		Feature[] result = new Feature[this.features.length];
		for (int i = 0; i < features.length; i++)
			result[i] = this.features[i].feature;
		return result;
	}

	public Set<Clause> getClauses(int featureId) {
		return this.features[featureId].involvedClauses;
//		FMNode node = this.features[featureId];
//		if (node != null)
//			return node.involvedClauses;
//		else
//			return null;
	}
	
	public Set<Clause> getClauses() {
		HashSet<Clause> clauses=new HashSet<Clause>();
		for(int i=0;i<this.features.length;i++){
			FMNode node = this.features[i];
			if (node != null)
				clauses.addAll(node.involvedClauses);
		}
		return clauses;
	}

	private void addClause(Clause clause) {
		for (int involvedFeature : clause.getInvolvedFeatures()) {
			FMNode node = this.features[involvedFeature];
			if (node != null)
				node.involvedClauses.add(clause);
		}
	}
	
	public boolean isRoot(int featureId){
		return featureId==ROOT_ID;
	}
	
	public boolean isLeaf(int featureId){
		return this.features[featureId].childrenTrees.size()==0;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("FM("+this.name+";");
		this.toString(ROOT_ID, builder);

		for (ClauseBasedConstraint constraint : this.getCrossTreeConstraints()) {
			builder.append(constraint.toString() + ";");
		}
		for (Feature feature : this.getFeatures()) {
			builder.append(feature.toString() + ";");
		}
		builder.append(')');
		return builder.toString();
	}

	private void toString(int featureId, StringBuilder builder) {
		builder.append("\"" + featureId + "\"");
		if (this.hasChildren(featureId)) {
			builder.append(" : ");
			for (TreeConstraint treeConstraint : this.getChildrenTrees(featureId)) {
				if (treeConstraint instanceof OptionalFeature) {
					builder.append("[\"" + treeConstraint.getChildren()[0] + "\"] ");
				} else {
					if (treeConstraint instanceof OrGroup) {
						builder.append("Or(");
						for (int subFeature : treeConstraint.getChildren())
							builder.append("\"" + subFeature + "\" ");
						builder.append(")");
					} else {
						if (treeConstraint instanceof AlternativeGroup) {
							builder.append("Alternative(");
							for (int subFeature : treeConstraint.getChildren())
								builder.append("\"" + subFeature + "\" ");
							builder.append(")");
						} else {
							if (treeConstraint instanceof CardinalityGroup) {
								int minCard=((CardinalityGroup)treeConstraint).getMinCardinality();
								int maxCard=((CardinalityGroup)treeConstraint).getMaxCardinality();
								builder.append("Group["+minCard+":"+maxCard+"](");
								for (int subFeature : treeConstraint.getChildren())
									builder.append("\"" + subFeature + "\" ");
								builder.append(")");
							} else {
								if (treeConstraint instanceof MandatoryFeature) {
									builder.append("\"" + treeConstraint.getChildren()[0] + "\" ");
								}
							}
						}
					}
				}
			}
			builder.append(";");
			for (int childId : this.getChildren(featureId)) {
				if (this.hasChildren(childId))
					toString(childId, builder);
			}
		}
	}

	@Override
	protected void generateClauses(Set<Clause> clauses) {
		clauses.addAll(this.getClauses());
	}

	/**
	 * Copy.
	 *
	 * @param other
	 *            the configuration to copy
	 */
	@SuppressWarnings("unchecked")
	public void copy(FeatureModel other) {
		this.features=new FMNode[other.features.length];
		for(int i=0;i<other.features.length;i++)
			if(other.features[i]!=null)
				this.addFeature(i, other.features[i].feature.clone());
		for(TreeConstraint treeConstraint:other.treeConstraints){
			this.addTreeConstraint((TreeConstraint) treeConstraint.clone());
		}
		for(ClauseBasedConstraint constraint:other.crossTreeConstraints){
			this.addCrossTreeConstraint(constraint.clone());
		}

		for(GlobalConstraint constraint:other.globalConstraints){
			this.addGlobalConstraint(constraint.clone());
		}
	}

	@Override
	public FeatureModel clone() {
		FeatureModel clone = new FeatureModel();
		clone.copy(this);
		return clone;
	}
	
	@Override
	public boolean isSatisfied(Configuration conf) {
		for(int i=0;i<this.getNumFeatures();i++){
			if(conf.isFeatureSelected(i)){
				for(TreeConstraint tree:this.features[i].childrenTrees){
					if(tree.getType()==TreeConstraintType.CARDINALITY_GROUP){
						
						int[] countSibling=new int[3];
						for(int siblingId : tree.getChildren()){
							FeatureState siblingState=conf.getFeatureState(siblingId);
							countSibling[siblingState.ordinal()]++;
						}

						if(countSibling[1]>tree.getMaxCardinality() || countSibling[1]+countSibling[2]<tree.getMinCardinality()){
							return false;
						}
					}
				}
			}
		}
		return super.isSatisfied(conf);
	}

	public int getFeatureIdByName(String featureName) {
		for(int i=0;i<this.features.length;i++) {
			FMNode fmNode = this.features[i];
			if(fmNode.feature.getName().equals(featureName))
				return i;
		}
		return -1;
	}

}
