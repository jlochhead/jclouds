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
package org.jclouds.compute.stub.config;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;
import static org.jclouds.compute.predicates.ImagePredicates.architectureIn;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.lifecycle.Closer;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * 
 * @author Adrian Cole
 */
@SingleThreaded
public class StubComputeServiceContextModule extends AbstractModule {
   // STUB STUFF STATIC SO MULTIPLE CONTEXTS CAN SEE IT
   private static final AtomicInteger nodeIds = new AtomicInteger(0);
   private static final ConcurrentMap<Integer, StubNodeMetadata> nodes = new ConcurrentHashMap<Integer, StubNodeMetadata>();

   @Provides
   @Singleton
   ConcurrentMap<Integer, StubNodeMetadata> provideNodes() {
      return nodes;
   }

   @Provides
   @Named("NODE_ID")
   Integer provideNodeId() {
      return nodeIds.incrementAndGet();
   }

   @Singleton
   @Provides
   @Named("PUBLIC_IP_PREFIX")
   String publicIpPrefix() {
      return "144.175.1.";
   }

   @Singleton
   @Provides
   @Named("PRIVATE_IP_PREFIX")
   String privateIpPrefix() {
      return "10.1.1.";
   }

   @Singleton
   @Provides
   @Named("PASSWORD_PREFIX")
   String passwordPrefix() {
      return "password";
   }

   @Singleton
   @Provides
   SocketOpen socketOpen(StubSocketOpen in) {
      return in;
   }

   @Singleton
   public static class StubSocketOpen implements SocketOpen {
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;
      private final String publicIpPrefix;

      @Inject
      public StubSocketOpen(ConcurrentMap<Integer, StubNodeMetadata> nodes,
            @Named("PUBLIC_IP_PREFIX") String publicIpPrefix) {
         this.nodes = nodes;
         this.publicIpPrefix = publicIpPrefix;
      }

      @Override
      public boolean apply(IPSocket input) {
         if (input.getAddress().indexOf(publicIpPrefix) == -1)
            return false;
         String id = input.getAddress().replace(publicIpPrefix, "");
         int intId = Integer.parseInt(id);
         StubNodeMetadata node = nodes.get(intId);
         return node != null && node.getState() == NodeState.RUNNING;
      }

   }

   @SuppressWarnings("unchecked")
   @Provides
   @Singleton
   RestContext<ConcurrentMap, ConcurrentMap> provideRestContext(Closer closer) {
      return new RestContextImpl<ConcurrentMap, ConcurrentMap>(closer, nodes,
            nodes, URI.create("http://stub"), System.getProperty("user.name"));
   }

   // NORMAL STUFF
   private final String providerName;

