package com.guhe.linklist;

/**
 * @author njl
 * @date 2023/2/9
 */
public class SinglyLinkedList<E> {

	private Node<E> head; // head refers to the first node
	private int size; // the number of nodes

	public SinglyLinkedList() {
	}

	// 检测该链表是否有环
	public boolean detectLoop() {
		Node<E> fast = head;
		Node<E> slow = head;
		while (fast != null && fast.next != null) {
			fast = fast.next.next;
			slow = slow.next;
			if (fast == null) {
				return true;
			}
		}
		return false;
	}



	private static class Node<E> {
		private E item;
		Node<E> next;

		public Node(E item) {
			this.item = item;
		}

		public Node(E item, Node<E> next) {
			this.item = item;
			this.next = next;
		}
	}
}
