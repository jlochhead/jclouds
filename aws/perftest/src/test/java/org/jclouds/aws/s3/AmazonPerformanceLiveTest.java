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
package org.jclouds.aws.s3;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jets3t.service.S3ServiceException;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.amazon.s3.AWSAuthConnection;

/**
 * Runs operations that amazon s3 sample code is capable of performing.
 * 
 * @author Adrian Cole
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "perftest.AmazonPerformanceLiveTest", groups = { "live" })
public class AmazonPerformanceLiveTest extends BasePerformanceLiveTest {
   private AWSAuthConnection amzClient;

   @BeforeClass(groups = { "live" }, dependsOnMethods = "setUpResourcesOnThisThread")
   protected void createLiveS3Context(ITestContext testContext) throws S3ServiceException {
      if (testContext.getAttribute("jclouds.test.user") != null) {
         amzClient = new AWSAuthConnection((String) testContext.getAttribute("jclouds.test.user"),
                  (String) testContext.getAttribute("jclouds.test.key"), false);
      } else {
         throw new RuntimeException("not configured properly");
      }
   }

   @Override
   @Test(enabled = false)
   public void testPutFileSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutFileParallel() throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutInputStreamSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutInputStreamParallel() throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutStringSerial() throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   @Test(enabled = false)
   public void testPutStringParallel() throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException();
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Future<?> putByteArray(final String bucket, final String key, byte[] data,
            String contentType) {
      final com.amazon.s3.S3Object object = new com.amazon.s3.S3Object(data, null);
      final Map<String, List<String>> headers = new TreeMap<String, List<String>>();
      headers.put("Content-Type", Arrays.asList(new String[] { contentType }));
      return exec.submit(new Callable() {
         @Override
         public Object call() throws Exception {
            return amzClient.put(bucket, key, object, headers).connection.getResponseMessage();
         }
      });
   }

   @Override
   protected Future<?> putFile(String bucket, String key, File data, String contentType) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Future<?> putInputStream(String bucket, String key, InputStream data,
            String contentType) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Future<?> putString(String bucket, String key, String data, String contentType) {
      throw new UnsupportedOperationException();
   }

}
