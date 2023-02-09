package com.guhe.dynamicarray;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 数组实现的列表 - 重新在练习一遍
 *
 * @author njl
 * @date 2023/2/9
 */
public class MyDynamicArray<E> implements Iterable<E> {

	private Object[] elements;
	private static final int DEFAULT_CAPACITY = 16;
	private int size;
	private int capacity;

	public MyDynamicArray(int capacity) {
		this.capacity = capacity;
		elements = new Object[capacity];
	}

	public MyDynamicArray() {
		this(DEFAULT_CAPACITY);
	}

	public int getSize() {
		return size;
	}

	public void add(E e) {
		if (size == elements.length) {
			elements = Arrays.copyOf(elements, newCapacity(capacity << 1));
		}
		elements[size++] = e;
	}

	public E get(int index) {
		rangeCheck(index);
		return element(index);
	}

	public void put(int index, E e) {
		rangeCheck(index);
		elements[index] = e;
	}

	public E remove(int index) {
		rangeCheck(index);
		E oldElement = get(index);
		fastRemove(index);
		if ((capacity > DEFAULT_CAPACITY) && ((size << 2) < capacity)) {
			elements = Arrays.copyOf(elements, capacity >> 1);
		}
		return oldElement;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	private class Itr implements Iterator<E> {
		private int cursor; // index of next element to return

		@Override
		public boolean hasNext() {
			return cursor != size;
		}

		@Override
		public E next() {
			if (cursor >= size) {
				throw new NoSuchElementException();
			}
			if (cursor >= capacity) {
				throw new ConcurrentModificationException();
			}
			return (E) get(cursor++);
		}

		@Override
		public void remove() {
			if (cursor < 0) {
				throw new IllegalArgumentException();
			}
			MyDynamicArray.this.remove(cursor);
		}

		@Override
		public void forEachRemaining(Consumer<? super E> action) {
			Objects.requireNonNull(action);
			for (int i = 0; i < size; i++) {
				action.accept(get(i));
			}
		}
	}

	private void fastRemove(int index) {
		int numMoved = size - index - 1;
		if (numMoved > 0) { // 被删除的元素在列表中间
			System.arraycopy(elements, index + 1, elements, index, numMoved);
		}
		elements[--size] = null; // clear the reference of objects that cannot be located, so let gc do its work
	}

	private void rangeCheck(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException();
		}
	}

	@SuppressWarnings("unchecked")
	private E element(int index) {
		return (E) elements[index];
	}

	private int newCapacity(int newCapacity) {
		this.capacity = newCapacity;
		return capacity;
	}

	@Override
	public Iterator<E> iterator() {
		return new Itr();
	}

	@Override
	public String toString() {
		return Arrays.toString(Arrays.stream(elements).filter(Objects::nonNull).toArray());
	}

	// driver code
	public static void main(String[] args) {
		MyDynamicArray<String> list = new MyDynamicArray<>();
		list.add("Tommy");
		list.add("Tommy2");
		list.add("Tommy3");
		list.add("Tommy4");
		list.add("Tommy5");
		list.add("Tommy6");
		list.add("Tommy7");
		list.add("Tommy8");
		list.add("Tommy9");
		list.add("Tommy10");
		list.add("Tommy11");
		list.add("Tommy12");
		list.add("Tommy13");
		list.add("Tommy14");
		list.add("Tommy15");
		list.add("Tommy16");
		list.add("Tommy17");

		String r1 = list.remove(0);
		System.out.println("r1 = " + r1);
	}
}
