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
package org.jclouds.vcloud.hostingdotcom.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudAsyncClient;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudClient;
import org.jclouds.vcloud.reference.VCloudConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
public class HostingDotComVCloudContextModule extends AbstractModule {
   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   RestContext<HostingDotComVCloudClient, HostingDotComVCloudAsyncClient> provideContext(Closer closer,
            HostingDotComVCloudAsyncClient asynchApi, HostingDotComVCloudClient defaultApi,
            @Org URI endPoint, @Named(VCloudConstants.PROPERTY_VCLOUD_USER) String account) {
      return new RestContextImpl<HostingDotComVCloudClient, HostingDotComVCloudAsyncClient>(closer,
               asynchApi, defaultApi, endPoint, account);
   }

}