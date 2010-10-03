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

import com.google.inject.Provider;
import com.google.inject.Inject;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelHandler;

public class ChannelPipelineFactoryProvider implements Provider<ChannelPipelineFactory> {
  /**
   * The channel handler for new clients.
   */
  private ChannelHandler channelHandler;

  /**
   * The factory for creating channel pipelines for new clients.
   */
  private ChannelPipelineFactory channelPipelineFactory;

  @Inject
  public ChannelPipelineFactoryProvider(ChannelHandler channelHandler) {
    this.channelHandler = channelHandler;
  }

  public ChannelPipelineFactory get() {
    if(channelPipelineFactory==null) {
      channelPipelineFactory = new NettyChannelPipelineFactory(channelHandler);    
    }

    return channelPipelineFactory;
  }
}
