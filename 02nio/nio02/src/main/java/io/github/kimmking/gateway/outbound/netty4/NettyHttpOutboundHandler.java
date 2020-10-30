package io.github.kimmking.gateway.outbound.netty4;

import io.github.kimmking.gateway.util.ByteBufToBytes;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.apache.http.util.EntityUtils;

import java.net.URI;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpOutboundHandler extends ChannelInboundHandlerAdapter {
    private ByteBufToBytes reader;
    private ChannelHandlerContext parentCtx;
    private int contentLength = 0;
    public NettyHttpOutboundHandler(ChannelHandlerContext ctx) {
        this.parentCtx = ctx;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        System.out.println("channelActive");
       /* URI uri = new URI("/api/hello");
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
        request.headers().add(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        request.headers().add(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
        ctx.writeAndFlush(request);*/
       DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, new URI("/api/hello").toASCIIString());
            // 构建http请求
            request.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
            request.headers().set(HttpHeaderNames.CONNECTION,
                    HttpHeaderNames.CONNECTION);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                    request.content().readableBytes());
        ctx.writeAndFlush(request);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println("channelRead");

        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            System.out.println("CONTENT_TYPE:"
                    + response.headers().get(HttpHeaderNames.CONTENT_TYPE));
            if (HttpUtil.isContentLengthSet(response)) {
                contentLength = (int) HttpUtil.getContentLength(response);
                reader = new ByteBufToBytes(contentLength);
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            reader.reading(content);
            content.release();
            byte[] bytes = reader.readFull();
            System.out.println(new String(bytes));
            if (reader.isEnd()) {
                FullHttpResponse response = null;
                try {
                    response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(bytes));
                    response.headers().set("Content-Type", "text/plain;charset=UTF-8");
                    response.headers().setInt("Content-Length", contentLength);
                } catch (Exception e) {
                    e.printStackTrace();
                    response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
                    exceptionCaught(parentCtx, e);
                } finally {
                    parentCtx.write(response);
                }
                parentCtx.flush();
                ctx.close();
            }
        }
    }
}