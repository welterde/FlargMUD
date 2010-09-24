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
package com.difficultology.flargmud.service;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.difficultology.flargmud.network.NetworkManager;

/**
 * The FlargMUD server class for running your own FlargMUD server using the
 * dependencies you want to use and configured just the way you want it to be. 
 */
public class FlargMUDServer implements Service {
  /**
   * The server's network manager.
   */
  private NetworkManager networkManager;

  /**
   * The logger to use for logging server related information.
   */
  private final Logger log = LoggerFactory.getLogger(FlargMUDServer.class); 

  /**
   * The FlargMUD server constructor.  Doesn't really do anything at the
   * moment.  Possible things to put in here would be a database access 
   * interface that could be implemented for all sorts of databases(fake,
   * flat file, NoSQL, SQL, etc), network manager interface for managing 
   * network connections to clients that could be implemented in various ways
   * such as one that does it through netty and another than manages it 
   * through the manual way of going about it with a selector.  With the 
   * network manager interface there would have to be as well an interface that
   * could pose as the way to communicate with clients(get their messages, 
   * send messages back to them, close the connection) and be implemented 
   * without much pain for netty or other ways.  Other things that will be 
   * needed would be for Room/Zone graph management and traversal, Player, NPC,
   * Item, Rank, Spell/Ability, Quest, Combat, Event, Command and anything 
   * else I can think of.  
   */
  @Inject
  public FlargMUDServer(NetworkManager networkManager) {
    this.networkManager = networkManager;
  }

  /**
   * This method will block once called since the server will not start its own
   * thread.  One could create a thread for it and run it in that if they so
   * choose to.  This server is single threaded as well, it will not be 
   * creating any threads.
   * @throws nothing yet, but once it starts doing stuff this can happen
   */
  public void start() throws Exception {
    log.info("Server starting!");
    networkManager.start();
  }

  /**
   * Requests immediate shutdown of the service.
   */
  public void stop() {
    log.info("Stopping server!");
    networkManager.stop();
  }
}
