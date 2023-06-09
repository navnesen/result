package com.github.navnesen.util;

import com.github.navnesen.result.InspectAction;
import com.github.navnesen.result.TypeAction;
import com.github.navnesen.result.TypeActionAnd;
import com.github.navnesen.result.TypeActionMap;

import java.util.Optional;

public class Result<O, E> {

	public static <O, E> Result<O, E> ok(O value) {
		return new Result<>(false, value, null);
	}

	public static <O, E> Result<O, E> err(E exception) {
		return new Result<>(false, null, exception);
	}

	protected boolean isError;
	protected E exception;
	protected O value;

	protected Result(boolean isError, O value, E exception) {
		this.isError = isError;
		this.value = value;
		this.exception = exception;
	}

	public boolean isOk() {
		return !this.isError;
	}

	public boolean isOkAnd(TypeActionAnd<O> action) {
		if (this.isErr()) return false;
		return action.run(this.value);
	}

	public boolean isErr() {
		return this.isError;
	}

	public boolean isErrAnd(TypeActionAnd<E> action) {
		if (this.isOk()) return false;
		return action.run(this.exception);
	}

	public Optional<O> ok() {
		return this.isError ? Optional.empty() : Optional.ofNullable(this.value);
	}

	public Optional<E> err() {
		return this.isError ? Optional.ofNullable(this.exception) : Optional.empty();
	}

	public <U> Result<U, E> map(TypeActionMap<O, U> action) {
		if (this.isErr()) return Result.err(this.exception);
		return Result.ok(action.run(this.value));
	}

	public <U> Result<U, E> mapOr(TypeActionMap<O, U> action, O defaultValue) {
		if (this.isErr()) return Result.ok(action.run(defaultValue));
		return Result.ok(action.run(this.value));
	}

	public <U> Result<U, E> mapOrElse(TypeActionMap<O, U> action, TypeAction<O> defaultValueAction) {
		if (this.isErr()) return Result.ok(action.run(defaultValueAction.run()));
		return Result.ok(action.run(this.value));
	}

	public <U> Result<O, U> mapErr(TypeActionMap<E, U> action) {
		if (this.isOk()) return Result.ok(this.value);
		return Result.err(action.run(this.exception));
	}

	public Result<O, E> inspect(InspectAction<O> action) {
		if (this.isOk()) action.run(this.value);
		return this;
	}

	public Result<O, E> inspectErr(InspectAction<E> action) {
		if (this.isErr()) action.run(this.exception);
		return this;
	}

	public O expect(String message) {
		if (this.isErr()) throw new RuntimeException(message);
		return this.value;
	}

	public O unwrap() {
		return this.expect("could not unwrap value");
	}

	public E expectErr(String message) {
		if (this.isOk()) throw new RuntimeException(message);
		return this.exception;
	}

	public E unwrapErr() {
		return this.expectErr("could not unwrap error");
	}

	public <U> Result<U, E> and(Result<U, E> res) {
		if (this.isErr()) return Result.err(this.exception);
		return res;
	}

	public <U> Result<U, E> andThen(TypeActionMap<O, Result<U, E>> action) {
		if (this.isErr()) return Result.err(this.exception);
		return action.run(this.value);
	}

	public <F> Result<O, F> or(Result<O, F> res) {
		if (this.isOk()) return Result.ok(this.value);
		return res;
	}

	public <F> Result<O, F> orElse(TypeActionMap<E, Result<O, F>> action) {
		if (this.isOk()) return Result.ok(this.value);
		return action.run(this.exception);
	}

	public O unwrapOr(O defaultValue) {
		if (this.isOk()) return this.value;
		return defaultValue;
	}

	public O unwrapOrElse(TypeActionMap<E, O> action) {
		if (this.isOk()) return this.value;
		return action.run(this.exception);
	}

	public <U> boolean contains(U x) {
		if (this.isErr()) return false;
		return this.value == x;
	}

	public <F> boolean containsErr(F f) {
		if (this.isOk()) return false;
		return this.exception == f;
	}
}
