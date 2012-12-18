package com.missionhub.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 * @param <T>
 *            Object's type in the tree.
 */
public class TreeDataStructure<T> {

	private final T head;

	private final ArrayList<TreeDataStructure<T>> leafs = new ArrayList<TreeDataStructure<T>>();

	private TreeDataStructure<T> parent = null;

	private HashMap<T, TreeDataStructure<T>> locate = new HashMap<T, TreeDataStructure<T>>();

	/**
	 * Instantiates a new tree.
	 * 
	 * @param root
	 *            the head
	 */
	public TreeDataStructure(final T root) {
		this.head = root;
		locate.put(head, this);
	}

	/**
	 * Adds the leaf.
	 * 
	 * @param root
	 *            the root
	 * @param leaf
	 *            the leaf
	 */
	public void addLeaf(final T root, final T leaf) {
		if (locate.containsKey(root)) {
			locate.get(root).addLeaf(leaf);
		} else {
			addLeaf(root).addLeaf(leaf);
		}
	}

	/**
	 * Adds the leaf.
	 * 
	 * @param leaf
	 *            the leaf
	 * @return the tree
	 */
	public TreeDataStructure<T> addLeaf(final T leaf) {
		final TreeDataStructure<T> t = new TreeDataStructure<T>(leaf);
		leafs.add(t);
		t.parent = this;
		t.locate = this.locate;
		locate.put(leaf, t);
		return t;
	}

	/**
	 * Sets the as parent.
	 * 
	 * @param parentRoot
	 *            the parent root
	 * @return the tree
	 */
	public TreeDataStructure<T> setAsParent(final T parentRoot) {
		final TreeDataStructure<T> t = new TreeDataStructure<T>(parentRoot);
		t.leafs.add(this);
		this.parent = t;
		t.locate = this.locate;
		t.locate.put(head, this);
		t.locate.put(parentRoot, t);
		return t;
	}

	/**
	 * Gets the head.
	 * 
	 * @return the head
	 */
	public T getHead() {
		return head;
	}

	/**
	 * Gets the tree.
	 * 
	 * @param element
	 *            the element
	 * @return the tree
	 */
	public TreeDataStructure<T> getTree(final T element) {
		return locate.get(element);
	}

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public TreeDataStructure<T> getParent() {
		return parent;
	}

	/**
	 * Gets the successors.
	 * 
	 * @param root
	 *            the root
	 * @return the successors
	 */
	public Collection<T> getSuccessors(final T root) {
		final Collection<T> successors = new ArrayList<T>();
		final TreeDataStructure<T> tree = getTree(root);
		if (null != tree) {
			for (final TreeDataStructure<T> leaf : tree.leafs) {
				successors.add(leaf.head);
			}
		}
		return successors;
	}

	/**
	 * Gets the sub trees.
	 * 
	 * @return the sub trees
	 */
	public Collection<TreeDataStructure<T>> getSubTrees() {
		return leafs;
	}

	/**
	 * Gets the successors.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param of
	 *            the of
	 * @param in
	 *            the in
	 * @return the successors
	 */
	public static <T> Collection<T> getSuccessors(final T of, final Collection<TreeDataStructure<T>> in) {
		for (final TreeDataStructure<T> tree : in) {
			if (tree.locate.containsKey(of)) {
				return tree.getSuccessors(of);
			}
		}
		return new ArrayList<T>();
	}

	@Override
	public String toString() {
		return printTree(0);
	}

	private static final int INDENT = 2;

	/**
	 * Prints the tree.
	 * 
	 * @param increment
	 *            the increment
	 * @return the string
	 */
	private String printTree(final int increment) {
		String s = "";
		String inc = "";
		for (int i = 0; i < increment; ++i) {
			inc = inc.concat(" ");
		}
		s = inc + head.toString();
		for (final TreeDataStructure<T> child : leafs) {
			s = s.concat("\n" + child.printTree(increment + INDENT));
		}
		return s;
	}

}