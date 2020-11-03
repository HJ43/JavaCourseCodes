package io.github.kimmking.gateway.filter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * 过滤http请求，如果是Post就返回错误信息，get请求就通过
 */
public class HttpHeaderRequestFilter implements HttpRequestFilter {

    @Override
    public void filter(FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
        HttpMethod method = fullRequest.method();
        HttpHeaders headers = fullRequest.headers();
        headers.add("nio", "huangjian");
    }
}