   public StubComputeServiceContextModule(String providerName) {
      this.providerName = providerName;
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<ComputeServiceContext>() {
      })
            .to(
                  new TypeLiteral<ComputeServiceContextImpl<ConcurrentMap, ConcurrentMap>>() {
                  }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(StubAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(StubListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(StubGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(StubRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(StubDestroyNodeStrategy.class);
      bind(LoadBalancerService.class).toProvider(
            Providers.<LoadBalancerService> of(null));
   }

   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(UBUNTU);
   }

   public static class StubNodeMetadata extends NodeMetadataImpl {

      /** The serialVersionUID */
      private static final long serialVersionUID = 5538798859671465494L;
      private NodeState state;
      private final ExecutorService service;

      public StubNodeMetadata(String providerId, String name, String id,
            Location location, URI uri, Map<String, String> userMetadata,
            String tag, Image image, NodeState state,
            Iterable<String> publicAddresses,
            Iterable<String> privateAddresses, Map<String, String> extra,
            Credentials credentials, ExecutorService service) {
         super(providerId, name, id, location, uri, userMetadata, tag, image,
               state, publicAddresses, privateAddresses, extra, credentials);
         this.setState(state, 0);
         this.service = service;
      }

      public void setState(final NodeState state, final long millis) {
         if (millis == 0l)
            this.state = state;
         else
            service.execute(new Runnable() {

               @Override
               public void run() {
                  try {
                     Thread.sleep(millis);
                  } catch (InterruptedException e) {
                     Throwables.propagate(e);
                  }
                  StubNodeMetadata.this.state = state;
               }

            });
      }

      @Override
      public NodeState getState() {
         return state;
      }

   }

   @Singleton
   public static class StubAddNodeWithTagStrategy implements
         AddNodeWithTagStrategy {
      private final Location location;
      private final ExecutorService service;
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;
      private final Provider<Integer> idProvider;
      private final String publicIpPrefix;
      private final String privateIpPrefix;
      private final String passwordPrefix;

      @Inject
      public StubAddNodeWithTagStrategy(
            ConcurrentMap<Integer, StubNodeMetadata> nodes, Location location,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service,
            @Named("NODE_ID") Provider<Integer> idProvider,
            @Named("PUBLIC_IP_PREFIX") String publicIpPrefix,
            @Named("PRIVATE_IP_PREFIX") String privateIpPrefix,
            @Named("PASSWORD_PREFIX") String passwordPrefix) {
         this.nodes = nodes;
         this.location = location;
         this.service = Executors.newCachedThreadPool();
         this.idProvider = idProvider;
         this.publicIpPrefix = publicIpPrefix;
         this.privateIpPrefix = privateIpPrefix;
         this.passwordPrefix = passwordPrefix;
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
         checkArgument(location.equals(template.getLocation()),
               "invalid location: " + template.getLocation());
         int id = idProvider.get();
         StubNodeMetadata node = new StubNodeMetadata(id + "", name, id + "",
               location, null, ImmutableMap.<String, String> of(), tag,
               template.getImage(), NodeState.PENDING, ImmutableSet
                     .<String> of(publicIpPrefix + id), ImmutableSet
                     .<String> of(privateIpPrefix + id), ImmutableMap
                     .<String, String> of(), new Credentials("root",
                     passwordPrefix + id), service);
         nodes.put(id, node);
         node.setState(NodeState.RUNNING, 100);
         return node;
      }

   }

   @Singleton
   public static class StubGetNodeMetadataStrategy implements
         GetNodeMetadataStrategy {
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;

      @Inject
      protected StubGetNodeMetadataStrategy(
            ConcurrentMap<Integer, StubNodeMetadata> nodes) {
         this.nodes = nodes;
      }

      @Override
      public NodeMetadata execute(String id) {
         return nodes.get(Integer.parseInt(id));
      }
   }

   @Singleton
   public static class StubListNodesStrategy implements ListNodesStrategy {
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;

      @Inject
      protected StubListNodesStrategy(
            ConcurrentMap<Integer, StubNodeMetadata> nodes) {
         this.nodes = nodes;
      }

      @Override
      public Iterable<? extends ComputeMetadata> list() {
         return listDetailsOnNodesMatching(NodePredicates.all());
      }

      @Override
      public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(
            Predicate<ComputeMetadata> filter) {
         return Iterables.filter(nodes.values(), filter);
      }
   }

   @Singleton
   public static class StubRebootNodeStrategy implements RebootNodeStrategy {
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;

      @Inject
      protected StubRebootNodeStrategy(
            ConcurrentMap<Integer, StubNodeMetadata> nodes) {
         this.nodes = nodes;
      }

      @Override
      public StubNodeMetadata execute(String id) {
         StubNodeMetadata node = nodes.get(Integer.parseInt(id));
         if (node == null)
            throw new ResourceNotFoundException("node not found: " + id);
         node.setState(NodeState.PENDING, 0);
         node.setState(NodeState.RUNNING, 50);
         return node;
      }
   }

   @Singleton
   public static class StubDestroyNodeStrategy implements DestroyNodeStrategy {
      private final ConcurrentMap<Integer, StubNodeMetadata> nodes;
      private final ExecutorService service;

      @Inject
      protected StubDestroyNodeStrategy(
            ConcurrentMap<Integer, StubNodeMetadata> nodes,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service) {
         this.nodes = nodes;
         this.service = service;
      }

      @Override
      public StubNodeMetadata execute(String id) {
         final int nodeId = Integer.parseInt(id);
         StubNodeMetadata node = nodes.get(nodeId);
         if (node == null)
            return node;
         node.setState(NodeState.PENDING, 0);
         node.setState(NodeState.TERMINATED, 50);
         service.execute(new Runnable() {

            @Override
            public void run() {
               try {
                  Thread.sleep(200);
               } catch (InterruptedException e) {
                  Throwables.propagate(e);
               } finally {
                  nodes.remove(nodeId);
               }
            }

         });
         return node;
      }
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   protected String provideNamingConvention() {
      return "%s-%s";
   }

   @Provides
   @Singleton
   protected Set<? extends Size> provideSizes() {
      return ImmutableSet.of(new StubSize("small", 1, 1740, 160, ImmutableSet
            .of(Architecture.X86_32)), new StubSize("medium", 4, 7680, 850,
            ImmutableSet.of(Architecture.X86_64)), new StubSize("large", 8,
            15360, 1690, ImmutableSet.of(Architecture.X86_64)));
   }

   private static class StubSize extends
         org.jclouds.compute.domain.internal.SizeImpl {
      /** The serialVersionUID */
      private static final long serialVersionUID = -1842135761654973637L;

      StubSize(String type, int cores, int ram, int disk,
            Iterable<Architecture> supportedArchitectures) {
         super(type, type, type, null, null,
               ImmutableMap.<String, String> of(), cores, ram, disk,
               architectureIn(supportedArchitectures));
      }
   }

   @Provides
   @Singleton
   protected Set<? extends Image> provideImages(Location defaultLocation) {
      return ImmutableSet.of(new ImageImpl("1", OsFamily.UBUNTU.name(), "1",
            defaultLocation, null, ImmutableMap.<String, String> of(),
            "stub ubuntu 32", "", OsFamily.UBUNTU, "ubuntu 64",
            Architecture.X86_64, new Credentials("root", null)), new ImageImpl(
            "2", OsFamily.UBUNTU.name(), "2", defaultLocation, null,
            ImmutableMap.<String, String> of(), "stub ubuntu 64", "",
            OsFamily.UBUNTU, "ubuntu 64", Architecture.X86_64, new Credentials(
                  "root", null)), new ImageImpl("3", OsFamily.CENTOS.name(),
            "3", defaultLocation, null, ImmutableMap.<String, String> of(),
            "stub centos 64", "", OsFamily.CENTOS, "centos 64",
            Architecture.X86_64, new Credentials("root", null)));
   }

   @Provides
   @Singleton
   Location getLocation() {
      Location provider = new LocationImpl(LocationScope.PROVIDER,
            providerName, providerName, null);
      return new LocationImpl(LocationScope.ZONE, "memory", "memory", provider);
   }

   @Provides
   @Singleton
   Set<? extends Location> provideLocations(Location location) {
      return ImmutableSet.of(location);
   }

}
