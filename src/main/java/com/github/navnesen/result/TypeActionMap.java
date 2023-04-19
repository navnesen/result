package com.github.navnesen.result;

public interface TypeActionMap<T, U> {
	U run(T value);
}
