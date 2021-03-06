/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.predicates;

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class InstanceStateTerminated implements Predicate<RunningInstance> {

   private final EC2Client client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public InstanceStateTerminated(EC2Client client) {
      this.client = client;
   }

   public boolean apply(RunningInstance instance) {
      logger.trace("looking for state on instance %s", instance);
      try {
         instance = refresh(instance);
      } catch (NoSuchElementException e) {
         return true;
      }
      logger.trace("%s: looking for instance state %s: currently: %s", instance.getId(),
               InstanceState.TERMINATED, instance.getInstanceState());
      return instance.getInstanceState() == InstanceState.TERMINATED;
   }

   private RunningInstance refresh(RunningInstance instance) {
      return Iterables.getOnlyElement(Iterables.getOnlyElement(client.getInstanceServices()
               .describeInstancesInRegion(instance.getRegion(), instance.getId())));
   }
}
