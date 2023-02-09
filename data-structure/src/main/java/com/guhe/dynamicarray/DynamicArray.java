package com.guhe.dynamicarray;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 这个类实现了动态数组
 *
 * @param <E> 数组存储的元素类型
 */
public class DynamicArray<E> implements Iterable<E> {

	/* 存储元素的容器 */
	private Object[] elements;
	/* 容器默认的大小 */
	private static final int DEFAULT_CAPACITY = 16;
	/* 容器的容量 */
	private int capacity;
	/* 容器的元素个数 */
	private int size;

	public DynamicArray(final int capacity) {
		this.capacity = capacity;
		this.elements = new Object[this.capacity];
	}

	/**
	 * 向容器中添加一个元素
	 */
	public void add(E element) {
		if (size == this.elements.length) {
			this.elements = Arrays.copyOf(this.elements, newCapacity(this.capacity << 1));
		}
		elements[size++] = element;
	}

	/**
	 * 修改容器中某个索引位置的元素
	 */
	public void put(int index, E element) {
		elements[index] = element;
	}

	/**
	 * 获取容器中某个索引位置的元素
	 */
	public E get(int index) {
		return getElement(index);
	}

	/**
	 * 删除容器中某个索引位置的元素
	 */
	public E remove(final int index) {
		E oldElement = getElement(index);
		fastRemove(index);
		// 如果一个容量较大的容器一直删除元素，而不增加元素，那么原来为它分配的内存就被浪费了，此时就需要缩容
		if ((capacity > DEFAULT_CAPACITY) && ((size << 2) < capacity)) {
			elements = Arrays.copyOf(elements, newCapacity(capacity >> 1)); // 容量缩为原来的一半
		}
		return oldElement;
	}

	/**
	 * 返回容器的元素大小
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 判断容器是否没有元素
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	public Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	private void fastRemove(int index) {
		final int newSize = size - 1;
		// 删除的不是容器中最后一个元素，那么就需要元素的拷贝移动
		if (newSize > index) {
			System.arraycopy(elements, index + 1, elements, index, (newSize - index));
		}

		// 如果删除的是容器中最后一个元素，则不需要进行数组的移动
		elements[this.size = newSize] = null; // let gc do its work
	}

	@SuppressWarnings("unchecked")
	private E getElement(final int index) {
		return (E) elements[index];
	}

	private int newCapacity(int newCapacity) {
		this.capacity = newCapacity;
		return this.capacity;
	}

	public DynamicArray() {
		this(DEFAULT_CAPACITY);
	}

	@Override
	public Iterator<E> iterator() {
		return new DynamicArrayIterator();
	}

	private class DynamicArrayIterator implements Iterator<E> {
		private int cursor;

		@Override
		public void remove() {
			if (cursor < 0) {
				throw new IllegalArgumentException();
			}
			DynamicArray.this.remove(cursor--);
		}

		@Override
		public boolean hasNext() {
			// 是否有下一个，如果是最后一个元素不就没有下一个了么，当游标指向最后一个元素时就没有下一个了
			return this.cursor != size;
		}

		@Override
		public E next() {
			// 边界条件，尤其是设计集合框架的时候，对索引等的边界条件的考虑，还有并发操作等
			// 编译器会为成员内部类创建一个参数是外部类的构造器，在成员内部类有个指向外部类的引用
			if (cursor > DynamicArray.this.size) {
				throw new NoSuchElementException();
			}
			if (cursor > DynamicArray.this.capacity) {
				throw new ConcurrentModificationException();
			}
			return getElement(cursor++);
		}

		@Override
		public void forEachRemaining(Consumer<? super E> action) {
			Objects.requireNonNull(action);
			for (int i = 0; i < size; i++) {
				action.accept(getElement(i));
			}
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(
				Arrays.stream(elements).filter(Objects::nonNull).toArray()
		);
	}

	// driver code
	public static void main(String[] args) {
		DynamicArray<String> names = new DynamicArray<>();
		names.add("Peubes");
		names.add("Marley");

		for (String name : names) {
			System.out.println(name);
		}

		names.stream().forEach(System.out::println);

		System.out.println(names);

		System.out.println(names.getSize());

		String remove = names.remove(0);
		System.out.println("remove = " + remove);

		for (String name : names) {
			System.out.println(name);
		}
	}
}
