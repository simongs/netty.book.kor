/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.nettybook.ch1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.  Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public final class EchoClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();

            // 서버 쪽과는 다르게 이벤트 루프 그룹이 하나만 설정됨
            // 서버와는 달리 서버에 연결된 채널 하나만 존재하기 떄문에 이벤트 루프 그룹이 하나이다
            b.group(group)
             .channel(NioSocketChannel.class) // 채널의 종류 NIO 소켓 채널
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast(new EchoClientHandler());
                 }
             });
            // 비동기 입출력 메소드인 connect를 호출한다
            // connect 메소드는 메소드의 호출 결과로 ChannelFuture 객체를 돌려준다
            // 이 객체를 통해서 비동기 메소드의 처리결과를 확인할 수 있다
            // ChannelFuture 객체의 sync()는 ChannelFuture 객체의 요청이 완료될 때까지 대기한다
            // 요청이 실패하면 예외를 던진다
            // 즉 connect 메소드의 처리가 완료될 때까지 다음라인으로 진행하지 않는다.
            ChannelFuture f = b.connect("localhost", 8888).sync();

            f.channel().closeFuture().sync();
        }
        finally {
            group.shutdownGracefully();
        }
    }
}

