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
package org.jclouds.vcloud.domain;

import java.net.URI;
import java.util.Date;

import org.jclouds.vcloud.domain.internal.TaskImpl;

import com.google.inject.ImplementedBy;

/**
 * @author Adrian Cole
 */
@ImplementedBy(TaskImpl.class)
public interface Task extends Comparable<Task> {
   String getId();

   URI getLocation();

   TaskStatus getStatus();

   Date getStartTime();

   Date getEndTime();
   
   Date getExpiryTime();
   /**
    * A link to the vDC in which the task was started
    */
   NamedResource getOwner();

   /**
    * A link to the result of the task
    */
   NamedResource getResult();
   
   Error getError();
   
   @ImplementedBy(TaskImpl.ErrorImpl.class)
   static interface Error {
     String getMessage();
     String getMajorErrorCode();
     String getMinorErrorCode();
   }
}