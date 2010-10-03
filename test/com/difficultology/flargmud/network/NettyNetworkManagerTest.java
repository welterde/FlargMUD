/*
 * Copyright (c) 2010, Preston Skupinski <skupinsk@cse.msu.edu>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.difficultology.flargmud.network;

import java.net.InetSocketAddress;

import junit.framework.*;
import com.google.inject.Provider;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;

import static org.mockito.Mockito.*;

public class NettyNetworkManagerTest extends TestCase {
  /**
   * Check that server bootstrap provider's get is called.
   */
  public void testNetworkManager() {
    Provider<ServerBootstrap> serverBootstrapProvider = mock(Provider.class);
    ServerBootstrap serverBootstrap = mock(ServerBootstrap.class);
    when(serverBootstrapProvider.get()).thenReturn(serverBootstrap);
    ChannelGroup channelGroup = mock(ChannelGroup.class);
    ChannelGroupFuture channelGroupFuture = mock(ChannelGroupFuture.class);
    Provider<ChannelFactory> channelFactoryProvider = mock(Provider.class);
    ChannelFactory channelFactory = mock(ChannelFactory.class);
    when(channelFactoryProvider.get()).thenReturn(channelFactory);
    when(channelGroup.close()).thenReturn(channelGroupFuture);
    InetSocketAddress serverAddress = mock(InetSocketAddress.class);
    NetworkManager networkManager = new NettyNetworkManager(
                                          serverBootstrapProvider, 
                                          channelFactoryProvider, channelGroup,
                                          serverAddress);
    try {
      networkManager.start(); 
    } catch(Exception e) {
      fail(e.toString());
    }
 
    verify(serverBootstrapProvider, times(1)).get();
    verify(serverBootstrap, times(1)).bind(serverAddress);
    verify(channelGroup, times(1)).add(serverBootstrap.bind(serverAddress));

    // Should only be called once by the NettyNetworkManager.  If the server
    // boostrap provider wasn't a mock we'd be passing it into that and be
    // expecting another call to get.
    verify(channelFactoryProvider, times(1)).get();
    verify(channelGroupFuture, never()).awaitUninterruptibly();
    verify(channelFactory, never()).releaseExternalResources();
    verify(channelGroup, never()).close();

    networkManager.stop();

    verify(channelGroupFuture, times(1)).awaitUninterruptibly();
    verify(channelFactory, times(1)).releaseExternalResources();
    verify(channelGroup, times(1)).close();
  }
}
