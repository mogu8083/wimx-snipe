package com.ulalalab.snipe.infra.manage;

import com.ulalalab.snipe.infra.channel.SpChannelGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Slf4j
public class EventManager {

    private static EventManager eventManager;
    //private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    //private EventLoop eventLoop = new DefaultEventLoop();
    //private ExecutorService executorService;
    //private ThreadPoolTaskExecutor taskExecutor;
    private SpChannelGroup spChannelGroup;

    static {
        eventManager = new EventManager();
    }

    private EventManager() {
        //this.initExecutor();
        //this.initExecutorService();
        this.initSpChannelGroup();
    }

    public static EventManager getInstance() {
        return eventManager;
    }

//    public EventLoop getEventLoop() {
//        return eventLoop;
//    }
//
//    public EventLoopGroup getEventLoopGroup() {
//        return eventLoopGroup;
//    }

//    public ExecutorService getExecutorService() {
//        return executorService;
//    }

//    @Async
//    public Future<ThreadPoolTaskExecutor> getTaskExecutor() {
//        return CompletableFuture.completedFuture(taskExecutor);
//    }

//    private void initExecutorService() {
//        executorService = new ForkJoinPool
//                (Runtime.getRuntime().availableProcessors(),
//                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
//                        null, true);
//    }

//    private void initExecutor() {
//        taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(15);
//        taskExecutor.setMaxPoolSize(25);
//        taskExecutor.setQueueCapacity(Integer.MAX_VALUE);
//        taskExecutor.initialize();
//    }

    private void initSpChannelGroup() {
        spChannelGroup = new SpChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public SpChannelGroup getSpChannelGroup() {
        return spChannelGroup;
    }
}