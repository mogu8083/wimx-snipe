//package com.ulalalab.api.common.service;
//
//import io.netty.util.concurrent.*;
//import io.netty.util.concurrent.Future;
//import io.netty.util.concurrent.ScheduledFuture;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.*;
//
//public class EventExecutorNew implements EventExecutor {
//
//	@Override
//	public boolean isShuttingDown() {
//		return false;
//	}
//
//	@Override
//	public Future<?> shutdownGracefully() {
//		return null;
//	}
//
//	@Override
//	public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
//		return null;
//	}
//
//	@Override
//	public Future<?> terminationFuture() {
//		return null;
//	}
//
//	@Override
//	public void shutdown() {
//
//	}
//
//	@Override
//	public List<Runnable> shutdownNow() {
//		return null;
//	}
//
//	@Override
//	public boolean isShutdown() {
//		return false;
//	}
//
//	@Override
//	public boolean isTerminated() {
//		return false;
//	}
//
//	@Override
//	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
//		return false;
//	}
//
//	@Override
//	public EventExecutor next() {
//		return null;
//	}
//
//	@Override
//	public Iterator<EventExecutor> iterator() {
//		return null;
//	}
//
//	@Override
//	public Future<?> submit(Runnable task) {
//		return null;
//	}
//
//	@Override
//	public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
//		return null;
//	}
//
//	@Override
//	public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
//		return null;
//	}
//
//	@Override
//	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
//		return null;
//	}
//
//	@Override
//	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
//		return null;
//	}
//
//	@Override
//	public <T> Future<T> submit(Runnable task, T result) {
//		task.run();
//		System.out.println("##@@ " + result);
//		return null;
//	}
//
//	@Override
//	public <T> Future<T> submit(Callable<T> task) {
//		return null;
//	}
//
//	@Override
//	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
//		return null;
//	}
//
//	@Override
//	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
//		return null;
//	}
//
//	@Override
//	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
//		return null;
//	}
//
//	@Override
//	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
//		return null;
//	}
//
//	@Override
//	public EventExecutorGroup parent() {
//		return null;
//	}
//
//	@Override
//	public boolean inEventLoop() {
//		return false;
//	}
//
//	@Override
//	public boolean inEventLoop(Thread thread) {
//		return false;
//	}
//
//	@Override
//	public <V> Promise<V> newPromise() {
//		return null;
//	}
//
//	@Override
//	public <V> ProgressivePromise<V> newProgressivePromise() {
//		return null;
//	}
//
//	@Override
//	public <V> Future<V> newSucceededFuture(V result) {
//		return null;
//	}
//
//	@Override
//	public <V> Future<V> newFailedFuture(Throwable cause) {
//		return null;
//	}
//
//	@Override
//	public void execute(Runnable command) {
//
//	}
//}