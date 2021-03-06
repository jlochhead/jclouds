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
package org.jclouds.azure.storage.blob;

import static org.jclouds.azure.storage.blob.options.CreateContainerOptions.Builder.withPublicAcl;
import static org.jclouds.azure.storage.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.blob.config.AzureBlobRestClientModule;
import org.jclouds.azure.storage.blob.functions.ParseContainerPropertiesFromHeaders;
import org.jclouds.azure.storage.blob.functions.ReturnFalseIfContainerAlreadyExists;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.blobstore.functions.ReturnNullOnContainerNotFound;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ReturnTrueOn404;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import com.google.inject.name.Names;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AzureBlobAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AzureBlobAsyncClientTest")
public class AzureBlobAsyncClientTest extends RestClientTest<AzureBlobAsyncClient> {

   public void testListContainers() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listContainers", Array.newInstance(
               ListOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/");
      assertEquals(httpMethod.getEndpoint().getQuery(), "comp=list");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListContainersOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listContainers", Array.newInstance(
               ListOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { maxResults(1).marker("marker").prefix("prefix") });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/");
      assert httpMethod.getEndpoint().getQuery().contains("comp=list");
      assert httpMethod.getEndpoint().getQuery().contains("marker=marker");
      assert httpMethod.getEndpoint().getQuery().contains("maxresults=1");
      assert httpMethod.getEndpoint().getQuery().contains("prefix=prefix");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createContainer", String.class, Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("deleteContainer", String.class);

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               CloseContentAndReturn.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   public void testCreateContainerOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createContainer", String.class, Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container",
                        withPublicAcl().withMetadata(ImmutableMultimap.of("foo", "bar")) });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 4);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(httpMethod.getHeaders().get("x-ms-meta-foo"), Collections.singletonList("bar"));
      assertEquals(httpMethod.getHeaders().get("x-ms-prop-publicaccess"), Collections
               .singletonList("true"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testCreateRootContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testDeleteRootContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("deleteRootContainer");

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               CloseContentAndReturn.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnTrueOn404.class);
   }

   public void testCreateRootContainerOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { withPublicAcl().withMetadata(ImmutableMultimap.of("foo", "bar")) });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 4);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(httpMethod.getHeaders().get("x-ms-meta-foo"), Collections.singletonList("bar"));
      assertEquals(httpMethod.getHeaders().get("x-ms-prop-publicaccess"), Collections
               .singletonList("true"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testListBlobs() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listBlobs", String.class, Array
               .newInstance(ListBlobsOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container&comp=list");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListRootBlobs() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listBlobs", Array.newInstance(
               ListBlobsOptions.class, 0).getClass());

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/$root");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container&comp=list");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testContainerProperties() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("getContainerProperties", String.class);

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container" });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container");
      assertEquals(httpMethod.getMethod(), HttpMethod.HEAD);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               ParseContainerPropertiesFromHeaders.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnNullOnContainerNotFound.class);
   }

   public void testSetResourceMetadata() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("setResourceMetadata", String.class,
               Map.class);

      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container", ImmutableMap.of("key", "value") });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=container&comp=metadata");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 3);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("0"));
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(httpMethod.getHeaders().get("x-ms-meta-key"), Collections.singletonList("value"));

      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               CloseContentAndReturn.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testSetBlobMetadata() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("setBlobMetadata", String.class,
               String.class, Map.class);
      GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod = processor.createRequest(method,
               new Object[] { "container", "blob", ImmutableMap.of("key", "value") });
      assertEquals(httpMethod.getEndpoint().getHost(), "myaccount.blob.core.windows.net");
      assertEquals(httpMethod.getEndpoint().getPath(), "/container/blob");
      assertEquals(httpMethod.getEndpoint().getQuery(), "comp=metadata");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 3);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("0"));
      assertEquals(httpMethod.getHeaders().get("x-ms-meta-key"), Collections.singletonList("value"));

      assertEquals(processor.createResponseParser(method, httpMethod).getClass(),
               CloseContentAndReturn.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<AzureBlobAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AzureBlobAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AzureBlobAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AzureBlobRestClientModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new AzureBlobPropertiesBuilder(new Properties())
                     .withCredentials("myaccount", "key").build());
            install(new NullLoggingModule());
            super.configure();
         }

      };
   }
}
