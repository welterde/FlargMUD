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

import com.difficultology.flargmud.network.UserChannel;

import org.jboss.netty.channel.Channel;

public class NettyUserChannel implements UserChannel {
  /**
   * The netty channel this user channel wraps around.
   */
  private Channel c;

  /**
   * The netty network manager to send information when the user is to be
   * disconnected.
   */
  private NettyNetworkManager nettyNetworkManager;

  /**
   * @param c the netty channel to have this user channel wrap around
   * @param nettyNetworkManager the netty network manager to have handle a 
   * disconnect event.
   */
  public NettyUserChannel(Channel c, NettyNetworkManager nettyNetworkManager) {
    this.c = c; 
    this.nettyNetworkManager = nettyNetworkManager;
  }

  /**
   * Send the given message to the user.
   * @param message the message to be sent
   */
  public void sendMessage(String message) {
    c.write(message);  
  }

  /**
   * Disconnect the user from the server for whatever reason.
   */
  public void disconnect() {
    c.close();
    nettyNetworkManager.disconnect(this);
  }
}
