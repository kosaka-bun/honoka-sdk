package de.honoka.util.json.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public abstract class JsonArray<T> implements Collection<T> {
	
	/**
	 * 可为null，null表示类型自动推断
	 */
	protected Class<?> dataType;
	
	public JsonArray(Class<?> dataType) {
		this.dataType = dataType;
	}
	
	public Class<?> getDataType() {
		return dataType;
	}
	
	public void setDataType(Class<?> dataType) {
		this.dataType = dataType;
	}
	
	//region 用于提醒实现构造器的方法
	
	protected abstract void initEmpty();
	
	protected abstract void initByJsonString(String jsonStr);
	
	protected abstract <A> void initByOriginalJsonArray(A originalJsonArray);
	
	//endregion
	
	public abstract T get(int index);
	
	@Override
	public abstract Iterator<T> iterator();
	
	@Override
	public abstract String toString();
	
	@Override
	public abstract int size();
	
	@Override
	public abstract boolean add(T t);
	
	/**
	 * 移除原框架的JsonArray中的某个元素
	 */
	protected abstract boolean originalRemove(int index);
	
	@Override
	public void forEach(Consumer<? super T> action) {
		for(T t : this) {
			action.accept(t);
		}
	}
	
	@Override
	public boolean isEmpty() {
		return size() <= 0;
	}
	
	@Override
	public boolean contains(Object o) {
		for(T t : this) {
			if(t.equals(o)) return true;
		}
		return false;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			if(!contains(o)) return false;
		}
		return true;
	}
	
	@Override
	public Object[] toArray() {
		Object[] objs = new Object[size()];
		int i = 0;
		for(T t : this) {
			objs[i] = t;
			i++;
		}
		return objs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T1> T1[] toArray(T1[] a) {
		Object[] objs = toArray();
		for(int i = 0; i < a.length; i++) {
			a[i] = (T1) objs[i];
		}
		return a;
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean allSuccess = true;
		try {
			for(T t : c) {
				boolean r = add(t);
				if(!r) allSuccess = false;
			}
			return allSuccess;
		} catch(Throwable t) {
			return false;
		}
	}
	
	@Override
	public boolean remove(Object o) {
		int i = 0;
		for(T t : this) {
			if(t.equals(o)) {
				originalRemove(i);
				return true;
			}
			i++;
		}
		return false;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean allSuccess = true;
		try {
			for(Object o : c) {
				boolean r = remove(o);
				if(!r) allSuccess = false;
			}
			return allSuccess;
		} catch(Throwable t) {
			return false;
		}
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean allSuccess = true;
		for(int i = 0; i < size(); i++) {
			if(!c.contains(get(i))) {
				boolean r = originalRemove(i);
				if(!r) allSuccess = false;
				else {
					//移除成功，元素下标有变化，重新遍历
					i = -1;		//下一次循环，i=0
				}
			}
		}
		return allSuccess;
	}
	
	@Override
	public void clear() {
		initEmpty();
	}
}
