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
package org.jclouds.aws.ec2.xml;

import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.date.DateService;

import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * DescribeImagesResponse xmlns="http:
 * 
 * @author Adrian Cole
 * @see <a href="http: />
 */
public class DescribeInstancesResponseHandler extends
         BaseReservationHandler<SortedSet<Reservation>> {
   private SortedSet<Reservation> reservations = Sets.newTreeSet();

   @Inject
   DescribeInstancesResponseHandler(DateService dateService, @EC2 String defaultRegion) {
      super(dateService, defaultRegion);
   }

   @Override
   public SortedSet<Reservation> getResult() {
      return reservations;
   }

   @Override
   protected void inItem() {
      if (!inInstances && !inProductCodes && !inGroups) {
         reservations.add(super.newReservation());
      } else {
         super.inItem();
      }
   }

}
