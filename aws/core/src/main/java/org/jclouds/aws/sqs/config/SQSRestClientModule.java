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
package org.jclouds.aws.sqs.config;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.sqs.SQS;
import org.jclouds.aws.sqs.SQSAsyncClient;
import org.jclouds.aws.sqs.SQSClient;
import org.jclouds.aws.sqs.reference.SQSConstants;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;

/**
 * Configures the SQS connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class SQSRestClientModule extends RestClientModule<SQSClient, SQSAsyncClient> {

   public SQSRestClientModule() {
      super(SQSClient.class, SQSAsyncClient.class);
   }

   @Provides
   @TimeStamp
   protected String provideTimeStamp(final DateService dateService,
            @Named(SQSConstants.PROPERTY_AWS_EXPIREINTERVAL) final int expiration) {
      return dateService.iso8601DateFormat(new Date(System.currentTimeMillis()
               + (expiration * 1000)));
   }

   @Provides
   @Singleton
   @SQS
   Map<String, URI> provideRegions(
            @Named(SQSConstants.PROPERTY_SQS_ENDPOINT_US_EAST_1) String useast,
            @Named(SQSConstants.PROPERTY_SQS_ENDPOINT_US_WEST_1) String uswest,
            @Named(SQSConstants.PROPERTY_SQS_ENDPOINT_EU_WEST_1) String euwest,
            @Named(SQSConstants.PROPERTY_SQS_ENDPOINT_AP_SOUTHEAST_1) String apsoutheast) {
      return ImmutableMap.<String, URI> of(Region.US_EAST_1, URI.create(useast), Region.US_WEST_1,
               URI.create(uswest), Region.EU_WEST_1, URI.create(euwest), Region.AP_SOUTHEAST_1, URI
                        .create(apsoutheast));
   }

   @Provides
   @Singleton
   @SQS
   protected URI provideURI(@Named(SQSConstants.PROPERTY_SQS_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   @SQS
   String getDefaultRegion(@SQS URI uri, @SQS Map<String, URI> map) {
      return ImmutableBiMap.<String, URI> builder().putAll(map).build().inverse().get(uri);
   }

   @Provides
   @Singleton
   RequestSigner provideRequestSigner(FormSigner in) {
      return in;
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseAWSErrorFromXmlContent.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(
               AWSRedirectionRetryHandler.class);
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
               AWSClientErrorRetryHandler.class);
   }
}