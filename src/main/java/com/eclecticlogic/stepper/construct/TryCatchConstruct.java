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

package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.AttributableState;
import com.eclecticlogic.stepper.state.Parallel;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.eclecticlogic.stepper.state.observer.NotificationReceiver;
import com.google.common.collect.Lists;

import java.util.List;

public class TryCatchConstruct extends Construct implements NotificationReceiver {

    private final List<AttributableState> statesOfInterest = Lists.newArrayList();
    private final List<CatchClause> clauses = Lists.newArrayList();
    private Construct tryBlock;


    public void setTryBlock(Construct tryBlock) {
        this.tryBlock = tryBlock;
    }


    @Override
    public void receive(State state) {
        if (state instanceof Task || state instanceof Parallel) {
            statesOfInterest.add((AttributableState) state);
        }
    }


    public void add(CatchClause cc) {
        clauses.add(cc);
    }


    @Override
    public void weave(WeaveContext context) {
        tryBlock.weave(context);

        for (CatchClause clause : clauses) {
            clause.getBlock().weave(context);
            for (AttributableState state : statesOfInterest) {
                state.addCatch(() ->
                        state.handleObject(() -> {
                            state.captureAttribute("ErrorEquals");
                            state.handleArray(() -> clause.getErrors().forEach(state::setProperty));
                            if (clause.getResultPath() != null) {
                                state.captureAttribute("ResultPath");
                                state.setProperty("$." + clause.getResultPath());
                            }
                            state.captureAttribute("Next");
                            state.setProperty(clause.getBlock().getFirstStateName());
                        })
                );
            }
        }

        getNext().weave(context);
        setNextStateName(getNext().getFirstStateName());
    }


    @Override
    protected String getFirstStateName() {
        return tryBlock.getFirstStateName();
    }


    @Override
    protected void setNextStateName(String name) {
        getLastInChain(tryBlock).setNextStateName(name);
    }


    @Override
    public List<State> getStates() {
        List<State> states = Lists.newArrayList();
        states.addAll(tryBlock.getStates());
        for (CatchClause clause : clauses) {
            states.addAll(clause.getBlock().getStates());
        }
        states.addAll(getNext().getStates());
        return states;
    }


}
