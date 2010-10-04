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

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.difficultology.flargmud.network.NetworkManager;
import com.difficultology.flargmud.network.UserChannel;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.Provider;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
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

import static org.jboss.netty.channel.Channels.*;

public class NettyNetworkManager extends SimpleChannelUpstreamHandler implements NetworkManager {
  /**
   * Has the server already been started?
   */
  private AtomicBoolean started = new AtomicBoolean(false);

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
  private ChannelGroup allChannels;
  
  /**
   * For getting the server bootstrap when we need it.
   */
  private final Provider<ServerBootstrap> serverBootstrapProvider;

  /**
   * To get the channel factory when we need it.
   */
  private final Provider<ChannelFactory> channelFactoryProvider;
 
  /**
   * What the server should listen on.
   */ 
  private InetSocketAddress serverAddress;
 
  /**
   * @param serverBootstrapProvider to get the server bootstrap when we need it
   * @param channelFactoryProvider  to get the channel factory when we need it
   * @param channelGroup            the channel group to use to manage channels
   * @param serverAddress           the address and port to listen on
   */
  @Inject
  public NettyNetworkManager( Provider<ServerBootstrap> 
                               serverBootstrapProvider,
                             Provider<ChannelFactory>
                               channelFactoryProvider,
                             ChannelGroup channelGroup, 
                             InetSocketAddress serverAddress) {
    this.serverBootstrapProvider = serverBootstrapProvider;
    this.channelFactoryProvider = channelFactoryProvider;
    this.allChannels = channelGroup;
    this.serverAddress = serverAddress;
  }

  /**
   * Handle a message received from the given user.  This method is NOT 
   * synchronized, as all it will do is pass the message onto a command 
   * parser for parsing and then whatever executes the commands has to be 
   * synchronized as executing two or more commands in parallel can lead to
   * race conditions pretty easily.  An example of such a condition would be
   * if two players both execute a command to pick up one item and both are
   * executed at the same time then who gets the item?  Command execution
   * MUST be synchronized and done in a single thread to keep things simple.
   * @param uc      the user sending the message
   * @param message the message being sent from the user
   */
  public void receivedMessage(UserChannel uc, String message) {
    // The code in this method is for testing purposes.
    if(message.equals("quit")) {
      uc.disconnect(); 
    } else if(message.equals("shutdown")) {
      uc.sendMessage("Server going down... NOW!");
      // If receivedMessage is being called then anything thrown by stop can be
      // ignored as it is either for sure running(this being called by netty)
      // or it is being tested with mock users and hypothetical messages being
      // thrown in.
      try {
        stop();
      } catch(IllegalStateException e) {}
    } else {
      uc.sendMessage(message+"\n");
    }
  }

  /** 
   * Called when the given user connects to the server.  I intend to have this
   * method call something from a login/registration system to take care of
   * the user who has just connected.  This method is NOT synchronized, so 
   * that system will need to make sure things are synchronized when needed.
   * Synchronization will eventually be needed to prevent two users from 
   * both logging in as the same user and prevent two users from both 
   * registering the same username.
   * @param uc the user connecting to the server
   */
  public void onConnect(UserChannel uc) { 
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
    if(!started.compareAndSet(false, true)) {
      throw new IllegalStateException("You already started the server!");   
    }

    bootstrap = serverBootstrapProvider.get();
    channelFactory = channelFactoryProvider.get();
    allChannels.add(bootstrap.bind(serverAddress));      
  }
 
  /**
   * Stop the network manager, closing all connections and cleaning up all
   * resources.
   */ 
  public void stop() {
    started.set(false);

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
