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

import java.util.logging.Logger;

import junit.framework.*;

import com.difficultology.flargmud.network.NetworkManager;

import static org.mockito.Mockito.*;

public class FlargMUDServerTest extends TestCase {
  /**
   * Check the start and stop methods.
   */
  public void testStartAndStop() {
    NetworkManager networkManager = mock(NetworkManager.class);
    Logger logger = mock(Logger.class);
    FlargMUDServer server = new FlargMUDServer(networkManager, logger);

    try {
      server.start();
    } catch(Exception e) {
      fail(e.toString());
    }

    server.stop();

    // Make sure the network manager is only started and stopped exactly once.
    try {
      verify(networkManager, times(1)).start();
      verify(networkManager, times(1)).stop();
    } catch(Exception e) {
      fail(e.toString());
    }
  }
}
