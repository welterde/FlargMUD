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
import java.util.concurrent.Executors;
import java.util.HashMap;

import static org.jboss.netty.channel.Channels.*;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;

public class NettyNetworkManager extends SimpleChannelUpstreamHandler implements NetworkManager {
  /**
   * The hash map for getting user channels for netty channels.
   */ 
  private HashMap<Channel, UserChannel> nettyChannelToUserChannel = 
    new HashMap<Channel, UserChannel>();

  /**
   * The server bootstrap for setting up the network connections.
   */
  private ServerBootstrap bootstrap;
 
  /**
   * The channel factory.
   */
  private ChannelFactory channelFactory;

  /**
   * A group of channels to use to close all channels later.
   */
  private ChannelGroup allChannels = new DefaultChannelGroup("all");
  
  /**
   * The port the server is to use.
   */
  private int port;

  /**
   * @param port the port for the server socket to use.
   */
  @Inject
  public NettyNetworkManager(@Named("Server Port") int port) {
    this.port = port;
  }

  /**
   * Handle a message received from the given user.  
   * @param uc      the user sending the message
   * @param message the message being sent from the user
   */
  public synchronized void receivedMessage(UserChannel uc, String message) {
    // The code in this function is for testing purposes.
    if(message.equals("quit")) {
      uc.disconnect(); 
    } else if(message.equals("shutdown")) {
      uc.sendMessage("Server going down... NOW!");
      stop();
    } else {
      uc.sendMessage(message+"\n");
    }
  }

  /** 
   * Called when the given user connects to the server.
   * @param uc the user connecting to the server
   */
  public synchronized void onConnect(UserChannel uc) { 
  }
  
  /**
   * Not needed for the Netty based network manager as it launches threads.
   */
  public void tick() {
  }

  /**
   * @return whether a call to tick is needed
   */
  public boolean needsTick() {
    return false;
  }

  /**
   * Start the network manager.
   */
  public void start() throws Exception {
    channelFactory = 
      new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                                        Executors.newCachedThreadPool());
    bootstrap = new ServerBootstrap(channelFactory);

    final ChannelHandler handler = this;
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
        public ChannelPipeline getPipeline() throws Exception {
          ChannelPipeline pipeline = pipeline();
          pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
          pipeline.addLast("decoder", new StringDecoder());
          pipeline.addLast("encoder", new StringEncoder());
          pipeline.addLast("handler", (ChannelHandler)handler);
          return pipeline;
        }
      });

    allChannels.add(bootstrap.bind(new InetSocketAddress(port)));      
  }
 
  /**
   * Stop the network manager, closing all connections and cleaning up all
   * resources.
   */ 
  public void stop() {
    ChannelGroupFuture future = allChannels.close();
    future.awaitUninterruptibly();
    channelFactory.releaseExternalResources();
  }

  /**
   * Get the user channel for the netty channel.
   * @param c the netty channel to get the user channel for
   * @return  the user channel for the given netty channel
   */
  private UserChannel getUserChannel(Channel c) {
    UserChannel uc = nettyChannelToUserChannel.get(c); 
    if(uc!=null) 
      return uc;

    uc = new NettyUserChannel(c, this);
    nettyChannelToUserChannel.put(c, uc);
    return uc;
  }

  /**
   * A netty function for dealing with users as they connect.
   * @param ctx 
   * @param e
   */
  public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    Channel c = e.getChannel();
    allChannels.add(c);
    onConnect(getUserChannel(c));
  }

  /**
   * A netty function for dealing with messages received.
   * @param ctx
   * @param e
   */
  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
    Channel c = e.getChannel();
    receivedMessage(getUserChannel(c), 
      (String)e.getMessage());
  }

  /**
   * Remove the user channel from the hash map.
   * @param uc the user channel to remove 
   */
  public void disconnect(UserChannel uc) {
    nettyChannelToUserChannel.remove(uc);
  }

  /**
   * Deal with exceptions caught by netty.
   * @param ctx
   * @param e
   */  
  public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
  }
}
