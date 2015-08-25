package com.example.guavademo;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class MainActivity extends Activity {
    
    public static final String TAG = "guavademo";
    private EventBus mEventBus;
    private AsyncEventBus mAsyncEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEventBus = new EventBus(TAG);
        mAsyncEventBus = new AsyncEventBus(Executors.newFixedThreadPool(3));
        
        mEventBus.register(this);
        mAsyncEventBus.register(this);
        test();
    }
    
    private void test() {
        Optional<String> possible = Optional.of("5ABC");
        boolean isPresent = possible.isPresent();
        String value = possible.get();
        Log.d(TAG, "" + isPresent + value);
        
        List<String> list = Lists.newArrayList();
        
        
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 123;
            }
        };
        FutureTask<Integer> future = new FutureTask<Integer>(callable);
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        new Thread(future).start();
        try {
            Integer result = future.get();
            Log.d(TAG, "" + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        
        ChangeEvent event = new ChangeEvent();
        event.value = 123;
        mEventBus.post(event);
        mAsyncEventBus.post(event);
        
        NonEvent nonEvent = new NonEvent();
        nonEvent.value = 123456;
        mEventBus.post(new DeadEvent(event, event));
        mEventBus.post(nonEvent);
    }
    
    public class ChangeEvent {
        public int value;
    }
    @Subscribe
    public void onChange(ChangeEvent event) {
        Log.d(TAG, "");
    }
    
    public class NonEvent {
        public int value;
    }
    @Subscribe
    public void onDeadEvent(DeadEvent event) {
        Log.d(TAG, "");
    }
}
