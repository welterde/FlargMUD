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
package com.difficultology.flargmud;

import java.net.InetSocketAddress;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import com.difficultology.flargmud.service.Service;
import com.difficultology.flargmud.service.FlargMUDServer;
import com.difficultology.flargmud.network.NetworkManager;
import com.difficultology.flargmud.network.netty.NettyNetworkManager;
import com.difficultology.flargmud.network.netty.NettyChannelPipelineFactory;
import com.difficultology.flargmud.network.netty.ServerBootstrapProvider;
import com.difficultology.flargmud.network.netty.ChannelFactoryProvider;
import com.difficultology.flargmud.network.netty.ChannelPipelineFactoryProvider;

public class FlargMUDModule extends AbstractModule {
  /**
   * Configure method for deciding what to inject for each type of class.
   */
  @Override 
  protected void configure() {
    bind(Service.class).to(FlargMUDServer.class).in(Singleton.class);
    bind(NettyNetworkManager.class).in(Singleton.class);
    bind(NetworkManager.class).to(NettyNetworkManager.class);
    bind(ChannelHandler.class).to(NettyNetworkManager.class);
    bind(ChannelFactory.class).toProvider(ChannelFactoryProvider.class);
    bind(ChannelPipelineFactory.class).toProvider(ChannelPipelineFactoryProvider.class);
    bind(ServerBootstrap.class).toProvider(ServerBootstrapProvider.class);
    bind(ChannelGroup.class).toInstance(new DefaultChannelGroup("main"));
    bind(InetSocketAddress.class).toInstance(new InetSocketAddress(8080));
  }
}
