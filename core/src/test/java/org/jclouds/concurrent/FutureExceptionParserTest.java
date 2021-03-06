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
package org.jclouds.concurrent;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;
import static org.jclouds.util.Utils.propagateOrNull;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Tests behavior of ListenableFutureExceptionParser
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "concurrent.ListenableFutureExceptionParserTest")
public class FutureExceptionParserTest {
   ExecutorService executorService = sameThreadExecutor();

   @Test
   public void testGet() throws InterruptedException, ExecutionException {
      ListenableFuture<?> future = createListenableFuture(new RuntimeException("foo"));
      assertEquals(future.get(), "foo");
   }

   @Test(expectedExceptions = Exception.class)
   public void testGetUnmatched() throws InterruptedException, ExecutionException {
      ListenableFuture<?> future = createListenableFuture(new Exception("foo"));
      assertEquals(future.get(), "foo");
   }

   @Test
   public void testGetLongTimeUnit() throws InterruptedException, ExecutionException,
            TimeoutException {
      ListenableFuture<?> future = createListenableFuture(new RuntimeException("foo"));
      assertEquals(future.get(1, TimeUnit.SECONDS), "foo");
   }

   @Test(expectedExceptions = Exception.class)
   public void testGetLongTimeUnitUnmatched() throws InterruptedException, ExecutionException,
            TimeoutException {
      ListenableFuture<?> future = createListenableFuture(new Exception("foo"));
      assertEquals(future.get(1, TimeUnit.SECONDS), "foo");
   }

   @SuppressWarnings("unchecked")
   private ListenableFuture<?> createListenableFuture(final Exception exception) {
      ListenableFuture<?> future = makeListenable(executorService.submit(new Callable<String>() {

         public String call() throws Exception {
            throw exception;
         }

      }), executorService);

      future = new FutureExceptionParser(future, new Function<Exception, String>() {

         public String apply(Exception from) {
            if (Iterables.size(Iterables.filter(Throwables.getCausalChain(from),
                     RuntimeException.class)) >= 1)
               return from.getMessage();
            return String.class.cast(propagateOrNull(from));
         }

      });
      return future;
   }

}
