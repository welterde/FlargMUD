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
package com.difficultology.flargmud.network.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelFactory;
import com.google.inject.Provider;
import com.google.inject.Inject;

public class ServerBootstrapProvider implements Provider<ServerBootstrap> {
  /**
   * The server bootstrap singleton that will be created(if not already 
   * created) and returned when get is called.
   */
  private ServerBootstrap serverBootstrap;

  /**
   * To get the channel factory when needed.
   */
  private final Provider<ChannelFactory> channelFactoryProvider;

  /**
   * To get the channel pipeline factory when needed.
   */
  private final Provider<ChannelPipelineFactory> channelPipelineFactoryProvider;

  @Inject
  public ServerBootstrapProvider(Provider<ChannelFactory>
                                   channelFactoryProvider, 
                                 Provider<ChannelPipelineFactory>
                                   channelPipelineFactoryProvider) {
    this.channelFactoryProvider = channelFactoryProvider;  
    this.channelPipelineFactoryProvider = channelPipelineFactoryProvider;
  }

  public ServerBootstrap get() {
    if(serverBootstrap==null) {
      serverBootstrap = new ServerBootstrap(channelFactoryProvider.get());
      serverBootstrap.setPipelineFactory(channelPipelineFactoryProvider.get());
    }

    return serverBootstrap;
  }
}
