package com.cmpe.boun.buyemek;

public interface AsyncTaskCompleteListener<T> {
	void onTaskComplete(T result);
}