/**
 * Copyright (c) 2014-2015 Eclectic Logic LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.eclecticlogic.ezra.poc;

import java.util.ArrayList;
import java.util.List;


/**
 * @author kabram.
 *
 */
public class MyWorkflow extends AbstractWorkflow<MyWorkflow> {

    
    public void onStart() {        
        Activity a = create(MyActivity.class).executing(m -> m.getGreeting());
        onSuccess(a).onGreetingCompletion(a.result());
    }
    

    public void onGreetingCompletion(ActivityResult<String> value) {
        Activity a1 = create(MyActivity.class).executing(m -> m.getFirstName());
        Activity a2 = create(MyActivity.class).executing(m -> m.getLastName());
        
        waitFor(a1, a2).sayHello(value.getResult(), a1.result(), a2.result());
    }
    
    
    public void sayHello(String v, ActivityResult<String> firstName, ActivityResult<String> lastName) {
        List<Activity> activities = new ArrayList<Activity>();
        
        activities.add(create(MyActivity.class).executing(m -> m.getFirstName()));
        
//        waitFor(activities).sayHello(resultList(activities))
    }
    
    
    public void arrayCall() {
        ActivityList list = new ActivityList();
        for (int i = 0; i < 10; i++) {
            list.add(create(MyActivity.class).executing(m -> m.getFirstName()));
        }
        waitFor(list).processAllOfThem(list.getResult());
    }


    public void processAllOfThem(List<ActivityResult<String>> names) {
//        EzraJob job = EzraJob.create(someInstance).executing(m -> m.someMethod(null)) // no parameter or a single string parameter.
//                .cronSchedule(" * * * /3 JAN,FEB")
//                .retryPolicy(RetryPolicy ....)
//                .chain(someOtherInstance);
        
    }
}

