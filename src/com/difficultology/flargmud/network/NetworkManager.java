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

/**
 * A network manager is something that manages network connections for the 
 * server.
 */
public interface NetworkManager {
  /**
   * Handle a message received from the given user.  This might need to be 
   * synchronized.
   * @param uc      the user sending the message
   * @param message the message being sent from the user
   */
  public void receivedMessage(UserChannel uc, String message);

  /** 
   * Called when the given user connects to the server.
   * @param uc the user connecting to the server
   */
  public void onConnect(UserChannel uc);

  /**
   * Needed for network managers that don't launch an additional thread, so
   * that this method call goes through and gets new messages and users
   * sending those to onConnect and messageReceived for handling.
   */
  public void tick();

  /**
   * @return whether a call to tick is needed
   */
  public boolean needsTick();

  /**
   * Start the network manager.
   */
  public void start() throws Exception;
 
  /**
   * Stop the network manager, closing all connections and cleaning up all
   * resources.
   */ 
  public void stop();
}
