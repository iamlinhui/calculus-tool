package cn.promptness.calculus.service;

import javafx.concurrent.WorkerStateEvent;

@FunctionalInterface
public interface Callback {

    void call(WorkerStateEvent event);
}
