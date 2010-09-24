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

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.difficultology.flargmud.service.Service;
import com.difficultology.flargmud.service.FlargMUDServer;
import com.difficultology.flargmud.network.NetworkManager;
import com.difficultology.flargmud.network.NettyNetworkManager;

public class FlargMUDModule extends AbstractModule {
  /**
   * Configure method for deciding what to inject for each type of class.
   */
  @Override 
  protected void configure() {
    bind(Service.class).to(FlargMUDServer.class);
    bind(NetworkManager.class).to(NettyNetworkManager.class).in(Singleton.class);
    bindConstant().annotatedWith(Names.named("Server Port")).to(8080);
  }
}
