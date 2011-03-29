/*******************************************************************************
 * Copyright (c) 2007, 2011 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.toolbox.assemblyeditor.model;

import java.util.List;

/**
 * BaseClass for all Nodes.
 * 
 * @param <T>
 *            the type of the childNode
 */
public abstract class AbstractAssemblyNode<T> {

	protected String name;
	protected BundleNode bundle;
	protected AbstractAssemblyNode parent;

	public AbstractAssemblyNode(final AbstractAssemblyNode parent) {
		this.parent = parent;
	}

	public AbstractAssemblyNode getParent() {
		return parent;
	}

	public void delete() {
		if (null != getParent()) {
			getParent().getChildren().remove(this);
		} else {
			System.err.println("Can not delete node: parent is null");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public abstract String getTreeLabel();

	public abstract List<T> getChildren();

	public abstract boolean add(T child);

	public boolean hasChildren() {
		final List<T> children = getChildren();
		if (null == children) {
			return false;
		}

		return !children.isEmpty();
	}

	public BundleNode getBundle() {
		return bundle;
	}

	public void setBundle(final BundleNode bundle) {
		this.bundle = bundle;
	}

	private int getCurrentIndex() {
		if (null == parent) {
			return -1;
		}

		for (int i = 0; i < parent.getChildren().size(); i++) {
			final Object sibling = parent.getChildren().get(i);

			if (sibling.equals(this)) {
				return i;
			}
		}
		return -1;
	}

	public boolean hasNextSibling() {
		final int currentIndex = getCurrentIndex();

		if (currentIndex == -1) {
			return false;
		}

		return currentIndex < getParent().getChildren().size() - 1;
	}

	public boolean hasPreviousSibling() {
		final int currentIndex = getCurrentIndex();

		if (currentIndex == -1) {
			return false;
		}

		return currentIndex > 0;
	}

	public T getPreviousSibling() {
		final int currentIndex = getCurrentIndex();

		if (currentIndex < 1) {
			return null;
		}

		return (T) parent.getChildren().get(currentIndex - 1);
	}

	public boolean moveDown() {
		if (!hasNextSibling()) {
			return false;
		}

		final List<T> siblings = parent.getChildren();
		final int selfIndex = getCurrentIndex();
		final T self = siblings.remove(selfIndex);
		siblings.add(selfIndex + 1, self);
		return true;
	}

	public boolean moveUp() {
		if (!hasPreviousSibling()) {
			return false;
		}

		final List<T> siblings = parent.getChildren();
		int selfIndex = getCurrentIndex();
		final T self = siblings.remove(selfIndex);
		siblings.add(--selfIndex, self);
		return false;
	}
}
