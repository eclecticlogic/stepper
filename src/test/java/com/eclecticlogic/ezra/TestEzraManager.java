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
package com.eclecticlogic.ezra;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.model.DomainInfo;
import com.amazonaws.services.simpleworkflow.model.DomainInfos;
import com.google.common.collect.Lists;

/**
 * @author kabram.
 *
 */
@Test
public class TestEzraManager {

    public void testDomainRegistrationAlreadyExistsNoPaging() {
        EzraManager manager = new EzraManager();
        manager.setDomain("abc");

        AmazonSimpleWorkflow workflow = mock(AmazonSimpleWorkflow.class);
        when(workflow.listDomains(any())).thenReturn(
                new DomainInfos().withDomainInfos(Lists.newArrayList(new DomainInfo().withName("def"),
                        new DomainInfo().withName("abc"))));
        manager.setWorkflowClient(workflow);
        manager.registerDomain();

        verify(workflow, times(0)).registerDomain(any());
    }


    public void testDomainRegistrationAlreadyExistsPaging() {
        EzraManager manager = new EzraManager();
        manager.setDomain("abc");

        AmazonSimpleWorkflow workflow = mock(AmazonSimpleWorkflow.class);
        when(workflow.listDomains(any())).thenReturn(
                new DomainInfos().withDomainInfos(
                        Lists.newArrayList(new DomainInfo().withName("def"), new DomainInfo().withName("abcdef")))
                        .withNextPageToken("a")).thenReturn(
                new DomainInfos().withDomainInfos(Lists.newArrayList(new DomainInfo().withName("def"),
                        new DomainInfo().withName("abc"))));
        manager.setWorkflowClient(workflow);
        manager.registerDomain();

        verify(workflow, times(0)).registerDomain(any());
        verify(workflow, times(2)).listDomains(any());
    }
    
    
}
