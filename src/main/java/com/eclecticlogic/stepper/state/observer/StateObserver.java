/*
Copyright 2015-2019 KR Abram

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.eclecticlogic.stepper.state.observer;

import com.eclecticlogic.stepper.state.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StateObserver {

    private static final ThreadLocal<Stack<StateObserver>> observers = new ThreadLocal<>();

    private final List<NotificationReceiver> receiverList = new ArrayList<>();

    static {
        observers.set(new Stack<>());
    }


    public static void over(Runnable runnable) {
        observers.get().push(new StateObserver());
        runnable.run();
        observers.get().pop();
    }


    public static void register(NotificationReceiver receiver, Runnable runnable) {
        observers.get().peek().receiverList.add(receiver);
        runnable.run();
        observers.get().peek().receiverList.remove(receiver);
    }


    public static void event(State state) {
        observers.get().peek().receiverList.forEach(r -> r.receive(state));
    }
}
